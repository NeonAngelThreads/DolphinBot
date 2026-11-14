> [!NOTE] Previous · [05-Event-System](./05-Event-System.md) · [PluginDocs](../PluginDocs.md) · [Next](./07-Commands.md)

# 6. Packet Listeners

When `@EventHandler` semantics aren't enough, `getListeners()` gives you direct access to raw MCProtocolLib packets flowing through the session. This is what `WorldCache`, `InventoryCache`, and every other packet-driven model inside Actions-Core uses.

## Adding a listener

```java
@Override
public void onEnable(RobotPlayer bot) {
    getListeners().add(
        new LoginHandler().addExtraAction(pkt -> {
            var spawn = pkt.getCommonPlayerSpawnInfo();
            getLogger().info("Joined dimension=" + spawn.getDimension());
        })
    );
}
```

`getListeners()` returns an `Iterable<AbstractEventProcessor>`. Each processor listens for a **specific packet type**, runs its default action, and optionally chains `.addExtraAction(...)` callbacks.

## Built-in processors

| Processor | Target packet | Typical use |
|-----------|--------------|-------------|
| `LoginHandler` | `ClientboundLoginPacket` | Read dimension, game mode, spawn |
| `JoinedGameHandler` | — | Per-joined-game hook |
| `SystemChatHandler` | `ClientboundSystemChatPacket` | Read server messages |
| `PlayerLogInfo.UpdateHandler` | — | Player list updates |
| `PlayerLogInfo.RemoveHandler` | — | Player list removals |

> [!TIP] Lambda vs named class
> `.addExtraAction` takes a lambda or an interface implementation. Use lambdas for one-liners; use a named class when the handler is long or needs fields.

## Writing a custom handler

Extend `AbstractEventProcessor<TargetPacket>` and override `isTargetPacket`:

```java
public class ChatLogHandler extends AbstractEventProcessor<ClientboundPlayerChatPacket> {
    @Override
    protected boolean isTargetPacket(Packet packet) {
        return packet instanceof ClientboundPlayerChatPacket;
    }
}
```

Register it the same way:

```java
getListeners().add(
    new ChatLogHandler().addExtraAction(pkt -> {
        getLogger().info("[chat] " + pkt.getContent());
    })
);
```

## Why not just listen to all packets?

You **could** write:

```java
getListeners().add(new AbstractEventProcessor<Packet>() {
    @Override protected boolean isTargetPacket(Packet packet) { return true; }
});
```

But you shouldn't — every packet would allocate an event wrapper. Target specific packet types; the framework dispatches efficiently.

## Actions-Core as reference

`WorldCache` and `InventoryCache` are both packet listeners. They subscribe to chunk, entity, and inventory packets and keep their state up-to-date with zero periodic polling. See [Actions-Core World](./12-Actions-Core-World.md) for the patterns.
