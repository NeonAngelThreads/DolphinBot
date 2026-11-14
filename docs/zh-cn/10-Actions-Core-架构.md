> [!NOTE] 上一页 · [09-玩家实体 API](./09-玩家实体API.md) · [PluginDocs_CN](../PluginDocs_CN.md) · [下一页](./11-Actions-Core-API.md)

# 10. Actions-Core — 架构

Actions-Core 是 DolphinBot 自带的**参考插件**，提供完全模块化的交互层——地形感知的寻路、方块交互、工具感知的挖掘、物品拾取。它就是一个普通插件，你可以扩展、替换或完全移除。

## 为什么强调模块化？

> [!IMPORTANT]
> Action **必须**队列化串行执行（跨插件竞态安全）。

这些约束来自踩过的坑：逐帧协议交互在两个插件同时操作 `RobotPlayer` 时会打架；紧耦合的模块无法单独测试或替换。

## 分层架构

```text
┌──────────────────────────────────────────────────────┐
│              终端命令系统                             │
│  /whereiam  /goto  /harvest  /place  /interact  /collect  │
└──────────┬───────────────────────────────────────────┘
           │  Executor.onCommand → queue.submit(XxxAction)
           ▼
┌──────────────────────────────────────────────────────┐
│  ┌────────────────┐   ┌────────────────────────┐     │
│  │  模块 (3 个)   │   │     ActionQueue        │     │
│  │  Movement      │──►│  (LinkedBlockingQueue)│     │
│  │  BlockInteract │   │  + daemon worker      │     │
│  │  Aim           │   └───────────┬────────────┘     │
│  └────────────────┘               │ worker 执行       │
│                                   ▼                  │
│  ┌──────────────────────────────────────────────┐   │
│  │          Action (6 个具体实现)               │   │
│  │  WhereAmIAction  GotoAction  HarvestAction   │   │
│  │  PlaceAction  InteractAction  CollectAction  │   │
│  └──────────────┬───────────────────────────────┘   │
│                 │                                    │
│  ┌──────────────▼───────────────────────────────┐   │
│  │        VanillaActionSimulator (SPI)          │   │
│  │  aimAt  swing  interactBlock  useItem  dig     │   │
│  └──────────────┬───────────────────────────────┘   │
│                 │                                    │
│  ┌──────────────▼──────────┐  ┌────────────────────┐│
│  │   WorldCache (被动模型)  │  │ InventoryCache     ││
│  │  区块 + 掉落物实体      │  │  热栏槽位          ││
│  └─────────────────────────┘  └────────────────────┘│
│  ┌─────────────────────────┐  ┌────────────────────┐│
│  │ BlockPropertiesRegistry │  │ ToolProperties     ││
│  │ JSON: block-properties  │  │ static: 30+ 工具  ││
│  └─────────────────────────┘  └────────────────────┘│
└──────────────────────────────────────────────────────┘
                  ┌─────────────────┐
                  │  AbstractPlugin  │  (DolphinBot)
                  └─────────────────┘
```

## 包结构

```text
org.dolphinbot.actionscore/
├── ActionsCorePlugin.java         ← 入口类
├── api/
│   ├── ActionsModule.java         ← SPI 接口
│   └── ModuleContext.java         ← 每 Bot 注入（bot、queue、world、inventory、simulator）
├── modules/
│   ├── AimModule.java             ← 目前不注册命令
│   ├── BlockInteractModule.java   ← 注册 /interact /place /harvest
│   ├── MovementModule.java        ← 注册 /whereiam /goto /collect
│   └── executors/                 ← 6 个 ICommandExecutor 实现（每条命令一个）
├── action/
│   ├── Action.java                ← 抽象工作单元
│   ├── ActionQueue.java           ← 串行 daemon worker
│   ├── Pathfinder.java            ← WorldCache 网格上的 A*
│   └── actions/                   ← 6 个具体 Action
├── world/
│   ├── WorldCache.java            ← 包驱动的区块 + 实体模型
│   ├── InventoryCache.java        ← 热栏（SetContent + SetSlot）
│   ├── ChunkColumn.java           ← 逐 section 区块解码器
│   ├── BlockNames.java            ← ViaVersion 全局 ID ↔ 标识
│   ├── BlockProperties.java       ← 不可变记录类
│   ├── BlockPropertiesRegistry.java ← JSON 方块属性加载器
│   └── ToolProperties.java        ← 工具类别 + 挖掘速度注册表
├── simulation/
│   └── VanillaActionSimulator.java ← 实现 RobotPlayer SPI
├── handlers/
│   └── SmokeHandler.java          ← 登录后自检
└── common/
    └── CoordinateUtil.java        ← CLI 参数解析
```

## 在框架里的生命周期

```java
public class ActionsCorePlugin extends AbstractPlugin {

    @Override
    public void onLoad() {
        RobotPlayer.registerActionSimulator(this.simulator);  // SPI 注册（一次性）
        commandManager = getTerminalCommands();
        modules.add(new AimModule(this));
        modules.add(new BlockInteractModule(this));
        modules.add(new MovementModule(this));
    }

    @Override
    public void onEnable(RobotPlayer bot) {
        WorldCache world = new WorldCache();                 // 每 Bot
        InventoryCache inv = new InventoryCache();           // 每 Bot
        context = new ModuleContext(bot, getLogger(), simulator, world, inv);

        modules.forEach(m -> m.onEnable(context));          // 每个模块注册命令

        getListeners().add(world);                           // 订阅包
        getListeners().add(inv);
    }

    @Override
    public void onDisable() {
        context.queue().shutdown();                          // 停掉 daemon worker
        getListeners().clear();
    }
}
```

## 加新命令的流程

1. 写一个 `XxxExecutor implements ICommandExecutor`（构造器收 `ModuleContext`）
2. 写一个 `XxxAction extends Action`（把真正的逻辑放这里）
3. 选模块——属于 `MovementModule`、`BlockInteractModule` 还是新建一个？
4. 在 `module.onEnable` 里注册：
   ```java
   commandManager.registerCommand(new TerminalCommand.Builder()
       .withName("xxx")
       .withDescription("...")
       .withUsage("/xxx ...")
       .withProvider(this.provider)
       .build(new XxxExecutor(context)));
   ```
5. 构建——零跨引用、零模板代码

> [!TIP] 从已有 Executor 复制
> `modules/executors/CollectExecutor.java` 是最简单的。复制一份然后改 Action 类型即可。

继续阅读：[API](./11-Actions-Core-API.md) · [世界模型](./12-Actions-Core-世界模型.md) · [数据注册表](./13-Actions-Core-数据注册表.md) · [命令](./14-Actions-Core-命令.md)
