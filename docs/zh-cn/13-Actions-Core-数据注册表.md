> [!NOTE] 上一页 · [12-世界模型](./12-Actions-Core-世界模型.md) · [PluginDocs_CN](../PluginDocs_CN.md) · [下一页](./14-Actions-Core-命令.md)

# 13. Actions-Core — 数据驱动注册表

方块破坏时间和工具选择**不再硬编码**。数据来自打包的 JSON 资源（`block-properties.json`）和小型静态工具注册表（`ToolProperties`）。

## 为什么替换硬编码的 `digMillis` / `toolFor`？

旧写法：

```java
// 之前：子串判断——很多方块不准，无法扩展
static long digMillis(String name, boolean hasTool) {
    if (name.contains("dirt") || name.contains("sand")) return 600;
    if (name.contains("stone")) return hasTool ? 1200 : 7500;
    ...
}
static String toolFor(String name) {
    if (name.contains("log") || name.contains("plank")) return "axe";
    ...
}
```

问题：

| 问题 | 后果 |
|------|------|
| 破坏时间不准（钻石镐挖石头 → 1200ms，应该是 281ms） | FINISH_DIGGING 提前发 → 方块没挖掉 |
| 工具类别不准（polished_deepslate → substring "stone" → pickaxe 对但 4.5s 破坏 vs 1.5s stone → 时间错） | 方块用错误时间挖 |
| 没有工具等级速度变化（木镐 = 2.0，下界合金 = 9.0，金 = 12.0） | 无法精确计时不同等级 |
| 错误工具破坏系数（错误工具 = 5x 慢且无掉落） | 手挖铁矿没掉落但时间又不反映这个 |
| 没检查 `requiresCorrectToolForDrops` | 手挖铁矿无掉落但 timing 暗示成功 |

## `BlockProperties` — 原版精确的数据类

```java
public record BlockProperties(
    float destroyTime,                   // 基础破坏时间（秒）
    boolean requiresCorrectToolForDrops, // 错误工具无掉落
    Set<String> mineableTags,            // {"pickaxe"}、{"axe"}、{"shovel"}、{"hoe"}、{"shears"}、{"sword"}
    float instantBreak                   // 特定工具可秒破（如剑对蛛网）
) { ... }
```

值来自 1.21.11 的 `BlockBehaviour.Properties` + `BlockTags.MINEABLE_WITH_*` + `Tool` 数据组件。

## `block-properties.json` — 数据文件

位置：`Actions-Core/src/main/resources/block-properties.json`。包含 220+ 原版方块：

```json
{
  "minecraft:stone":          { "destroyTime": 1.5, "correctTool": true,  "minable": ["pickaxe"] },
  "minecraft:dirt":           { "destroyTime": 0.5, "correctTool": false, "minable": ["shovel"] },
  "minecraft:polished_diorite": { "destroyTime": 1.5, "correctTool": true, "minable": ["pickaxe"] },
  "minecraft:obsidian":       { "destroyTime": 9.0, "correctTool": true,  "minable": ["pickaxe"] },
  "minecraft:leaves":         { "destroyTime": 0.2, "correctTool": false, "minable": ["shears", "hoe"] },
  "minecraft:cobweb":         { "destroyTime": 0.2, "correctTool": false, "minable": ["shears", "sword"], "instantBreak": 0.0 },
  "minecraft:bedrock":        { "destroyTime": -1,  "correctTool": false, "minable": [] }
}
```

### JSON 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `destroyTime` | 数字 | `-1` = 不可破坏；`0` = 秒破 |
| `correctTool` | 布尔 | 错误工具是否无掉落 |
| `minable` | 字符串数组 | 能高效挖掘的工具类别 |
| `instantBreak` | 数字（可选） | 非零时表示特定工具速度超过此值秒破 |

