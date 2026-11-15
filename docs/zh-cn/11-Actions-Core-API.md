> [!NOTE] 上一页 · [10-架构](./10-Actions-Core-架构.md) · [PluginDocs_CN](../../PluginDocs_CN.md) · [下一页](./12-Actions-Core-世界模型.md)

# 11. Actions-Core — API

这页的四个类型是每个排队 Action 和每个扩展模块的基石。掌握了它们就懂了 90% 的代码。

## `ActionsModule`

```java
public interface ActionsModule {
    String getModuleName();
    void onEnable(ModuleContext context);
    void onLogin(ModuleContext context);  // ClientboundLoginPacket 到达后调用一次
    void onDisable(ModuleContext context);
}
```

每个模块都是**自包含的功能单元**。契约：

| 规则 | 保障方式 |
|------|---------|
| 模块之间严禁互相引用 | 编译期（无交叉 import） |
| 模块与排队 Action 拿到同一个 `ModuleContext` | 运行时（`ActionsCorePlugin` 装配） |
| `onLogin` 在生成后触发一次 | 框架回调 |

> [!TIP] 为什么要有空 `onLogin`？
> 多数模块不需要它。但有些命令（如 `/whereiam`）在 Bot 有生成位置之前没有意义。只有需要生成后状态时才覆盖。

## `ModuleContext` — 每 Bot 注入

```java
public final class ModuleContext {
    RobotPlayer      bot();        // Bot 句柄
    LoggerWrapper    logger();     // 插件日志器
    PlayerActionSimulator simulator(); // aim、swing、interact、dig
    WorldCache       world();      // 被动区块 + 实体模型
    InventoryCache   inventory();  // 热栏槽位
    ActionQueue      queue();      // 串行 Action 执行器
}
```

每个模块和每个排队 Action 对同一个 Bot **拿到的是同一个 `ModuleContext` 实例**。这就是 `queue.submit()` 能从任意线程安全调用的原因——context 是每 Bot 独立的，不会被切换。

> [!IMPORTANT] 不要在 context 外持有 bot 引用
> 每次需要就用 `ctx.bot()`。如果把 `RobotPlayer bot` 存进字段而不通过 context，onDisable 后会持有过期引用。

## `Action` — 一个排队工作单元

```java
public abstract class Action {
    protected Action(String name);
    public abstract boolean run(ModuleContext ctx) throws InterruptedException;
    protected static void sleep(long millis) throws InterruptedException;
}
```

### 规则

| 规则 | 原因 |
|------|------|
| `run` **必须**返回 `true` 表示成功 | `ActionQueue` 根据这个打 `-> done` / `-> failed` |
| `run` **可以自由阻塞/sleep** | 跑在 queue 的 daemon worker 线程上，不是会话线程 |
| `run` **必须**响应中断 | `shutdown` 会调 `Thread.interrupt()`；捕获的 `InterruptedException` 要向上传播 |

### 生命周期

```text
ctx.queue().submit(new HarvestAction("stone", 10, 20, 30))
  ↓
queue 日志: #5 queued: harvest(stone @ 10,20,30)
  ↓ (worker 线程拾取)
action.run(ctx)
  ├── 走到目标（内部用 new GotoAction(...).run(ctx)——直接调，不是 queue.submit）
  ├── 装备工具（用 ctx.inventory()）
  ├── 通过 simulator 挖掘（START_DIGGING + sleep + FINISH_DIGGING）
  └── return true
  ↓
queue 日志: harvest(...) -> done
```

> [!WARNING] 嵌套 `queue.submit()`
> Action 里可能提交别的 Action（比如 HarvestAction 调 `new GotoAction(...).run(ctx)`）。这是故意的——内层 Action 必须先完成才能继续外层。顺序依赖用直接 `.run()`；独立任务才用 `queue.submit`。

## `ActionQueue` — 串行执行器

```java
public class ActionQueue {
    long submit(Action action);     // 返回序号；调用方永不阻塞
    int pending();                  // 当前队列积压
    void shutdown();                // 停掉 worker，清空队列
}
```

### 为什么必须串行？

Minecraft 协议交互**不能并发**。两个插件同时操作 `RobotPlayer` 会产生：

- 交错的 `START_DIGGING` / `FINISH_DIGGING` 包——什么都没挖掉
- 移动和挖掘同时发——目标面/槽位错误
- 损坏的交互序列号

单 daemon worker 保证每 Bot **同一时刻只有一个 Action 在跑**。任意插件、任意线程都能 `queue.submit()`——底层是 `LinkedBlockingQueue` + 单消费者。

### 日志格式

```
[ActionQueue] &2#1 queued: &7whereiam
[ActionQueue] &7whereiam &r-> &2done
[ActionQueue] &2#2 queued: &7goto(-45,117,-544)
[ActionQueue] &7goto(-45,117,-544) &r-> &2done
```

`&2` / `&4` / `&7` 是 `ConsoleTokens` 颜色码（绿色=成功，红色=失败，灰色=中性）。

## 模式：模块 → Executor → Action

每条内置命令都是这个三角关系：

```text
BlockInteractModule.onEnable(ctx)
  └─ registerCommand(name="harvest", build(new HarvestExecutor(ctx)))
       └─ HarvestExecutor.onCommand(response, bot)
            └─ ctx.queue().submit(new HarvestAction(blockName, x, y, z))
```

**Executor** 解析 CLI 参数，构造**带类型参数**的 **Action**。**Action** 通过 `ctx` 做真正的事。模块负责注册；Action 负责语义；Executor 是胶水层。
