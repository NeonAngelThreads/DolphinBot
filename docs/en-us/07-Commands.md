> [!NOTE] Previous · [06-Packet-Listeners](./06-Packet-Listeners.md) · [PluginDocs](../PluginDocs.md) · [Next](./08-Chat-Message.md)

# 7. Commands

DolphinBot has **two parallel command systems** — understand the difference before you pick one.

## In-game commands (`getCommands()`)

Triggered by chat messages prefixed with `!` or `！` (Chinese exclamation). Each **player** types the command; only the bot sees it.

```java
@Override
public void onEnable(RobotPlayer bot) {
    getCommands().register(
        new Command.Builder()
            .withName("uid")
            .allowedUsers("Melibertan")        // optional whitelist; omit = everyone
            .build((response, botInst) -> {
                String[] args = response.getCommandList();  // ["uid", "arg1", ...]
                String sender = response.getSender();       // who typed it
                botInst.getSession().sendChat("Hi, " + sender + "!");
            })
    );
}
```

### API

| Method | Purpose |
|--------|---------|
| `.withName(String)` | Command trigger (without the `!` prefix) |
| `.allowedUsers(String...)` | Whitelist; absent means everyone can use it |
| `.build(ICommandExecutor)` | Terminal lambda `(CommandResponse, RobotPlayer) -> void` |

Every plugin gets its **own command namespace** — commands don't collide across plugins.

## Terminal commands (`getTerminalCommands()`)

Triggered by typing `/command` in the bot's **console** (not in-game). Used for operator-level control. This is the system Actions-Core uses for `/whereiam`, `/goto`, etc.

```java
@Override
public void onEnable(RobotPlayer bot) {
    getTerminalCommands().registerCommand(
        new TerminalCommand.Builder()
            .withName("whereiam")
            .withDescription("Show current world and coordinates.")
            .withUsage("/whereiam")
            .withProvider(this)
            .build((r, b) -> {
                var pos = b.getPosition();
                getLogger().info("pos=({}, {}, {})", pos.getX(), pos.getY(), pos.getZ());
            })
    );
}
```

### API

| Method | Purpose |
|--------|---------|
| `.withName(String)` | Command trigger (without the `/` prefix) |
| `.withDescription(String)` | Shown in help output |
| `.withUsage(String)` | Shown when arguments are missing |
| `.withProvider(this)` | Points to your plugin for ownership |
| `.build(ICommandExecutor)` | Terminal lambda `(CommandResponse, RobotPlayer) -> void` |

> [!TIP] Actions-Core Executor pattern
> Actions-Core wraps the lambda body into separate `*Executor` classes that take a `ModuleContext` in their constructor. This keeps commands thin and makes them testable. See [Actions-Core Commands](./14-Actions-Core-Commands.md).

## When to pick which?

| You need... | Use |
|-------------|-----|
| Players to trigger bot actions in-game | `getCommands()` + `!cmd` |
| Console operator to trigger bot actions | `getTerminalCommands()` + `/cmd` |
| Both | Register both — they're independent systems |

## `CommandResponse` helpers

```java
String[] args = response.getCommandList();  // always has at least one element (the command name)
String sender = response.getSender();       // player name, or "terminal"
```

The command serializer strips the prefix (`!` / `！` / `/`) and splits by whitespace. If the player types `/goto 10 20 30`, the executor receives `["goto", "10", "20", "30"]`.