> [!TIP] 加新方块
> 往 JSON 末尾追加条目然后重新构建即可。未知方块默认 `UNBREAKABLE`（安全回退）。如果某个方块应该能挖但没在文件里，它会静默拒绝——grep 你的目标方块名然后加上。

## `BlockPropertiesRegistry` — 加载器

```java
public final class BlockPropertiesRegistry {
    public static synchronized void load();    // 从 /block-properties.json 加载（只加载一次）
    public static BlockProperties get(String blockName);     // "minecraft:stone" → record
    public static BlockProperties get(int stateId);         // 全局 id → record（经 BlockNames 解析）
    public static boolean available();                       // 是否已加载至少一条
}
```

懒加载——第一次 `get()` 触发 `load()`。JSON 缺失或损坏会打错误日志，之后所有查询 fallback 到 `UNBREAKABLE`。

> [!NOTE] JSON 解析器是手写的
> `BlockPropertiesRegistry` 用最小的平面 JSON 解析器，能处理当前 schema（`{ "key": { ... }, ... }`）但不支持嵌套数组或复杂 Unicode 转义。如果将来需要更复杂的值，换成 Gson 或 Jackson。

## `ToolProperties` — 静态工具注册表

工具数据量小（~30 条），直接用 Java 编码：

```java
public record ToolProperties(
    String toolCategory,      // "pickaxe"、"axe"、"shovel"、"hoe"、"shears"、"sword"、"hand"
    float miningSpeed,        // 破坏时间的速度倍率
    boolean correctForDrops   // 工具类 = true，徒手 = false
) {
    public static ToolProperties get(String itemName); // "minecraft:diamond_pickaxe" → (pickaxe, 8.0, true)
    public static ToolProperties HAND = ...;
}
```

### 挖掘速度

| 工具等级 | 镐 | 斧 | 铲 | 锄 |
|----------|----|----|----|----|
| 木 | 2.0 | 2.0 | 2.0 | 2.0 |
| 石 | 4.0 | 4.0 | 4.0 | 4.0 |
| 铁 | 6.0 | 6.0 | 6.0 | 6.0 |
| 钻石 | 8.0 | 8.0 | 8.0 | 8.0 |
| 下界合金 | 9.0 | 9.0 | 9.0 | 9.0 |
| 金 | 12.0 | 12.0| 12.0| 12.0|

剪刀 = 1.5，剑 = 1.5，徒手 = 1.0。

## 原版破坏时间公式

HarvestAction 用 **原版精确公式**（来自 `BlockBehaviour.getDestroyProgress`）：

```java
float progressPerTick = toolSpeed / block.destroyTime / (correctTool ? 30 : 100);
long digMs = (long)(block.destroyTime * (correctTool ? 30 : 100) * 50.0f / toolSpeed);
```

| 方块 | destroyTime | 工具 | toolSpeed | 正确？ | digMs | 旧硬编码 |
|------|------------:|------|----------:|--------|------:|----------:|
| stone | 1.5 | 徒手 | 1.0 | ✗ | 7500 | 7500 ✓ |
| stone | 1.5 | diamond_pickaxe | 8.0 | ✓ | **281** | 1200 ✗ |
| dirt | 0.5 | 徒手 | 1.0 | ✓ | 750 | 600 ✗ |
| dirt | 0.5 | netherite_shovel | 9.0 | ✓ | **83** | 600 ✗ |
| obsidian | 9.0 | netherite_pickaxe | 9.0 | ✓ | **1500** | 1200 ✗ |

注册表驱动在钻石/下界合金工具和错误工具场景下**显著更准**。

## 还没做什么（backlog）

- **附魔**：效率附魔进一步提速——还没建模（harvest 用基础 tier 速度）
- **精准采集 / 时运**：原版掉落系统处理，Actions-Core 不管
- **水**：脚下在水中时挖掘慢 5x——还没建模
- **MINING_EFFICIENCY 属性**：药水利害、潮涌之力——还没建模

每个都是破坏时间公式里多加一个乘数。
