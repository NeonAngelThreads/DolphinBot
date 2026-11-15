> [!TIP] 快速导航  
> - 返回 [README.md](./README.md)
> ### [English](./PluginDocs.md) / `简体中文`
> _(想提出新的翻译？欢迎提交 PR 告诉我！)_

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
./plugins/my-plugin.jar                     ← 对所有 Bot Profile 生效
./plugins/<ProfileName>/my-plugin.jar       ← 仅对某一个 Bot Profile 生效
```

> [!IMPORTANT]
> 框架要求 **shaded JAR**（依赖打包进 jar）。Maven 模板见 [快速上手 § Maven POM 模板](docs/zh-cn/02-快速上手.md#maven-pomxml-模板)。

---

## Actions-Core：内置的模块化插件

DolphinBot 自带 `Actions-Core`——一个**全模块化的交互依赖库**，提供了终端命令驱动的游戏任意交互。它也可以成为你扩展自定义交互起点：

- ✅ 方块交互（放置、挖掘、交互）
- ✅ 移动 / 寻路
- ✅ 地形管理（区块解码、方块放置/挖掘、方块查找）
- ✅ 背包感知的工具选择
- ✅ 多插件并发时的异步安全保障

> [!NOTE]
> Actions-Core **是框架的可选依赖**，它是普通的插件 JAR。你可以直接使用它、也可以扩展它的模块。

深入阅读 [Actions-Core 用法](docs/zh-cn/14-Actions-Core-命令.md)。

---

## Dolphinbot插件市场
正在寻找符合你需求的插件？[这个链接](https://github.com/NeonAngelThreads/dolphinbot-plugins)提供了官方和社区插件，涵盖了
目前可用的自DolphinBot 1.7.0以上版本的**功能插件**、**依赖库插件**、**元插件**三种插件类型。

> [!NOTE]    
> *  **功能插件**： 提供具体功能的插件，如实现了DolphinBot、其他**依赖库插件**的原生接口、SPI的实现。一个**功能插件**可能依赖一个或多个
> **依赖库插件**或**元插件**。
> * **依赖库插件**： 提供一套具体的接口，如SPI，用于被一个或多个**功能插件**依赖和实现，同时也可以选择性的提供一些基本的功能（如[Actions-core]()）。
> 一个**依赖库插件**可能依赖一个或多个**依赖库插件**或**元插件**。
> * **元插件**：只提供和定义针对特定服务器的基本交互协议（保证正常进入主服务器为最终目的），如特定`登录流程`/`验证流程`/`进入主大厅的流程`的实现。
> 因为某些复杂的流程可能涉及自定义交互（如移动、实体交互、方块交互），因此一个**元插件**可能也依赖一个或多个**依赖库插件**（如[Actions-core]()）。

上述的插件类型在[dolphinbot-plugins仓库](https://github.com/NeonAngelThreads/dolphinbot-plugins)中公开，所有基于DolphinBot的插件
都使用与 DolphinBot 相同的**GPL-3.0**协议（见每个插件的LICENSE文件）

## 分享你制作的插件
想为DolphinBot社区做出贡献？欢迎在[issue](https://github.com/NeonAngelThreads/dolphinbot-plugins/issues)/[PR](https://github.com/NeonAngelThreads/dolphinbot-plugins/pulls)面板中提出一切的插件投稿！

希望在Bilibili频道上亮相自己实现的有趣插件？欢迎在[bilibili@颜控小帕](https://space.bilibili.com/386644641)私信与我联系。

## 我该从哪篇开始读？

| 我要…… | 建议先读                                                                                      |
|--------|-------------------------------------------------------------------------------------------|
| 写一个基础插件 | [快速上手](docs/zh-cn/02-快速上手.md) → [生命周期](docs/zh-cn/03-生命周期.md)                             |
| 对包作出响应 | [包监听器](docs/zh-cn/06-包监听器.md)                                                             |
| 注册聊天/终端命令 | [命令系统](docs/zh-cn/07-命令系统.md)                                                             |
| 自定义服务器登录流程 | [LoginStateMachine](docs/zh-cn/04-LoginStateMachine.md)                                   |
| 让 Bot 找方块 / 挖方块 / 放方块 | [世界模型](docs/zh-cn/12-Actions-Core-世界模型.md) + [数据注册表](docs/zh-cn/13-Actions-Core-数据注册表.md) |
| 多 Bot 协调 | [聊天与跨 Bot 消息](docs/zh-cn/08-聊天与跨Bot消息.md)                                                 |
| 扩展 Actions-Core / 写新模块 | [Actions-Core API](docs/zh-cn/11-Actions-Core-API.md)                                     |


---

> [!NOTE]
> 所有代码示例均使用 Java 17，编译目标为 `DolphinBot-1.6.0-ALPHA-full-shaded.jar`（模块 POM 中 system scope 引入）。Actions-Core 子模块即为本仓库内的实战样例。

---

## 许可证

与 DolphinBot 相同——GPL-3.0。见根目录 [LICENSE](./LICENSE) 文件。
