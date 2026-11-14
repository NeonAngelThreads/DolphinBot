> [!NOTE] Previous · [13-Registries](./13-Actions-Core-Registries.md) · [PluginDocs](../PluginDocs.md)

# 14. Actions-Core — Built-In Commands

Six terminal commands ship with Actions-Core. They're registered by their owning modules and execute asynchronously through `ActionQueue`.

## Command list

| Command | Arguments | Owner Module | What it does |
|---------|-----------|--------------|--------------|
| `/whereiam` | — | MovementModule | Logs world, position, rotation, block below feet |
| `/goto` | `<x> <y> <z>` | MovementModule | Pathfinds to the target; walks, jumps, climbs as needed |
| `/harvest` | `<block> [x y z]` | BlockInteractModule | Finds (or takes) the target block, equips best tool, digs with START/FINISH |
| `/place` | `<block> <x> <y> <z>` | BlockInteractModule | Switches to the item in hotbar, right-clicks to place |
| `/interact` | `<x> <y> <z> [block]` or `<block>` | BlockInteractModule | Right-clicks — interactive blocks (doors, buttons, levers…) respond |
| `/collect` | `<item>` | MovementModule | Finds nearest dropped item entity, walks to it, waits for pickup |

## Usage examples

```
/whereiam
  [whereiam] world=minecraft:overworld dimension=0
             pos=(-46.56, 117.00, -544.49)
             block=(-47, 117, -545) rot=(yaw 162.7, pitch 33.9)

/goto -45 117 -544
  [goto] path found: 4 waypoints
  [goto] arrived at (-45, 117, -544)

/harvest polished_diorite           ← nearest matching block (within 16)
/harvest polished_diorite -47 117 -545  ← exact coordinates

/place stone -45 117 -543           ← stone must be in hotbar
  [place] 'stone' not found in hotbar    ← helpful error
  [place] target out of reach (5.85 blocks)  ← try /goto first

/interact oak_trapdoor               ← nearest within ±6, must be reachable
/interact -46 117 -543              ← unconditional right-click
/interact -46 117 -543 minecraft:oak_trapdoor  ← right-click + confirm target

/collect minecraft:oak_button       ← walks to nearest dropped entity
  [ActionQueue] #12 queued: collect(minecraft:oak_button)
  [ActionQueue] #12 collect(minecraft:oak_button) -> done
```

## The Executor pattern

Each command follows the same three-layer pattern:

```java
// 1) EXECUTOR — parses CLI args, constructs action with typed params
public class HarvestExecutor implements ICommandExecutor {
    private final ModuleContext ctx;

    public HarvestExecutor(ModuleContext ctx) { this.ctx = ctx; }

    @Override
    public void onCommand(CommandResponse r, RobotPlayer bot) throws Exception {
        String[] a = r.getCommandList();
        if (a.length >= 5) {
            int[] c = CoordinateUtil.coordsAt(a, 2);
            ctx.queue().submit(new HarvestAction(a[1], c[0], c[1], c[2]));
        } else if (a.length >= 2) {
            ctx.queue().submit(new HarvestAction(a[1], null, null, null));
        }
    }
}

// 2) MODULE — registers terminal command with framework
public class BlockInteractModule implements ActionsModule {
    @Override
    public void onEnable(ModuleContext ctx) {
        ActionsCorePlugin.getCommandManager().registerCommand(
            new TerminalCommand.Builder()
                .withName("harvest")
                .withDescription("Dig a block with the right tool.")
                .withUsage("/harvest <block> [x y z]")
                .withProvider(this.provider)
                .build(new HarvestExecutor(ctx)));     // ← inject ctx
    }
}
```

> [!IMPORTANT] Don't use `ActionsCorePlugin.getContext()` directly
> The `ModuleContext` is passed into `module.onEnable(ctx)`. Pass it down to executors as a constructor argument — don't reach back to the static plugin getter. It makes the dependency explicit and avoids subtle initialization-order bugs.

## Why queue-based?

Commands don't run on the terminal thread. They `queue.submit()` and the daemon worker picks them up — which means:

- **No blocking on the console** — terminal remains responsive even during a 15-second pathfind
- **Race-free with other plugins** — if another plugin also calls `ctx.queue().submit(...)` from an event handler, both execute one at a time
- **Progress logged** — every action gets `#N queued: name` → `-> done` / `-> failed` lines

```
[ActionQueue] #3 queued: whereiam
[ActionQueue] #3 whereiam -> done
[ActionQueue] #4 queued: goto(-45,117,-544)
[ActionQueue] #5 queued: harvest(polished_diorite @ -47,117,-545)   ← queued while #4 is running
[ActionQueue] #4 goto(...) -> done
[ActionQueue] #5 harvest(...) -> done
```

## Registering your own command

1. Write `MyExecutor implements ICommandExecutor`
2. Write `MyAction extends Action` (or reuse an existing one)
3. Pick a module — add the registration in `module.onEnable(ctx)`
4. Build — done

```java
@Override
public void onEnable(ModuleContext ctx) {
    ActionsCorePlugin.getCommandManager().registerCommand(
        new TerminalCommand.Builder()
            .withName("mycmd")
            .withDescription("Does something cool.")
            .withUsage("/mycmd <arg>")
            .withProvider(this.provider)
            .build((r, b) -> ctx.queue().submit(new MyAction(...))));
}
```
