> [!NOTE] Previous · [11-API](./11-Actions-Core-API.md) · [PluginDocs](../PluginDocs.md) · [Next](./13-Actions-Core-Registries.md)

# 12. Actions-Core — World & Inventory Models

Actions-Core keeps **passive, packet-driven models** of the world and inventory. No polling, no queries to the server — every data point flows from `Clientbound*` packets through `getListeners()`.

## `WorldCache`

A `SessionAdapter` that subscribes to:

| Packet | What it does |
|--------|--------------|
| `ClientboundLevelChunkWithLightPacket` | Decodes the chunk column into `ChunkColumn` |
| `ClientboundForgetLevelChunkPacket` | Drops the column |
| `ClientboundBlockUpdatePacket` | Overrides a single block |
| `ClientboundSectionBlocksUpdatePacket` | Overrides blocks in a section |
| `ClientboundAddEntityPacket` | Adds item entities (dropped items) |
| `ClientboundEntityPositionSyncPacket` | Updates item-entity positions (1.21.2+ wire format) |
| `ClientboundSetEntityDataPacket` | Reads item stack from entity metadata |
| `ClientboundRemoveEntitiesPacket` | Removes entities from tracking |
| `ClientboundLoginPacket` | Reads dimension / world name |

### Key methods

```java
public class WorldCache extends SessionAdapter {
    int    getBlockState(int x, int y, int z);   // global block state id, or -1 if chunk unloaded
    String getBlockName(int x, int y, int z);    // "minecraft:stone", or null if air/unloaded
    boolean isAir(int x, int y, int z);
    boolean isSolid(int x, int y, int z);          // real collision blocks only (not grass/torches/buttons)
    boolean isWalkableThrough(int x, int y, int z); // air + no-collision blocks
    boolean isLoaded(int x, int z);

    int[] nearestBlock(IntPredicate statePred, int cx, int cy, int cz, int rxz, int ry);
    double[] nearestItemEntity(double x, double y, double z, double radius, int itemId);

    Map<Integer, double[]> itemEntitiesSnapshot();  // entityId → [x,y,z,itemId-or--1]
}
```

> [!NOTE] `isSolid` is **not** `state > 0`
> The former includes only blocks with real collision. Grass, torches, buttons, rails all return `false` — they don't block movement. This was a bug in the first pathfinder that caused false "no path" rejections.

### Chunk decoding

`ChunkColumn.parse(x, z, chunkData)` implements **vanilla 1.21.11 section format**:

```text
section = [short blockCount][blocks paletted][biomes paletted]
paletted container = [u8 bits][palette][fixed long array (no length prefix)]
```

Critical detail: `SimpleBitStorage` (vanilla) uses `valuesPerLong = floor(64 / bits)` — entries **don't cross long boundaries**. The first implementation got this wrong and produced 100% parse failures until an offline hex decoder found the misalignment. See `ChunkColumn.java` for the corrected version.

> [!WARNING] Unknown block = unbreakable
> `BlockPropertiesRegistry.get()` defaults to `UNBREAKABLE` for blocks not in the JSON data file. If you add a modded server, update `block-properties.json` or it'll silently skip digging.

## `InventoryCache`

Tracks the 9 hotbar slots from two packet types:

| Packet | Scope |
|--------|-------|
| `ClientboundContainerSetContentPacket` | Full inventory snapshot |
| `ClientboundContainerSetSlotPacket` | Single slot update (from `/give`, pickup, slot switch) |

### Key methods

```java
public class InventoryCache extends SessionAdapter {
    OptionalInt findHotbarSlot(String nameFragment);     // "diamond_pickaxe" → slot index
    OptionalInt findBestToolFor(Set<String> categories); // {"pickaxe","axe"} → highest-speed match
    OptionalInt findBestTool(String[] tierOrder, String toolWord); // legacy tier-based lookup
    ItemStack slot(int slot);                           // direct access
}
```

### Tool resolution

```java
OptionalInt slot = ctx.inventory().findBestToolFor(Set.of("pickaxe"));
if (slot.isPresent()) {
    ItemStack stack = ctx.inventory().slot(slot.getAsInt());
    String itemName = BlockNames.itemName(stack.getId());
    ToolProperties tp = ToolProperties.get(itemName);
    // tp = (category="pickaxe", miningSpeed=8.0, correctForDrops=true) for diamond_pickaxe
}
```

## Pathfinder — A* on the grid

`Pathfinder.find(start, goal, world)` runs A* over the `WorldCache` grid with these actions:

```text
dx,dz ∈ {-1, 0, 1}, d ∈ {0, 1, -1, -2, -3}
goal cell walkable? walkable(start+dx, start+dz, startY+d)?
+ ceiling headroom check when stepping up through the ceiling
```

The `walkable` predicate uses `world.isWalkableThrough(x,y,z)` + `world.isSolid(x,y-1,z)`. This correctly allows walking over flowers, through tall grass, past buttons, and on solid ground.

> [!TIP] Goal may not be walkable
> The A* goal cell is often **solid** (e.g., the stone block you want to stand next to). `GotoAction` calls the pathfinder, then picks the **neighbour cell** as the final waypoint — you stand next to the target, not inside it.

## `BlockNames` — global-id → identifier

DolphinBot's MCProtocolLib reports blocks as **global state IDs** (e.g., `stateId=5` for polished_diorite, not the full stack). `BlockNames` resolves these to `"minecraft:polished_diorite"` via ViaVersion's identifier table.

> [!WARNING] Key name mismatch
> ViaVersion's `MappingDataLoader.identifierFromGlobalId(key, id)` uses `"blocks"` and `"items"` as table keys, **not** `"blockstates"`. The first hardcoded `"blockstates"` lookup returned `null` for every block, causing `HarvestAction` to silently find nothing. `probeKey()` tries all three names and caches the first that works.

## Why passive models?

Every data structure here is **read-only** from the perspective of actions and commands. They listen to packets, update state atomically, and answer queries. This makes them safe to call from any thread — the queue worker, command executor, or even packet handler callbacks.
