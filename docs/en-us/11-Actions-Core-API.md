> [!NOTE] Previous · [10-Architecture](./10-Actions-Core-Architecture.md) · [PluginDocs](../PluginDocs.md) · [Next](./12-Actions-Core-World.md)

# 11. Actions-Core — API

The four types in this page are the backbone of every queued action and every plugin that extends Actions-Core. Master them and you'll understand 90% of the codebase.

## `ActionsModule`

```java
public interface ActionsModule {
    String getModuleName();
    void onEnable(ModuleContext context);
    void onLogin(ModuleContext context);  // called after ClientboundLoginPacket arrives
    void onDisable(ModuleContext context);
}
```

Each module is a **self-contained functional unit**. The contract:

| Rule | Enforcement |
|------|-------------|
| Modules must not reference each other directly | Compile-time (no cross-imports) |
| Modules get the same `ModuleContext` as queued actions | Runtime (wiring in `ActionsCorePlugin`) |
| `onLogin` fires once after spawn | Framework callback |

> [!TIP] Why a no-op `onLogin`?
> Most modules don't need it. But some commands (like `/whereiam`) are meaningless before the bot has a spawn position. Override `onLogin` only when you need post-spawn state.

## `ModuleContext` — the per-bot wiring

```java
public final class ModuleContext {
    RobotPlayer      bot();        // the bot handle
    LoggerWrapper    logger();     // plugin logger
    PlayerActionSimulator simulator(); // aim, swing, interact, dig
    WorldCache       world();      // passive chunk + entity model
    InventoryCache   inventory();  // hotbar slots
    ActionQueue      queue();      // serial action executor
}
```

Every module and every queued action receives the **same `ModuleContext` instance** for a given bot. This is what makes queue.submit() safe from any thread — the context is per-bot and never swapped.

> [!IMPORTANT] Don't hold bot references outside context
> Always pass `ctx.bot()` when you need it. If you stash `RobotPlayer bot` in a field without context, you'll eventually hold a stale reference after `onDisable`.

## `Action` — one queued unit of work

```java
public abstract class Action {
    protected Action(String name);
    public abstract boolean run(ModuleContext ctx) throws InterruptedException;
    protected static void sleep(long millis) throws InterruptedException;
}
```

### Rules

| Rule | Why |
|------|-----|
| `run` **must** return `true` on success | `ActionQueue` logs `-> done` / `-> failed` based on this |
| `run` **may block/sleep freely** | Runs on the queue's daemon worker thread, not on the session thread |
| `run` **must** be responsive to interruption | The queue calls `Thread.interrupt()` during shutdown; caught `InterruptedException` propagates |

### Lifecycle

```text
ctx.queue().submit(new HarvestAction("stone", 10, 20, 30))
  ↓
queue logs: #5 queued: harvest(stone @ 10,20,30)
  ↓ (worker thread picks it up)
action.run(ctx)
  ├── walk to target (uses GotoAction → queue.submit → ...)
  ├── equip tool (uses ctx.inventory())
  ├── dig via simulator (START_DIGGING + sleep + FINISH_DIGGING)
  └── return true
  ↓
queue logs: harvest(...) -> done
```

> [!WARNING] Nested `queue.submit()`
> Actions may submit other actions (e.g., `HarvestAction` calls `new GotoAction(...).run(ctx)` directly, **not** through `queue.submit`). This is intentional — the inner action must complete before the outer one can proceed. Use direct `.run()` for sequential dependencies; use `queue.submit` only when the two jobs are truly independent.

## `ActionQueue` — serial executor

```java
public class ActionQueue {
    long submit(Action action);     // returns sequence number; never blocks the caller
    int pending();                  // items in queue right now
    void shutdown();                // stops worker, clears queue
}
```

### Why a serial queue?

Minecraft protocol interactions **cannot** be concurrent. Two plugins racing to `RobotPlayer` would produce:

- Interleaved `START_DIGGING` / `FINISH_DIGGING` packets → nothing breaks
- Stale position when a move and a dig fire simultaneously → wrong face, wrong slot
- Corrupted interaction sequences

The single daemon worker guarantees **one action at a time** per bot. Any plugin, any thread can `queue.submit()` — the queue is a `LinkedBlockingQueue` with a single consumer.

### Log format

```
[ActionQueue] &2#1 queued: &7whereiam
[ActionQueue] &7whereiam &r-> &2done
[ActionQueue] &2#2 queued: &7goto(-45,117,-544)
[ActionQueue] &7goto(-45,117,-544) &r-> &2done
```

The `&2` / `&4` / `&7` are `ConsoleTokens` color codes (green for success, red for failure, gray for neutral).

## Pattern: module → executor → action

Every built-in command follows this triangle:

```text
BlockInteractModule.onEnable(ctx)
  └─ registerCommand(name="harvest", build(new HarvestExecutor(ctx)))
       └─ HarvestExecutor.onCommand(response, bot)
            └─ ctx.queue().submit(new HarvestAction(blockName, x, y, z))
```

The **executor** parses CLI args and constructs the **action** with typed parameters. The **action** does the real work via `ctx`. Modules own the registrations; actions own the semantics; executors are the glue.
