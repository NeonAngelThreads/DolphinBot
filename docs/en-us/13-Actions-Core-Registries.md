> [!NOTE] Previous · [12-World](./12-Actions-Core-World.md) · [PluginDocs](../PluginDocs.md) · [Next](./14-Actions-Core-Commands.md)

# 13. Actions-Core — Data-Driven Registries

Block break time and tool selection are **not** hardcoded anymore. They come from a bundled JSON resource (`block-properties.json`) and a small static tool registry (`ToolProperties`). This page covers both.

## Why replace hardcoded `digMillis` / `toolFor`?

The original heuristic looked like this:

```java
// BEFORE: substring checks — wrong for many blocks, impossible to extend
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

Problems:

| Problem | Consequence |
|---------|-------------|
| Wrong break times (diamond pick on stone → 1200ms, should be 281ms) | Premature FINISH_DIGGING → block doesn't break |
| Wrong tool categories (polished_deepslate → substring "stone" → pickaxe ✓ but 4.5s destroy vs 1.5s stone → wrong timing) | Block takes wrong time to break |
| No tool-speed variation (wood pick = 2.0, netherite = 9.0, gold = 12.0) | Can't accurately time digs with different tiers |
| Wrong-tool break factor (wrong tool = 5x slower for drops, 3.3x otherwise) | No drops when wrong tool used, but timing doesn't reflect this |
| `requiresCorrectToolForDrops` flag not checked | Hand mining on iron ore yields nothing but timing suggests success |

## `BlockProperties` — vanilla-derived data class

```java
public record BlockProperties(
    float destroyTime,                   // base break time in seconds
    boolean requiresCorrectToolForDrops, // true when wrong-tool gives no drops
    Set<String> mineableTags,            // {"pickaxe"}, {"axe"}, {"shovel"}, {"hoe"}, {"shears"}, {"sword"}
    float instantBreak                   // non-zero for instant-break under specific tool (e.g., sword on cobweb)
) { ... }
```

Values come from 1.21.11's `BlockBehaviour.Properties` + `BlockTags.MINEABLE_WITH_*` + `Tool` data component.

## `block-properties.json` — the data file

Located at `Actions-Core/src/main/resources/block-properties.json`. 220+ vanilla blocks:

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

### JSON schema

| Field | Type | Notes |
|-------|------|-------|
| `destroyTime` | number | `-1` = unbreakable; `0` = instant |
| `correctTool` | boolean | Whether wrong tool gives no drops |
| `minable` | array of strings | Tool categories that dig efficiently |
| `instantBreak` | number (optional) | Non-zero means instant break when tool speed exceeds this |

> [!TIP] Adding blocks
> Just append to the JSON and rebuild. Unknown blocks default to `UNBREAKABLE` (safe fallback). If you know a block should be diggable but isn't in the file, it'll silently refuse — grep for your target block and add it.

## `BlockPropertiesRegistry` — the loader

```java
public final class BlockPropertiesRegistry {
    public static synchronized void load();    // loads from /block-properties.json (once)
    public static BlockProperties get(String blockName);     // "minecraft:stone" → record
    public static BlockProperties get(int stateId);         // global id → record via BlockNames resolution
    public static boolean available();                       // has loaded at least one entry
}
```

Load is **lazy** — first `get()` call triggers `load()`. If the JSON is missing or malformed, logs an error and falls back to `UNBREAKABLE` for every query.

> [!NOTE] JSON parser is hand-rolled
> `BlockPropertiesRegistry` uses a minimal hand-written flat-JSON parser. It handles the specific schema (`{ "key": { ... }, ... }`) but not nested arrays or fancy Unicode escapes. If you need more complex values, swap in Gson or Jackson.

## `ToolProperties` — static tool registry

Tool data is small (~30 entries) so it's encoded directly in Java:

```java
public record ToolProperties(
    String toolCategory,      // "pickaxe", "axe", "shovel", "hoe", "shears", "sword", "hand"
    float miningSpeed,        // multiplier applied to destroyTime
    boolean correctForDrops   // true for real tools, false for hand
) {
    public static ToolProperties get(String itemName); // "minecraft:diamond_pickaxe" → (pickaxe, 8.0, true)
    public static ToolProperties HAND = ...;
}
```

### Mining speeds

| Tool tier | pickaxe | axe | shovel | hoe |
|-----------|---------|-----|--------|-----|
| wooden    | 2.0     | 2.0 | 2.0    | 2.0 |
| stone     | 4.0     | 4.0 | 4.0    | 4.0 |
| iron      | 6.0     | 6.0 | 6.0    | 6.0 |
| diamond   | 8.0     | 8.0 | 8.0    | 8.0 |
| netherite | 9.0     | 9.0 | 9.0    | 9.0 |
| golden    | 12.0    | 12.0| 12.0   | 12.0|

Shears = 1.5, sword = 1.5, hand = 1.0.

## Vanilla break-time formula

Harvest action computes dig time with the **exact vanilla formula** from `BlockBehaviour.getDestroyProgress`:

```java
float progressPerTick = toolSpeed / block.destroyTime / (correctTool ? 30 : 100);
long digMs = (long)(block.destroyTime * (correctTool ? 30 : 100) * 50.0f / toolSpeed);
```

| Block | destroyTime | Tool | toolSpeed | correct? | digMs | Old hardcoded |
|-------|------------:|------|----------:|----------:|------:|--------------:|
| stone | 1.5 | hand | 1.0 | ✗ | 7500 | 7500 ✓ |
| stone | 1.5 | diamond_pickaxe | 8.0 | ✓ | **281** | 1200 ✗ |
| dirt | 0.5 | hand | 1.0 | ✓ | 750 | 600 ✗ |
| dirt | 0.5 | netherite_shovel | 9.0 | ✓ | **83** | 600 ✗ |
| obsidian | 9.0 | netherite_pickaxe | 9.0 | ✓ | **1500** | 1200 ✗ |

The registry-driven approach is **substantially more accurate** for diamond/netherite tools and wrong-tool scenarios.

## What's NOT in the registry (yet)

- **Enchantments**: `Efficiency` on tools further increases speed; not modeled (harvest uses base tier speed)
- **Silk Touch / Fortune**: handled separately by vanilla drops, not by Actions-Core
- **Water**: slows digging 5x when the player's feet are in water; not modeled
- **Attribute `MINING_EFFICIENCY`**: potion effects, haste, conduit power — not modeled

These are all on the backlog. Each is a simple additional multiplier in the break-time formula.
