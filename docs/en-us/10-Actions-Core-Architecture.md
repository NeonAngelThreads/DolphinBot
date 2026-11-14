> [!NOTE] Previous · [09-Player-Entity](./09-Player-Entity.md) · [PluginDocs](../PluginDocs.md) · [Next](./11-Actions-Core-API.md)

# 10. Actions-Core — Architecture

Actions-Core is the **reference plugin** shipped with DolphinBot. It provides a fully modular interaction layer — terrain-aware pathfinding, block interaction, tool-aware digging, item pickup — all built as a regular plugin that you can extend, replace, or rip out.

## Why modular?

The project constraint is strict:

> [!IMPORTANT]
> Actions **must** use queue-based serial execution (race-free across plugins).
> All new functionality **must** live inside Actions-Core — nothing gets wired directly into the framework.
> Modules **must not** reference each other directly.

These constraints came from hard-won lessons: frame-by-frame protocol interactions break when two plugins race on the same `RobotPlayer`; tightly-coupled modules can't be tested or replaced in isolation.

## Layered architecture

```text
┌──────────────────────────────────────────────────────┐
│              Terminal Command System                 │
│  /whereiam  /goto  /harvest  /place  /interact  /collect  │
└──────────┬───────────────────────────────────────────┘
           │  Executor.onCommand → queue.submit(XxxAction)
           ▼
┌──────────────────────────────────────────────────────┐
│  ┌────────────────┐   ┌────────────────────────┐     │
│  │  Modules (3)   │   │      ActionQueue      │     │
│  │  Movement      │──►│  (LinkedBlockingQueue)│     │
│  │  BlockInteract │   │  + daemon worker      │     │
│  │  Aim           │   └───────────┬────────────┘     │
│  └────────────────┘               │ run on worker    │
│                                   ▼                  │
│  ┌──────────────────────────────────────────────┐   │
│  │          Actions (6 concrete)                │   │
│  │  WhereAmIAction  GotoAction  HarvestAction   │   │
│  │  PlaceAction  InteractAction  CollectAction  │   │
│  └──────────────┬───────────────────────────────┘   │
│                 │                                    │
│  ┌──────────────▼───────────────────────────────┐   │
│  │        VanillaActionSimulator (SPI)          │   │
│  │  aimAt  swing  interactBlock  useItem  dig    │   │
│  └──────────────┬───────────────────────────────┘   │
│                 │                                    │
│  ┌──────────────▼──────────┐  ┌────────────────────┐│
│  │   WorldCache (passive)  │  │ InventoryCache     ││
│  │  chunks + entities      │  │  hotbar slots      ││
│  └─────────────────────────┘  └────────────────────┘│
│  ┌─────────────────────────┐  ┌────────────────────┐│
│  │ BlockPropertiesRegistry │  │ ToolProperties     ││
│  │ JSON: block-properties  │  │ static: 30+ tools  ││
│  └─────────────────────────┘  └────────────────────┘│
└──────────────────────────────────────────────────────┘
                  ┌─────────────────┐
                  │  AbstractPlugin  │  (DolphinBot)
                  └─────────────────┘
```

## Package map

```text
org.dolphinbot.actionscore/
├── ActionsCorePlugin.java         ← entry point
├── api/
│   ├── ActionsModule.java         ← SPI interface
│   └── ModuleContext.java         ← per-bot wiring (bot, queue, world, inventory, simulator)
├── modules/
│   ├── AimModule.java             ← registers nothing currently
│   ├── BlockInteractModule.java   ← registers /interact /place /harvest
│   ├── MovementModule.java        ← registers /whereiam /goto /collect
│   └── executors/                 ← 6 ICommandExecutor classes (one per command)
├── action/
│   ├── Action.java                ← abstract queued unit of work
│   ├── ActionQueue.java           ← serial daemon worker
│   ├── Pathfinder.java            ← A* on WorldCache grid
│   └── actions/                   ← 6 concrete actions
├── world/
│   ├── WorldCache.java            ← packet-driven chunk + entity model
│   ├── InventoryCache.java        ← hotbar (SetContent + SetSlot)
│   ├── ChunkColumn.java           ← section-by-section chunk decoder
│   ├── BlockNames.java            ← ViaVersion global-id ↔ name
│   ├── BlockProperties.java       ← immutable record
│   ├── BlockPropertiesRegistry.java ← JSON-backed loader
│   └── ToolProperties.java        ← tool category + mining-speed registry
├── simulation/
│   └── VanillaActionSimulator.java ← implements RobotPlayer SPI
├── handlers/
│   └── SmokeHandler.java          ← post-login self-test
└── common/
    └── CoordinateUtil.java        ← CLI arg parsing
```

## Lifecycle inside the framework

```java
public class ActionsCorePlugin extends AbstractPlugin {

    @Override
    public void onLoad() {
        RobotPlayer.registerActionSimulator(this.simulator);  // SPI registration (once)
        commandManager = getTerminalCommands();
        modules.add(new AimModule(this));
        modules.add(new BlockInteractModule(this));
        modules.add(new MovementModule(this));
    }

    @Override
    public void onEnable(RobotPlayer bot) {
        WorldCache world = new WorldCache();                 // per-bot
        InventoryCache inv = new InventoryCache();           // per-bot
        context = new ModuleContext(bot, getLogger(), simulator, world, inv);

        modules.forEach(m -> m.onEnable(context));          // each module registers its commands

        getListeners().add(world);                           // packet subscriptions
        getListeners().add(inv);
    }

    @Override
    public void onDisable() {
        context.queue().shutdown();                          // stops daemon worker
        getListeners().clear();
    }
}
```

## Adding a new command

1. Create an `XxxExecutor implements ICommandExecutor` (takes `ModuleContext` in constructor)
2. Create an `XxxAction extends Action` (put the real logic here)
3. Pick a module — does it belong in `MovementModule`, `BlockInteractModule`, or a new one?
4. Register in `module.onEnable`:
   ```java
   commandManager.registerCommand(new TerminalCommand.Builder()
       .withName("xxx")
       .withDescription("...")
       .withUsage("/xxx ...")
       .withProvider(this.provider)
       .build(new XxxExecutor(context)));
   ```
5. Build the plugin — zero cross-references, zero boilerplate

> [!TIP] Copy from an existing executor
> `modules/executors/CollectExecutor.java` is the simplest one. Copy it and tweak the action type.

Jump to: [API](./11-Actions-Core-API.md) · [World Model](./12-Actions-Core-World.md) · [Registries](./13-Actions-Core-Registries.md) · [Commands](./14-Actions-Core-Commands.md)
