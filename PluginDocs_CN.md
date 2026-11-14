> [!TIP] 快速导航  
> - 返回 [README.md](./README.md)
> ### [English](./PluginDocs.md) / `简体中文`

# DolphinBot 插件开发指南

DolphinBot 是一个基于 GeyserMC **MCProtocolLib 1.21.11-SNAPSHOT**（协议版本 774）的无头 Minecraft Bot 框架。插件是 JAR 文件，放在 `plugins/` 目录下即可被运行时发现，通过三个生命周期钩子和一套事件/命令/包 API 接入 Bot 行为。

本文档采用 **主索引 + 子章节** 的树形结构：本页为总入口。如果你想深入某一主题，可以直接跳转到 `docs/zh-cn/` 下的对应文件。

---

## 目录

| # | 专题 | 你将学到什么 |
|---|------|-------------|
| 1 | [插件结构](docs/zh-cn/01-插件结构.md) | JAR 打包布局、`plugin.json` 清单、入口类解析 |
| 2 | [快速上手](docs/zh-cn/02-快速上手.md) | 从零构建一个最小可用插件——可直接复制运行 |
| 3 | [生命周期](docs/zh-cn/03-生命周期.md) | `onLoad` / `onEnable` / `onDisable`，全局与单 Bot 作用域 |
| 4 | [LoginStateMachine](docs/zh-cn/04-LoginStateMachine.md) | 链式状态机，自定义服务器登录流程 |
| 5 | [事件系统](docs/zh-cn/05-事件系统.md) | `@EventHandler`、`IListener`、优先级、内置事件 |
| 6 | [包监听器](docs/zh-cn/06-包监听器.md) | `getListeners()`、`AbstractEventProcessor`、自定义处理器 |
| 7 | [命令系统](docs/zh-cn/07-命令系统.md) | 游戏内（`!cmd`）+ 终端（`/cmd`）两套命令体系 |
| 8 | [聊天与跨 Bot 消息](docs/zh-cn/08-聊天与跨Bot消息.md) | `MessageManager`、多 Bot 消息广播、`UStringHelper` |
| 9 | [玩家实体 API](docs/zh-cn/09-玩家实体API.md) | `Player` 对象、距离检测、移动追踪 |
| 10 | [Actions-Core 架构](docs/zh-cn/10-Actions-Core-架构.md) | 模块化交互插件——最推荐的扩展切入点 |
| 11 | [Actions-Core API](docs/zh-cn/11-Actions-Core-API.md) | `ModuleContext`、`ActionsModule`、`Action`、`ActionQueue` |
| 12 | [Actions-Core 世界模型](docs/zh-cn/12-Actions-Core-世界模型.md) | `WorldCache`、`InventoryCache`、区块解码、实体追踪 |
| 13 | [Actions-Core 数据注册表](docs/zh-cn/13-Actions-Core-数据注册表.md) | 由 JSON 和注册表驱动的方块/工具属性（原版精确的破坏时间） |
| 14 | [Actions-Core 命令](docs/zh-cn/14-Actions-Core-命令.md) | 内置 6 条命令（`/whereiam`、`/goto`、`/harvest`…）与 Executor 模式 |

---

## 快速上手

### 1. 插件结构
插件 JAR 文件应在其根目录下包含插件清单文件 `plugin.json`。
```text
my-plugin.jar
├── plugin.json                    ← 位于 JAR 根目录的清单文件
└── org/
    └── me/
        └── plugin/
            └── MyPlugin.class     ← 已编译入口类
```

### 2. plugin.json
清单文件 `plugin.json` 旨在指定插件的入口类、插件名称及版本。  
**配置 plugin.json 的示例：**
```json
{
  "entry": "org.me.plugin.MyPlugin",
  "name": "MyPlugin",
  "version": "1.0.0"
}
```  
**入口类的定位方式：**  
在加载插件时，DolphinBot 会查找 `plugin.json` 中定义的目标入口类；如果 `plugin.json` 不存在，它会扫描所有继承自 `AbstractPlugin` 的类。若未能找到相应的类，则会跳过并忽略该插件。  
### 3. 入口类
插件入口类应继承 AbstractPlugin 类：
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
        getLogger().info("再见。");
    }
}
```

### 4. 扔进插件目录

```text
target/plugins/my-plugin.jar          ← 对所有 Bot Profile 生效
target/plugins/<ProfileName>/my-plugin.jar  ← 仅对某一个 Bot Profile 生效
```

> [!IMPORTANT]
> 框架要求 **shaded JAR**（依赖打包进 jar）。Maven 模板见 [快速上手 § Maven POM 模板](docs/zh-cn/02-快速上手.md#maven-pomxml-模板)。

---

## Actions-Core：内置的模块化插件

DolphinBot 自带 `Actions-Core`——一个**全模块化的交互插件**，展示了本指南覆盖的所有模式。它是你扩展功能的首选起点：

- ✅ 方块交互（放置、挖掘、右键）
- ✅ 移动 / 寻路
- ✅ 地形查询（区块解码、方块查找）
- ✅ 背包感知的工具选择
- ✅ 多插件并发时的异步安全保障

> [!NOTE]
> Actions-Core **不是框架的可选依赖**——它是普通的插件 JAR。你可以直接使用它、扩展它的模块、或完全移除它。

深入阅读 [Actions-Core 架构](docs/zh-cn/10-Actions-Core-架构.md)。

---

## 🧭 我该从哪篇开始读？

| 我要…… | 先读这篇 |
|--------|---------|
| 写一个基础插件 | [快速上手](docs/zh-cn/02-快速上手.md) → [生命周期](docs/zh-cn/03-生命周期.md) |
| 对包作出响应 | [包监听器](docs/zh-cn/06-包监听器.md) |
| 注册聊天/终端命令 | [命令系统](docs/zh-cn/07-命令系统.md) |
| 自定义服务器登录流程 | [LoginStateMachine](docs/zh-cn/04-LoginStateMachine.md) |
| 扩展 Actions-Core / 写新模块 | [Actions-Core 架构](docs/zh-cn/10-Actions-Core-架构.md) → [Actions-Core API](docs/zh-cn/11-Actions-Core-API.md) |
| 让 Bot 找方块 / 挖方块 / 放方块 | [世界模型](docs/zh-cn/12-Actions-Core-世界模型.md) + [数据注册表](docs/zh-cn/13-Actions-Core-数据注册表.md) |
| 多 Bot 协调 | [聊天与跨 Bot 消息](docs/zh-cn/08-聊天与跨Bot消息.md) |

---

> [!NOTE]
> 所有代码示例均使用 Java 17，编译目标为 `DolphinBot-1.6.0-ALPHA-full-shaded.jar`（模块 POM 中 system scope 引入）。Actions-Core 子模块即为本仓库内的实战样例。

---

## 许可证

与 DolphinBot 相同——GPL-3.0。见根目录 [LICENSE](./LICENSE) 文件。
