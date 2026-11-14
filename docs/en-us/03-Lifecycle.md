> [!NOTE] Previous · [02-Quick-Start](./02-Quick-Start.md) · [PluginDocs](../PluginDocs.md) · [Next](./04-LoginStateMachine.md)

# 3. Plugin Lifecycle

Every plugin instance follows the same three-phase lifecycle. Understanding it is the single most important thing to get right.

## State machine

```
┌──────────┐   onLoad()   ┌────────────┐  onEnable(bot)  ┌────────────┐  onDisable()  ┌─────────┐
│ NOT LOAD ├──────────────►│  LOADED    ├──────────────────►│  ENABLED   ├──────────────►│ DISABLED│
└──────────┘              └────────────┘                 └────────────┘               └─────────┘
                                │                              ▲
                                │  plugin is                   │  same bot profile
                                │  added later                 │  joins again
                                └──────────────────────────────┘
```

## Three hooks

| Hook | When it runs | What's available | Typical use |
|------|-------------|------------------|-------------|
| `onLoad()` | Once when the JAR is first discovered | Only plugin metadata — **no bot yet** | Register shared SPI implementations |
| `onEnable(RobotPlayer bot)` | **Once per bot profile** that joins a server | `bot` handle, `getListeners()`, `getCommands()`, `getLogger()` | Register packet listeners, commands, state machines |
| `onDisable()` | When the bot disconnects or shuts down | Same as onEnable but bot may already be disconnected | Shutdown queues, clean up state |

## `onLoad()` — the singleton phase

`onLoad()` is called **once per JAR, not once per bot**. This makes it the right place to:

- Register global SPI implementations (e.g., `RobotPlayer.registerActionSimulator(...)`)
- Cache immutable data (config parsing, registry lookups)
- Wire plugin-wide singletons that don't depend on a specific bot

```java
@Override
public void onLoad() {
    // Called once — good for singleton setup
    RobotPlayer.registerActionSimulator(this.simulator);
    this.commandManager = getTerminalCommands();
}
```

> [!WARNING] No bot in `onLoad`
> `RobotPlayer`, `getListeners()`, `getCommands()` are **not meaningful** yet. Don't try to subscribe to packet events here — there is no session.

## `onEnable(bot)` — per-bot wiring

`onEnable` runs once every time a bot profile joins a server. Everything that needs packet-level interaction with a **specific bot** goes here:

```java
@Override
public void onEnable(RobotPlayer bot) {
    // Per-bot: wire listeners + commands + state machines
    var worldCache = new WorldCache();
    getListeners().add(worldCache);
    getListeners().add(new LoginHandler().addExtraAction(pkt -> ...));

    getTerminalCommands().registerCommand(new TerminalCommand.Builder()
        .withName("hello")
        .withProvider(this)
        .build((r, b) -> getLogger().info("hi!")));
}
```

## `onDisable()` — cleanup

Called when the bot disconnects (kick, server stop, manual shutdown). The most common cleanup is shutting down serial executors:

```java
@Override
public void onDisable() {
    if (this.context != null) {
        this.context.queue().shutdown();
    }
    getListeners().clear();
}
```

> [!WARNING] `onDisable` is **not** called twice
> If a plugin throws during its own `onDisable`, that call is swallowed by the framework. Guard critical cleanup with try/catch or do it idempotently.

## Per-bot vs global: the mental model

| Scope | Mechanism | Example |
|-------|-----------|---------|
| **Global (per JAR)** | `onLoad()` fields, static state | SPI registrations, config, shared registries |
| **Per bot** | `onEnable(bot)` creates new state | `ActionQueue`, `WorldCache`, `InventoryCache` |

**Rule of thumb:** if your data depends on `bot.getPosition()`, `bot.getSession()`, or the player profile name — it's per-bot. If it's a shared constant or a class that doesn't care which bot is running — it's global.

> [!TIP] Actions-Core as a reference
> See `ActionsCorePlugin.onLoad()` vs `onEnable()` in the source for a clean example of this split.

## Login timing

There are two separate moments inside `onEnable`:

1. **Session opened** — `onEnable` is called as soon as the TCP connection is established
2. **Login completed** — the bot has received `ClientboundLoginPacket` and spawned

If your plugin needs to know the bot's dimension, game mode, or spawn position, subscribe a `LoginHandler` in `onEnable` and listen for the first `ClientboundLoginPacket`.

```java
@Override
public void onEnable(RobotPlayer bot) {
    getListeners().add(new LoginHandler().addExtraAction(loginPacket -> {
        var spawn = loginPacket.getCommonPlayerSpawnInfo();
        getLogger().info("Dimension: " + spawn.getDimension());
    }));
}
```
