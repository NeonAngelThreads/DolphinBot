> [!NOTE] 上一页 · [08-聊天与跨Bot消息](./08-聊天与跨Bot消息.md) · [PluginDocs_CN](../PluginDocs_CN.md) · [下一页](./10-Actions-Core-架构.md)

# 9. 玩家实体 API

`RobotPlayer` 追踪服务器模拟距离内的所有玩家实体。用 `getOnlinePlayers()` 遍历。

## 获取玩家

```java
@Override
public void onEnable(RobotPlayer bot) {
    bot.getOnlinePlayers().forEach((entityId, player) -> {
        getLogger().info("玩家 " + player.getName() + " 在 " + player.getPosition());
    });
}
```

每个 `Player` 对象暴露：

| 方法 | 返回 |
|------|------|
| `.getName()` | 玩家名 |
| `.getPosition()` | 当前 `Position`（世界坐标） |
| `.getSession()` | 网络会话句柄 |

位置数据从实体移动包**持续更新**——不需要轮询。

## 距离检测

```java
IPlayer target = bot.getOnlinePlayers().get(someEntityId);
if (target != null) {
    double dist = target.getPosition().getDistance(bot.getPosition());
    if (dist < 7.0) {
        bot.getSession().disconnect("玩家太近！");
    }
}
```

## 追踪移动

结合包监听器在玩家移动时作出响应：

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
            getLogger().info(player.getName() + " 移动到 " + player.getPosition());
        }
    }));
}
```

> [!TIP] 优先用内置事件
> `PlayerMoveEvent` 已经封装好了实体位置更新。除非需要事件包装器没暴露的字段，否则直接用它。

## 适用场景

- 检测附近玩家（反作弊规避）
- 追踪特定玩家
- 收集移动数据做寻路测试
- 宣告玩家加入 / 离开

> [!WARNING] 模拟距离
> `getOnlinePlayers()` 只包含服务器实体模拟距离内的玩家（默认 ~128 格）。更远的玩家不会出现在 Map 里，直到 Bot 靠近。
