> [!NOTE] Previous · [08-Chat-Message](./08-Chat-Message.md) · [PluginDocs](../PluginDocs.md) · [Next](./10-Actions-Core-Architecture.md)

# 9. Player Entity API

`RobotPlayer` tracks every player entity within server simulation distance. Use `getOnlinePlayers()` to inspect them.

## Getting players

```java
@Override
public void onEnable(RobotPlayer bot) {
    bot.getOnlinePlayers().forEach((entityId, player) -> {
        getLogger().info("Player " + player.getName() + " at " + player.getPosition());
    });
}
```

Each `Player` object exposes:

| Method | Returns |
|--------|---------|
| `.getName()` | Player name |
| `.getPosition()` | Current `Position` (world coordinates) |
| `.getSession()` | Network session handle |

Position data is **continuously updated** from entity movement packets — you don't need to poll.

## Distance check

```java
IPlayer target = bot.getOnlinePlayers().get(someEntityId);
if (target != null) {
    double dist = target.getPosition().getDistance(bot.getPosition());
    if (dist < 7.0) {
        bot.getSession().disconnect("Player too close!");
    }
}
```

## Tracking movement

Combine with packet listeners to react when a player moves:

```java
@Override
public void onEnable(RobotPlayer bot) {
    getListeners().add(new AbstractEventProcessor<ClientboundEntityPositionSyncPacket>() {
        @Override protected boolean isTargetPacket(Packet p) {
            return p instanceof ClientboundEntityPositionSyncPacket;
        }
    }.addExtraAction(move -> {
        var player = bot.getOnlinePlayers().get(move.getId());
        if (player != null) {
            getLogger().info(player.getName() + " moved to " + player.getPosition());
        }
    }));
}
```

> [!TIP] Use built-in events when possible
> `PlayerMoveEvent` already abstracts entity-position updates. Use it unless you need fields that the event wrapper doesn't expose.

## When to use this

- Detecting nearby players for anti-cheat evasion
- Following / chasing specific players
- Gathering movement data for pathfinding tests
- Announcing player joins / leaves

> [!WARNING] Simulation distance
> `getOnlinePlayers()` only contains players within the server's entity-simulation distance (default ~128 blocks). Players further away are **not** in the map until the bot gets closer.
