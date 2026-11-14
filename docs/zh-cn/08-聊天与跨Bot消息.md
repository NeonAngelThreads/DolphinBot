> [!NOTE] 上一页 · [07-命令系统](./07-命令系统.md) · [PluginDocs_CN](../PluginDocs_CN.md) · [下一页](./09-玩家实体API.md)

# 8. 聊天与跨 Bot 消息

每个 `RobotPlayer` 有 `MessageManager` 负责发送聊天和命令。多 Bot 协调通过 `BotMessage` + `MessageBroadcastEvent` 完成。

## 发送聊天

```java
@Override
public void onEnable(RobotPlayer bot) {
    // 聊天消息
    bot.getMessageManager().sendChat("你好，服务器！");

    // 服务器命令
    bot.getMessageManager().sendCommand("/give @s stone 64");
}
```

> [!NOTE] 速率限制
> 未发送的消息和命令都会入队。框架有服务器节流——即使 tight loop 里狂 `sendChat` 也不会被踢出。延迟可在配置文件里调整。

## 跨 Bot 消息

Message API 让一个 Bot 向同一 DolphinBot 实例里的另一个 Bot 发送负载。

### 步骤一——接收方定义监听器

```java
import org.angellock.impl.api.events.MessageBroadcastEvent;

public class AlertListener implements IListener {
    @EventHandler
    public void onAlert(MessageBroadcastEvent event) {
        String payload = event.getMessage();
        log.info("收到警报: {}", payload);
    }
}
```

### 步骤二——注册

```java
@Override
public void onEnable(RobotPlayer bot) {
    getEvents().registerListeners(new AlertListener(), this);
}
```

### 步骤三——从另一个 Bot 发送

```java
// 发给指定 Bot
bot.getBotManager()
    .getBotByName("bot#1")
    .callHandleableEvent(new MessageBroadcastEvent("raid incoming at x=100"));

// 或广播给所有 Bot
entityBot.getBotManager().distributeMessage("服务器 5 分钟后重启");
```

## `UStringHelper`——混淆工具

某些服务器会屏蔽特定词或模式。`UStringHelper` 把 ASCII 转成视觉相似的 Unicode：

```java
String clean   = "TestText123 789";
String unicode = UStringHelper.forceUnicode(clean);
// 例如 "ＴｅｓｔＴｅｘｔ１２３　７８９"
```

> [!WARNING] 谨慎使用
> 重度审查（基于正则、相似度检测）仍可能识破。`forceUnicode` 最适用于已知服务器只屏蔽纯 ASCII 形式的场景。
