> [!NOTE] Previous · [07-Commands](./07-Commands.md) · [PluginDocs](../PluginDocs.md) · [Next](./09-Player-Entity.md)

# 8. Chat & Cross-Bot Messaging

Every `RobotPlayer` has a `MessageManager` for sending chat and commands. Cross-bot coordination goes through `BotMessage` + `MessageBroadcastEvent`.

## Sending chat

```java
@Override
public void onEnable(RobotPlayer bot) {
    // Chat message
    bot.getMessageManager().sendChat("Hello, server!");

    // Server command
    bot.getMessageManager().sendCommand("/give @s stone 64");
}
```

> [!NOTE] Rate limiting
> Every unsent message and command is placed into a queue. The framework respects server throttling — you won't get kicked for spam even if you `sendChat` in a tight loop. Configure the delay in the bot's config file.

## Cross-bot messaging

Message API lets one bot send a payload to another bot running under the same DolphinBot instance.

### Step 1 — define the listener on the receiver bot

```java
import org.angellock.impl.api.events.MessageBroadcastEvent;

public class AlertListener implements IListener {
    @EventHandler
    public void onAlert(MessageBroadcastEvent event) {
        String payload = event.getMessage();
        log.info("Received alert: {}", payload);
        // do something...
    }
}
```

### Step 2 — register it

```java
@Override
public void onEnable(RobotPlayer bot) {
    getEvents().registerListeners(new AlertListener(), this);
}
```

### Step 3 — send from another bot

```java
// Send to one bot by name
bot.getBotManager()
    .getBotByName("bot#1")
    .callHandleableEvent(new MessageBroadcastEvent("raid incoming at x=100"));

// Or broadcast to all bots
entityBot.getBotManager().distributeMessage("server restart in 5 min");
```

## `UStringHelper` — obfuscation

Some servers censor certain words or patterns. `UStringHelper` converts ASCII text into visually similar Unicode characters:

```java
String clean   = "TestText123 789";
String unicode = UStringHelper.forceUnicode(clean);
// e.g., "ＴｅｓｔＴｅｘｔ１２３　７８９"
```

> [!WARNING] Use sparingly
> Heavier censorship (regex-based, similarity checks) can still detect this. `forceUnicode` is best used when you know the server only blocks the plain ASCII form.
