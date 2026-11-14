> [!NOTE] 上一页 · [13-数据注册表](./13-Actions-Core-数据注册表.md) · [PluginDocs_CN](../PluginDocs_CN.md)

# 14. Actions-Core — 内置命令

Actions-Core 自带 6 条终端命令。由所属模块注册，通过 `ActionQueue` 异步执行。

## 命令列表

| 命令 | 参数 | 所属模块 | 作用 |
|------|------|---------|------|
| `/whereiam` | — | MovementModule | 日志世界、坐标、旋转、脚下方块 |
| `/goto` | `<x> <y> <z>` | MovementModule | 寻路走到目标点；自动走、跳、爬 |
| `/harvest` | `<block> [x y z]` | BlockInteractModule | 找（或指定）目标方块、装备最佳工具、START/FINISH 挖掘 |
| `/place` | `<block> <x> <y> <z>` | BlockInteractModule | 切到热栏里对应物品，右键放置 |
| `/interact` | `<x> <y> <z> [block]` 或 `<block>` | BlockInteractModule | 右键——交互式方块（门、按钮、拉杆…）会响应 |
| `/collect` | `<item>` | MovementModule | 找最近的掉落物实体、走到旁、等待拾取 |

## 用法示例

```
/whereiam
  [whereiam] world=minecraft:overworld dimension=0
             pos=(-46.56, 117.00, -544.49)
             block=(-47, 117, -545) rot=(yaw 162.7, pitch 33.9)

/goto -45 117 -544
  [goto] path found: 4 waypoints
  [goto] arrived at (-45, 117, -544)

/harvest polished_diorite           ← 最近匹配方块（16 格内）
/harvest polished_diorite -47 117 -545  ← 精确坐标

/place stone -45 117 -543           ← stone 必须在热栏里
  [place] 'stone' not found in hotbar    ← 友好错误
  [place] target out of reach (5.85 blocks)  ← 先用 /goto

/interact oak_trapdoor               ← ±6 格内最近、需可到达
/interact -46 117 -543              ← 无条件右键
/interact -46 117 -543 minecraft:oak_trapdoor  ← 右键 + 确认目标

/collect minecraft:oak_button       ← 走到最近掉落实体旁
  [ActionQueue] #12 queued: collect(minecraft:oak_button)
  [ActionQueue] #12 collect(minecraft:oak_button) -> done
```

## Executor 模式

每条命令遵循相同的三层结构：

```java
// 1) EXECUTOR — 解析 CLI 参数，用带类型的参数构造 Action
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

// 2) MODULE — 向框架注册终端命令
public class BlockInteractModule implements ActionsModule {
    @Override
    public void onEnable(ModuleContext ctx) {
        ActionsCorePlugin.getCommandManager().registerCommand(
            new TerminalCommand.Builder()
                .withName("harvest")
                .withDescription("用正确工具挖方块。")
                .withUsage("/harvest <block> [x y z]")
                .withProvider(this.provider)
                .build(new HarvestExecutor(ctx)));     // ← 注入 ctx
    }
}
```

> [!IMPORTANT] 不要直接用 `ActionsCorePlugin.getContext()`
> `ModuleContext` 通过 `module.onEnable(ctx)` 传入。**显式传给 Executor 构造器**——不要从静态插件 getter 回取。这样依赖关系明确，也避免初始化顺序隐患。

## 为什么排队执行？

命令不跑在终端线程上。它们 `queue.submit()` 后 daemon worker 拾取——这意味着：

- **终端永远不阻塞**——即使 15 秒的寻路期间终端还能继续输入
- **跨插件无竞态**——如果另一个插件也从事件 handler 里 `queue.submit()`，两者也是依次执行
- **进度可追踪**——每个 Action 都会打 `#N queued: name` → `-> done` / `-> failed`

```
[ActionQueue] #3 queued: whereiam
[ActionQueue] #3 whereiam -> done
[ActionQueue] #4 queued: goto(-45,117,-544)
[ActionQueue] #5 queued: harvest(polished_diorite @ -47,117,-545)   ← #4 还在跑时入队
[ActionQueue] #4 goto(...) -> done
[ActionQueue] #5 harvest(...) -> done
```

## 自己加命令

1. 写 `MyExecutor implements ICommandExecutor`
2. 写 `MyAction extends Action`（或复用现有）
3. 选模块——在 `module.onEnable(ctx)` 里注册
4. 构建——完成

```java
@Override
public void onEnable(ModuleContext ctx) {
    ActionsCorePlugin.getCommandManager().registerCommand(
        new TerminalCommand.Builder()
            .withName("mycmd")
            .withDescription("做件很酷的事。")
            .withUsage("/mycmd <参数>")
            .withProvider(this.provider)
            .build((r, b) -> ctx.queue().submit(new MyAction(...))));
}
```
