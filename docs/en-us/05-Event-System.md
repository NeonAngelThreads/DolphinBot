> [!NOTE] Previous · [04-LoginStateMachine](./04-LoginStateMachine.md) · [PluginDocs](../PluginDocs.md) · [Next](./06-Packet-Listeners.md)

# 5. Event System

DolphinBot provides a Bukkit-style event system: annotate methods with `@EventHandler`, register the listener, and the framework dispatches matching events at the appropriate priority.

## `@EventHandler` annotation

```java
@EventHandler(EventPriority.HIGHEST)
public void onPlayerMove(PlayerMoveEvent event) {
    // handles PlayerMoveEvent at highest priority
}

@EventHandler  // default = EventPriority.NORMAL
public void onChat(PlayerChatEvent event) { ... }
```

### Six priority levels

| Priority | Relative position |
|----------|-------------------|
| `LOWEST`  | First to see the event |
| `LOW`     | |
| `NORMAL`  | Default |
| `HIGH`    | |
| `HIGHEST` | Second-to-last |
| `MONITOR` | Last — for monitoring only |

## Implementing `IListener`

Option A — separate listener class (cleaner when you have many handlers):

```java
import org.angellock.impl.events.IListener;
import org.angellock.impl.events.annotations.EventHandler;
import org.angellock.impl.api.events.game.PlayerMoveEvent;

public class MyListener implements IListener {
    @EventHandler(EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        var player = event.getPlayer();
        System.out.println(player.getName() + " moved!");
    }

    @EventHandler(EventPriority.LOW)
    public void onChat(PlayerChatEvent event) {
        System.out.println("chat: " + event.getMessage());
    }
}
```

Option B — plugin class itself (convenient for one-off handlers):

```java
public class MyPlugin extends AbstractPlugin implements IListener {
    @Override
    public void onEnable(RobotPlayer bot) {
        getEvents().registerListeners(this); // self as listener
    }

    @EventHandler
    public void onEntitySpawn(EntityEmergedEvent event) {
        System.out.println("entity emerged: " + event.getEntity());
    }
    // ...
}
```

## Registration

```java
@Override
public void onEnable(RobotPlayer bot) {
    getEvents().registerListeners(new MyListener());
    // or
    getEvents().registerListeners(this);
}
```

## Built-in events

| Event | Triggered when… |
|-------|-----------------|
| `PlayerMoveEvent` | A nearby player moves |
| `EntityEmergedEvent` | An entity spawns (player, projectile, etc.) |
| `PlayerChatEvent` | A player sends an in-game message |
| `MessageBroadcastEvent` | Another bot sends a cross-bot message |

> [!NOTE] Rules of thumb
> - Every handler method **must** be `public`
> - Annotate every handler with `@EventHandler`
> - Always register listeners — unregistered listeners are silently ignored

## Event vs packet listener

Both work, but they're different layers:

| Layer | What you see | Use when… |
|-------|-------------|-----------|
| `@EventHandler` events | Semantic objects (`PlayerMoveEvent`, `EntityEmergedEvent`) | You care about **what happened** |
| Packet listeners | Raw MCProtocolLib packets (`ClientboundEntityPositionSyncPacket`, …) | You care about **the wire format** |

For most plugins, prefer `@EventHandler`. Fall back to packet listeners only when you need fields that the event wrapper doesn't expose.
