> [!TIP] Quick Navigation  
> - 返回 [README.md](./README.md)
> ### `English` / [简体中文](./PluginDocs_CN.md) 

# DolphinBot Plugin Development Guide

DolphinBot is a headless Minecraft bot framework built on GeyserMC **MCProtocolLib 1.21.11-SNAPSHOT** (protocol 774). Plugins are JAR files dropped into `plugins/`, discovered at runtime, and wired through three simple lifecycle hooks plus a rich event / command / packet-API surface.

This guide is organised as a **top-level index page** with self-contained deep-dives in the [docs/en-us/](./docs/en-us/) directory. You can jump in anywhere.

---

## Table of Contents

| # | Topic | What you'll learn |
|---|-------|-------------------|
| 1 | [Plugin Structure](docs/en-us/01-Plugin-Structure.md) | JAR layout, `plugin.json` manifest, entry-class resolution |
| 2 | [Quick Start](docs/en-us/02-Quick-Start.md) | Build a minimal plugin from scratch — copy-paste ready |
| 3 | [Lifecycle](docs/en-us/03-Lifecycle.md) | `onLoad` / `onEnable` / `onDisable`, per-bot vs global scope |
| 4 | [LoginStateMachine](docs/en-us/04-LoginStateMachine.md) | Chain-style state machine for custom server login flows |
| 5 | [Event System](docs/en-us/05-Event-System.md) | `@EventHandler`, `IListener`, priority levels, built-in events |
| 6 | [Packet Listeners](docs/en-us/06-Packet-Listeners.md) | `getListeners()`, `AbstractEventProcessor`, custom handlers |
| 7 | [Commands](docs/en-us/07-Commands.md) | In-game (`!cmd`) + terminal (`/cmd`) command systems |
| 8 | [Chat & Message API](docs/en-us/08-Chat-Message.md) | `MessageManager`, bot-to-bot messaging, `UStringHelper` |
| 9 | [Player Entity API](docs/en-us/09-Player-Entity.md) | `Player` objects, distance checks, movement tracking |
| 10 | [Actions-Core Architecture](docs/en-us/10-Actions-Core-Architecture.md) | Modular interaction plugin — the SPI you'll most likely extend |
| 11 | [Actions-Core API](docs/en-us/11-Actions-Core-API.md) | `ModuleContext`, `ActionsModule`, `Action`, `ActionQueue` |
| 12 | [Actions-Core World](docs/en-us/12-Actions-Core-World.md) | `WorldCache`, `InventoryCache`, chunk decoding, entity tracking |
| 13 | [Actions-Core Registries](docs/en-us/13-Actions-Core-Registries.md) | Data-driven block / tool properties (vanilla-accurate break times) |
| 14 | [Actions-Core Commands](docs/en-us/14-Actions-Core-Commands.md) | Built-in commands (`/whereiam`, `/goto`, `/harvest`…) and Executor pattern |

---

## Quick Start

### 1. Plugin structure
Plugin jar files are supposed to be contained the plugin manifest file plugin.json inside the root of jar file.
```text
my-plugin.jar
├── plugin.json                    ← manifest at JAR root
└── org/
    └── me/
        └── plugin/
            └── MyPlugin.class     ← compiled entry class
```

### 2. plugin.json
Manifest file plugin.json is aiming to specify the entry class of plugin, plugin name and version.  
**An example for configuring the plugin.json:**
```json
{
  "entry": "org.me.plugin.MyPlugin",
  "name": "MyPlugin",
  "version": "1.0.0"
}
```
**How Entry Class Being Located:**  
When loading a plugin, DolphinBot will locate the target entry class defined in plugin.json, if plugin.json not existing, it will scan all classes that extends AbstractPlugin, if class not found, it will skip and ignore this plugin.

### 3. Entry class
Plugin entry class should extend the AbstractPlugin class:  
```java
package org.me.plugin;

import org.angellock.impl.RobotPlayer;
import org.angellock.impl.plugin.AbstractPlugin;

public class MyPlugin extends AbstractPlugin {
    @Override public String getPluginName() { return "MyPlugin"; }
    @Override public String getVersion()   { return "1.0.0"; }
    @Override public String getDescription() { return "Hello DolphinBot"; }

    @Override
    public void onEnable(RobotPlayer bot) {
        getLogger().info("Hello, " + bot.getProfileName() + "!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Goodbye.");
    }
}
```

