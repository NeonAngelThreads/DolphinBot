> [!NOTE] 上一页 · [11-API](./11-Actions-Core-API.md) · [PluginDocs_CN](../PluginDocs_CN.md) · [下一页](./13-Actions-Core-数据注册表.md)

# 12. Actions-Core — 世界与背包模型

Actions-Core 维护**被动的、包驱动的**世界和背包模型。没有轮询，没有服务器查询——每个数据点从 `Clientbound*` 包流入 `getListeners()`。

## `WorldCache`

继承 `SessionAdapter`，订阅以下包：

| 包 | 作用 |
|----|------|
| `ClientboundLevelChunkWithLightPacket` | 把区块列解码进 `ChunkColumn` |
| `ClientboundForgetLevelChunkPacket` | 丢弃列 |
| `ClientboundBlockUpdatePacket` | 覆盖单个方块 |
| `ClientboundSectionBlocksUpdatePacket` | 覆盖 section 内方块 |
| `ClientboundAddEntityPacket` | 添加掉落物实体 |
| `ClientboundEntityPositionSyncPacket` | 更新掉落物位置（1.21.2+ 线格式） |
| `ClientboundSetEntityDataPacket` | 从实体元数据读物品栈 |
| `ClientboundRemoveEntitiesPacket` | 移除追踪 |
| `ClientboundLoginPacket` | 读维度 / 世界名 |

### 关键方法

```java
public class WorldCache extends SessionAdapter {
    int    getBlockState(int x, int y, int z);   // 全局方块 state id，区块未加载时 -1
    String getBlockName(int x, int y, int z);    // "minecraft:stone"，空气/未加载时 null
    boolean isAir(int x, int y, int z);
    boolean isSolid(int x, int y, int z);          // 只有真实碰撞方块（不含草/火把/按钮）
    boolean isWalkableThrough(int x, int y, int z); // 空气 + 无碰撞方块
    boolean isLoaded(int x, int z);

    int[] nearestBlock(IntPredicate statePred, int cx, int cy, int cz, int rxz, int ry);
    double[] nearestItemEntity(double x, double y, double z, double radius, int itemId);

    Map<Integer, double[]> itemEntitiesSnapshot();  // entityId → [x,y,z,itemId-or--1]
}
```

> [!NOTE] `isSolid` 不是 `state > 0`
> 后者会把草、火把、按钮、铁轨全部当成实心——导致 Pathfinder 大量假"无路径"拒绝。`isSolid` 只返回有真实碰撞的方块。这是寻路早期踩过的坑。

### 区块解码

`ChunkColumn.parse(x, z, chunkData)` 实现**原版 1.21.11 section 格式**：

```text
section = [short blockCount][blocks paletted][biomes paletted]
paletted container = [u8 bits][palette][固定长度 long 数组（无长度前缀）]
```

关键细节：`SimpleBitStorage`（原版实现）使用 `valuesPerLong = floor(64 / bits)`——**entry 不跨 long 边界**。第一个实现写错了，100% 解析失败，直到离线 hex 解码器定位到错位。修正版见 `ChunkColumn.java`。

> [!WARNING] 未知方块 = 不可破坏
> `BlockPropertiesRegistry.get()` 对不在 JSON 里的方块默认 `UNBREAKABLE`。如果加了模组服务器，记得更新 `block-properties.json`，否则会静默跳过挖掘。

## `InventoryCache`

从两种包跟踪 9 格热栏：

| 包 | 范围 |
|----|------|
| `ClientboundContainerSetContentPacket` | 全量背包快照 |
| `ClientboundContainerSetSlotPacket` | 单槽更新（`/give`、拾取、切槽都走这个） |

### 关键方法

```java
public class InventoryCache extends SessionAdapter {
    OptionalInt findHotbarSlot(String nameFragment);     // "diamond_pickaxe" → 槽索引
    OptionalInt findBestToolFor(Set<String> categories); // {"pickaxe","axe"} → 速度最快的匹配
    OptionalInt findBestTool(String[] tierOrder, String toolWord); // 旧版 tier 查找
    ItemStack slot(int slot);                           // 直接访问
}
```

### 工具解析

```java
OptionalInt slot = ctx.inventory().findBestToolFor(Set.of("pickaxe"));
if (slot.isPresent()) {
    ItemStack stack = ctx.inventory().slot(slot.getAsInt());
    String itemName = BlockNames.itemName(stack.getId());
    ToolProperties tp = ToolProperties.get(itemName);
    // tp = (category="pickaxe", miningSpeed=8.0, correctForDrops=true) 对应 diamond_pickaxe
}
```

## Pathfinder — 网格上的 A*

`Pathfinder.find(start, goal, world)` 在 `WorldCache` 网格上跑 A*，动作集：

```text
dx,dz ∈ {-1, 0, 1}, dy ∈ {0, 1, -1, -2, -3}
目标格可走？走得到(start+dx, start+dz, startY+dy)？
+ 上跳时的天花板净空检查
```

`walkable` 谓词用 `world.isWalkableThrough(x,y,z)` + `world.isSolid(x,y-1,z)`。这正确允许走过草地、穿过高草、跨过按钮、踩在坚实地面上。

> [!TIP] 目标格可能不可走
> A* 的 goal 格经常是**实心**（比如要站到石头旁边挖）。`GotoAction` 调寻路后取**相邻格**作为最终 waypoint——站在目标旁边，不是站在目标里。

## `BlockNames` — 全局 ID → 标识

MCProtocolLib 报告方块为**全局 state ID**（例如 `stateId=5` 代表 polished_diorite，不是完整字符串）。`BlockNames` 通过 ViaVersion 的标识表解析为 `"minecraft:polished_diorite"`。

> [!WARNING] 表名不匹配
> ViaVersion 的 `MappingDataLoader.identifierFromGlobalId(key, id)` 用 `"blocks"` 和 `"items"` 作为表键，**不是** `"blockstates"`。最初硬编码 `"blockstates"` 导致每个方块解析返回 `null`，`HarvestAction` 静默找不到任何方块。`probeKey()` 尝试所有三个名字并缓存第一个有效的。

## 为什么用被动模型？

这里每个数据结构对 Action 和命令来说都是**只读的**。它们只订阅包、原子更新状态、回答查询。这种设计让它们能从任意线程安全调用——queue worker、命令执行器、甚至包处理器回调都可以。