### 4. Drop it

```text
target/plugins/my-plugin.jar          ← plugin applies to every bot
target/plugins/<ProfileName>/my-plugin.jar  ← applies to one bot only
```

> [!IMPORTANT]
> The framework expects a **shaded JAR** (all dependencies bundled). See [Building](docs/en-us/02-Quick-Start.md#maven-pomxml-template) for the Maven template.

---

## Actions-Core: The Built-In Modular Plugin

DolphinBot ships with `Actions-Core` — a **fully modular interaction plugin** that demonstrates every pattern in this guide. It's the recommended starting point for anything that needs:

- ✅ Block interaction (place, dig, interact)
- ✅ Movement / pathfinding
- ✅ Terrain queries (chunk decoding, block lookup)
- ✅ Inventory-aware tool selection
- ✅ Asynchronous multi-plugin coordination

> [!NOTE]
> Actions-Core is **not optional dependency** of the framework — it's a regular plugin JAR. You can use it as-is, extend its modules, or rip it out entirely.

### Architecture at a glance

```text
Actions-Core
├── api/                ← SPI (no impl)
│   ├── ActionsModule       interface with onEnable / onLogin / onDisable
│   └── ModuleContext       per-bot wiring: bot, simulator, world, inventory, queue
├── modules/            ← functional units (zero cross-references)
│   ├── AimModule
│   ├── BlockInteractModule   registers /interact /place /harvest
│   ├── MovementModule        registers /whereiam /goto /collect
│   └── executors/*           ICommandExecutor classes (one per command)
├── action/             ← serial execution + pathfinding
│   ├── Action           abstract unit of work
│   ├── ActionQueue      daemon worker; race-free across plugins
│   ├── Pathfinder       A* on the WorldCache grid
│   └── actions/*        6 concrete actions (Goto, Harvest, Place, ...)
├── world/              ← passive models (packet-driven)
│   ├── WorldCache       chunks + block overrides + item entities
│   ├── InventoryCache   hotbar slots (SetContent + SetSlot)
│   ├── BlockNames       global-id ↔ "minecraft:stone" via ViaVersion
│   ├── BlockProperties  vanilla break-time / correct-tool data class
│   ├── BlockPropertiesRegistry   JSON-backed block properties
│   └── ToolProperties   tool category + mining-speed registry
├── simulation/
│   └── VanillaActionSimulator    implements PlayerActionSimulator SPI
└── commands/ (deleted — modules register directly)
```

Jump to [Actions-Core Architecture](docs/en-us/10-Actions-Core-Architecture.md) for the deep dive.

---

## 🧭 Which Section Do I Need?

| I want to… | Read first |
|------------|------------|
| Write a basic plugin | [Quick Start](docs/en-us/02-Quick-Start.md) → [Lifecycle](docs/en-us/03-Lifecycle.md) |
| React to packets | [Packet Listeners](docs/en-us/06-Packet-Listeners.md) |
| Register chat / terminal commands | [Commands](docs/en-us/07-Commands.md) |
| Custom server login flow | [LoginStateMachine](docs/en-us/04-LoginStateMachine.md) |
| Extend Actions-Core / write a new module | [Actions-Core Architecture](docs/en-us/10-Actions-Core-Architecture.md) → [Actions-Core API](docs/en-us/11-Actions-Core-API.md) |
| Make bots find / mine / place blocks | [Actions-Core World](docs/en-us/12-Actions-Core-World.md) + [Registries](docs/en-us/13-Actions-Core-Registries.md) |
| Multi-bot coordination | [Chat & Message API](docs/en-us/08-Chat-Message.md) |

---

> [!NOTE]
> All code examples target Java 17 and are compiled against `DolphinBot-1.6.0-ALPHA-full-shaded.jar` (system scope in the module POM). The Actions-Core submodule under this repository demonstrates every pattern end-to-end.

---

## License

Same as DolphinBot — GPL-3.0. See the top-level [LICENSE](./LICENSE) file.
