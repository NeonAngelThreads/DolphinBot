### Language: 
**简体中文** / [한국어](README_KO.md) / [Español](README_ES.md) / [Français](README_FR.md) / [Русский](README_RU.md) / [Português](README_PT.md) / [English](README.md)
# DolphinBot
DolphinBot 是一个无头 Minecraft 机器人框架，基于 GeyserMC MCProtocolLib 1.21.11-SNAPSHOT（协议 774）构建。
<p align="center">
   <img src="assets/logo.jpg" width="350" height="350">
   <br>
    <a href="https://www.oracle.com/java/technologies/downloads/">
        <img src="https://forthebadge.com/badges/made-with-java.svg" alt="java"/>
    </a>
    <a href="">
        <img src="https://forthebadge.com/api/badges/generate?panels=2&primaryLabel=MADE+WITH&secondaryLabel=%E2%9D%A4%EF%B8%8E&primaryBGColor=%23ff7f7f&secondaryBGColor=%23ff6b6b&primaryTextColor=%23FFFFFF&primaryFontSize=12&primaryFontWeight=600&primaryLetterSpacing=2&primaryFontFamily=Roboto&primaryTextTransform=uppercase&secondaryTextColor=%23FFFFFF&secondaryFontSize=16&secondaryFontWeight=900&secondaryLetterSpacing=2&secondaryFontFamily=Montserrat&secondaryTextTransform=uppercase" alt="MADE WITH LOVE">
    </a>
    <a href="">
        <img src="https://forthebadge.com/api/badges/generate?panels=2&primaryLabel=BUILT&secondaryLabel=WITH+HEART%21&primaryBGColor=%23ae3eb6&secondaryBGColor=%23d99cdd&primaryTextColor=%23FFFFFF&primaryFontSize=12&primaryFontWeight=600&primaryLetterSpacing=2&primaryFontFamily=Roboto&primaryTextTransform=uppercase&secondaryTextColor=%23000000&secondaryFontSize=12&secondaryFontWeight=900&secondaryLetterSpacing=2&secondaryFontFamily=Montserrat&secondaryTextTransform=uppercase&primaryIcon=uber&primaryIconColor=%23000000&primaryIconSize=16&primaryIconPosition=left" alt="BUILT WITH HEARTS">
    </a>
</p>
   <div align="center">
   ✨ 面向通用 Minecraft 服务器的轻量、高可扩展、跨版本机器人框架。它内置类 Bukkit 插件管理器和易用 API，允许你自定义事件处理。 ✨
   </div>
<p align="center">
  <a href="https://github.com/NeonAngelThreads/DolphinBot/releases">
    <img src="https://img.shields.io/github/v/release/NeonAngelThreads/DolphinBot" alt="Release"/>
  </a>
  <a href="https://github.com/NeonAngelThreads/DolphinBot/releases">
        <img src="https://img.shields.io/github/downloads/neonangelthreads/dolphinbot/total" alt="downloads"/>
  </a>
   <br>
   <a href="https://github.com/NeonAngelThreads/DolphinBot/commits/master/">
      <img src="https://img.shields.io/github/last-commit/NeonAngelThreads/DolphinBot" alt="commits"/>
   </a>
  <img src="https://img.shields.io/github/commit-activity/w/NeonAngelThreads/DolphinBot" alt="GitHub commit activity"/>
  <a href="https://github.com/NeonAngelThreads/DolphinBot/issues">
    <img src="https://img.shields.io/github/issues/NeonAngelThreads/DolphinBot" alt="issues"/>
  </a>
  <a href="https://github.com/NeonAngelThreads/DolphinBot/tree/master/src/main">
     <img src="https://img.shields.io/github/languages/code-size/NeonAngelThreads/DolphinBot" alt="GitHub code size"/>
  </a>
  <a href="https://app.codacy.com/gh/NeonAngelThreads/DolphinBot/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade">
     <img src="https://app.codacy.com/project/badge/Grade/ec90e9d4d7ef4023b124c4609b41a961"/>
  </a>
  <p align="center">
     <a href="https://github.com/NeonAngelThreads/DolphinBot/blob/master/PluginDocs.md">📖文档</a>
     ·
     <a href="https://github.com/NeonAngelThreads/DolphinBot/issues">🐛提交建议/缺陷</a>
</p> 


## 为什么选择 DolphinBot？
   - **跨版本**：集成 ViaVersion，DolphinBot 可支持 1.7.x 到 1.21.11 与 26.1 的多版本服务器。
   - **高可靠**：网络占用低，断线自动重连，可长期运行而无需担心掉线。
   - **高可编程性**：DolphinBot 实现了类似 **SpringBoot** 的有限 **状态机**，可为不同服务器轻松配置**多种登录流程**。
   - **高可扩展性**：DolphinBot 内置成熟的 DolphinAPI，包含多种 `数据包监听器`、`事件系统` 和基于 `mc protocol lib` 的易用 `事件处理器`，
     并集成类 Bukkit 插件 API，让你能在很短时间内开发自定义插件。
   - **高级日志系统**：DolphinAPI 还实现了 `TextComponent` 序列化器，可解析服务器消息的丰富颜色和样式，并提供更多调试信息。
   - **高性能**：DolphinBot 允许在单个客户端启动多个机器人实例，CPU 和内存占用低。
   - **易用**：可将机器人配置文件放入配置文件中，无需命令行定义，快速启动。
### **快捷链接**：[自定义插件开发指南](PluginDocs.md)
## 功能：
   - 使用 CommandBuilder DolphinAPI 轻松注册以 `!` 开头的自定义命令。
   - 可在连接服务器期间**热注入**插件。
   - 支持配置机器人集群并一次性启动。
   - 在 `2b2t.xin` 中自动回答问题以加快登录流程。
   - 支持为多个机器人配置代理设置。
   - **Web 控制台 API** - 内置 HTTP API 服务器，用于远程机器人管理与控制。
   - **实时日志流** - 基于 WebSocket 的日志流，支持 ANSI 颜色。
   - **Tab 补全** - Web 终端支持自动补全，体验与桌面终端一致。
   - **多终端管理** - 每个机器人拥有独立终端实例，并可通过标签页切换。

## 截图：
### 在 Windows Server 2019 上运行：
<p align="center">
   <img src="assets/dev2.png">
</p>

## 介绍：
   **功能分类：**
-  [`跨版本`](#cross-version-support)
- [`热重载插件`](#hot-swapping-plugins-in-game)
- [`终端交互`](#interactions-in-terminal)
- [`数据包调试器`](#config-file-setting-)
- [`代理设置`](#config-file-setting-)
- [`DolphinBot Web 仪表盘`](#web-console)

**已实现的事件 API：**
- [`可编程状态机`](PluginDocs.md#3-programmable-login-state-machine)
- [`命令系统`](PluginDocs.md#42-commands-system)
- [`数据包处理器`](PluginDocs.md#4-deep-understand-dolphinapis)
- [`事件处理器系统`](PluginDocs.md#3-register-event-handlers)
   - [`玩家事件`](PluginDocs.md#player-events)
- [`强制 Unicode 聊天`](PluginDocs.md#unicode-string-helper)

## 跨版本支持
**现在 DolphinBot 能够动态切换多个 Minecraft 版本。**   
DolphinBot 会自动检测服务器协议版本并选择最佳版本使用，可用版本列表如下：

| 支持版本 | 协议 ID 范围 | 支持状态 |
|--------------------|-------------------|--------------|
| 26.1.x             | 775-777           | ✓            |
| 1.21.x             | 767-774           | ✓ 基础       |
| 1.20.x             | 761-766           | ✓            |
| 1.19.x             | 759-760           | ✓            |
| 1.18.x             | 757-758           | ✓            |
| 1.17.x             | 755-756           | ✓            |
| 1.16.x             | 735-754           | ✓            |
| 1.15.x             | 573-578           | ✓            |
| 1.14.x             | 477-498           | ✓            |
| 1.13.x             | 393-404           | ✓            |
| 1.12.x             | 335-340           | ✓            |
| 1.11.x             | 315-316           | ✓            |
| 1.10.1             | 210               | ✓            |
| 1.9.x              | 107-110           | ✓            |
| 1.8.x              | 47                | ✓            |
| 1.7.x              | 4-5               | ✓            |

你可以在 DolphinBot 中调试 ViaVersion，以检查数据包如何被转换。  
要启用 ViaVersion 调试模式，可为 Java 添加 JVM 选项 `-Dvia.debug=true`。
```bash 
java -Dvia.debug=true -jar "DolphinBot-[version].jar"
```
## 终端交互
- 你可以从 DolphinBot 终端发送游戏内消息或执行命令。
- 内置命令：
-
  |         终端命令         | 描述                                       |
  |:---------------------------------:|---------------------------------------------------|
  | `reload <plugin Name> [bot name]` | 热重载指定插件                  |
  |  `load <pLugin Name> [bot name]`  | 热加载指定插件                    |
  |        `reconnect` (`rc`)         | 在游戏中重新连接服务器。                  |
  |     `license`    (`l`, `lic`)     | 在终端显示许可证                  |
  |        `help`   (`h`, `?`)        | 显示命令菜单及每个命令用法 |
  |         `respawn` (`rs`)          | 使机器人重生。                                  |
   
- 对于这些命令，你可以按 `TAB` 自动补全。
## 快速开始
本节将说明以下内容：
  - **1. 如何通过命令行直接启动单个机器人。**  
  - **2. 如何不通过命令行而使用配置文件指定机器人配置。**  
  - **3. 如何同时启动多个机器人并配置代理。**  
  - **4. 如何配置高级选项。**  
  - **5. 如何制作自定义插件。**  
  - **6. 如何使用 Web 控制台进行远程管理。**  

1. **下载客户端**  
   下载 jar 归档文件：`DolphinBot-[version]-full.jar`。  

> [!IMPORTANT]
> 要求：**Java 版本 >= 17**

2. **机器人配置**  
### 配置 Profile 
   有两种设置机器人配置的方式：
   - 如果只想简单快速启动，可使用**命令行设置**  
   - 如果想一次启动多个机器人并使用高级选项，可使用**配置文件设置**    

1. **命令行设置**  
     游戏内配置应在以下启动命令行中定义。  
     参数列表示例：
     ```bash
     java -jar "DolphinBot-[version]-full.jar" --username=[username] --password=[password] --skin-recorder=[enable/disable]
     ```
   | 命令参数  | 描述                                                                |
   |--------------------|----------------------------------------------------------------------------|
   | `--username`       | 机器人在游戏内显示的名称。                                            |
   | `--password`       | 登录或注册密码。                                            |
   | `--auto-reconnect` | 被踢或因其他原因断开时是否重连服务器。 |
   | `--skin-recorder`  | 是否自动捕获并保存在线玩家皮肤。                  |
   | `--server`         | 目标服务器地址。                                                     |
   | `--port`           | 目标服务器端口。                                                        |

   示例：
     ```bash
     java -jar "DolphinBot-[version]-full.jar" --username=[username] --password=[password] --server=0.0.0.0 --port=25565
     ```
     ```bash
     java -jar "DolphinBot-[version]-full.jar" --username=Dolphin1 --password=123 --server=2b2t.xin --port=25565 --owner=Melibertan
     ```
   <p align="center">
     <img src="assets/dolphinbot-profile.png" alt="profile list">
   </p>
         
> [!NOTE]
> 命令行优先级高于配置文件，若选项重复，只识别命令行并忽略配置文件中的对应项。  
   
> [!TIP]
> 可选地，你可以添加参数指定更多选项：  
   `--owner`：指定谁可以使用此机器人。  
   
  **示例：**   
   `--owner=Melibertan`，当然也可以定义多个名称。每个所有者名称用 `;` 分隔。  
   **示例：**  
   `--owner=owner1;owner2;owner3;...`  

   ### 配置文件设置    
   配置文件包括功能配置 `bot.config.global.json` 和 Profile 配置 `bot.profiles.json`。  
      你也可以将上述 Profile 参数按照以下格式移入配置文件 `bot.profiles.json`，其中所有配置值都会被加载。
      DolphinBot 会优先应用命令行选项，配置文件中重复的选项会被忽略。    
      配置文件路径可选，使用选项 `--config-file` 定位配置目录或文件。  
      例如：  
   ```bash 
   java -jar "DolphinBot-[version].jar" --config-file=path/to/config.json
   ```
   如果你指定的路径是目录而不是文件，Dolphin 会在该目录中提取配置文件作为默认配置。  
   ```bash
   java -jar "DolphinBot-[version].jar" --config-file=path/to/config_directory
   ```
   如果缺少 `--config-file` 参数，DolphinBot 会在 jar 所在目录创建默认文件。  
   ```bash 
   java -jar "DolphinBot-[version].jar"
   ```
   **多机器人与代理设置**  
      在 Profile 配置文件中，你可以在 `bot.profiles.json` 中创建 `profiles` 字段，以指定多个机器人 Profile 登录服务器。  
               
> [!NOTE]
> 某些服务器可能禁止同一 IP 启动多个机器人，代理设置旨在帮助你在不同网络环境或需要不同出口 IP 时运行多个机器人。  
     
要为每个机器人配置代理，需要编辑 `proxy` 字段。示例如下：
        
> [!WARNING]
> 定义多个机器人可能触发反机器人或反作弊机制，部分策略严格的服务器可能禁止此行为。
    
```json
{
   "profiles": {
      "bot#1": {
         "name": "Player494",
         "password": "123example",
         "owner": ["player_name"],
         "enabled_plugins": [
            "QuestionAnswerer",
            "MessageDisplay",
            "HumanVerify"
         ],
         "proxy": {
            "enabled": false,
            "info": {
               "address": "XX.XXX.XXX.XX",
               "port": 8081,
               "type": "SOCKS4",
               "username": "",
               "password": ""
            }
         }
      },
      "bot#2": {
         "name": "Player495",  
         "password": "password",
         "owner": ["player_name", "other_owner"],
         "enabled_plugins": [
            "HumanVerify"
         ],
         "proxy": {
            "enabled": false,
            "info": {"...": "..."}
         }
      },
      "bot#3": {"...": "..."}
   }
}
```
          
   1. `enabled_plugins` 字段表示应在此机器人上启用哪些插件。   
   2. `proxy` 字段（可选）表示每个机器人的代理配置，字段 `enabled` 标记是否启用此代理设置，   
              字段 `info` 包含：
      
      | 字段       | 描述                                     |
      |-------------|-------------------------------------------------|
      | `address`   | 代理服务器远程 IP 地址或主机名。 |
      | `port`      | 代理服务器端口。                              |
      | `type`      | 代理模式。（`HTTP`、`SOCKS4`、`SOCKS5`）        |

> [!TIP]
> “username”、“password” 是可选的；如果远程代理服务器需要认证，则需要添加。

   在这种情况下，如果你想将 `bot#1` 作为单个机器人加载，应添加以下参数：  
   ```bash
   java -jar "DolphinBot-[version].jar" --config-file=path/to/config_directory --profiles="bot#1"
   ```
   ```bash
   java -jar "DolphinBot-[version].jar" --profiles="bot#1"
   ```   
   如果你想同时启动多个机器人，可在选项 `--profiles` 中以列表形式指定多个 Profile 名称，每个 Profile 名称用 `;` 分隔。

   **示例：**
   ```bash
   java -jar "DolphinBot-[version].jar" --profiles="bot#1;bot#2;bot#3;..."
   ```

> [!NOTE]
> 如果缺少 `--profiles` 选项，默认会加载 Profile 配置中的所有机器人。  

**所有者：**  
   如果你希望限制机器人只能由指定玩家使用，可以将玩家名称放入 `owner` 列表。  
   **示例：**
```json
{
   "profiles": {
      "bot#1": {
         "name": "Player494",
         "password": "123example",
         "owner": [
            "owner1", 
            "owner2",
            "owner3"
         ],
         "enabled_plugins": [ "QuestionAnswerer", "MessageDisplay", "HumanVerify" ],
         "proxy": {
            "enabled": false,
            "info": {
               "address": "XX.XXX.XXX.XX",
               "port": 8081, 
               "type": "SOCKS4",
               "username": "",
               "password": ""
            }
         }
      }
   }
}
```
### 高级配置（可选）
   如果你想访问更多高级配置，可编辑 `bot.config.global.json`。  
   每个配置选项都等同于命令行定义的选项，所有配置值（包括未识别选项）都会被解析，因此你可以添加自定义配置选项。  
   配置此文件的示例：
```json
{
  "server": "2b2t.xin",
  "port": 25565,
  "auto-reconnect": true,
  "packet-filter-delay": 0,
  "msg-send-delay": 0,
  "max-chunk-view": 12,
  "language": "zh",
  "connect-timing-out": 2000,
  "reconnect-delay": 5000,
  "debug-settings": {
    "enable-packet-debug": false,
    "packet-warning": true
  },
  "other": {
    "enable-skin-recorder": false
  }
}
```   
   ### 配置选项：
   
   | 配置                 | 描述                                                                |
   |------------------------|----------------------------------------------------------------------------|
   | `server`               | 用于定义服务器地址。                                               |
   | `port`                 | 用于定义服务器端口。                                                  |
   | `auto-reconnecting`    | 被踢或因其他原因断开时是否重连服务器。 |
   | `enable-skin-recorder` | 是否启用皮肤记录器。                                              |
   | `packet-filter-delay`  | 每个目标数据包之间的最大接收延迟（毫秒）。                   |
   | `max-chunk-view`       | 区块数据包接收的最大范围。                                       |
   | `connect-timing-out`   | 判定连接超时所需毫秒数。           |
   | `reconnect-delay`      | 重连服务器时的最小冷却延迟（毫秒）。                |
   | `msg-send-delay`       | 发送游戏内消息的延迟。                                     |
   | `enable-packet-debug`  | 是否启用数据包调试器。                                            |
   | `packet-warning`       | 是否显示数据包错误。                                              |
   | `language`             | UI 区域设置。（目前支持 `zh`、`en` 语言）          |


## Web 控制台
DolphinBot 现在包含内置 Web 控制台，用于远程机器人管理与控制。你可以通过现代 Web 界面管理机器人，而无需直接访问终端。

### 启动 Web 控制台
要启用 Web 控制台，只需在启动 DolphinBot 时添加 `--api` 参数：

```bash
java -jar "DolphinBot-[version]-full.jar" --api [port]
```

API 服务器将在指定端口启动（默认：25560），WebSocket 日志服务器将在 port+1 启动（默认：25561）。

### Web 控制台功能
- **机器人管理仪表盘** - 查看所有机器人实例的实时状态、游戏模式和位置
- **独立终端** - 每个机器人都有独立终端实例，并支持标签页切换
- **实时日志流** - 所有日志都会流式传输到 Web 终端，完整支持 ANSI 颜色
- **Tab 补全** - 自动补全与桌面终端完全一致
- **现代 UI/UX** - 美观的模态对话框、轻提示和响应式设计
- **机器人统计** - 可视化环形进度显示在线率，并用颜色编码状态

### 访问 Web 控制台
API 服务器运行后，可通过以下地址访问 Web 控制台：
```
http://localhost:8080
```

Web 控制台作为独立的 Spring Boot 应用位于 `web-console/` 目录。详细设置和使用说明请参阅 [Web 控制台文档](web-console/README.md)。

### API 端点
内置 HTTP API 提供以下端点：

| 端点 | 方法 | 描述              |
|-----------|----------|--------------------------|
| `/api/health` | GET | 健康检查             |
| `/api/bots` | GET | 列出所有机器人            |
| `/api/bots/start` | POST | 启动机器人              |
| `/api/bots/stop` | POST | 停止机器人              |
| `/api/bots/send-command` | POST | 向机器人发送命令    |
| `/api/config` | GET/POST | 获取/更新配置 |
| `/api/bot/create`      |   POST       | 创建新机器人           |             
| `/api/bot/delete`      |     POST         | 删除指定机器人     |      

### WebSocket 端点
| 端点 | 描述 |
|-----------|-------------|
| `ws://localhost:25561` | 日志流和 Tab 补全 |

## 常见问题

- 我可以为 DolphinBot 制作插件吗？   
    可以，DolphinBot 拥有易用的插件系统，并聚合类 Bukkit API，这里有[完整开发指南。](PluginDocs.md)


- 配置文件很难配置吗？  
    不难，初始配置对大多数情况已足够使用。


- 如何提交问题或请求功能？  
    在 GitHub Issues 中打开 issue。详细步骤或复现方式会更有帮助。


- 我可以加入 DolphinBot 开发吗？  
    当然！你可以成为 DolphinBot 团队的**第二位贡献者**！可随时自由加入。

### 我们的第一位贡献者：
1. huangdihd - （在提交 `#372990a` 中修复了一个关键缺陷）
## 社区
  - 遇到缺陷？欢迎提交 issue 和建议！  
  我的 Bilibili 空间：
  https://m.bilibili.com/space/386644641  
  - 如果你喜欢 DolphinBot，点一个 **star** 会很有帮助！

## 许可证
GPL-3.0 或更高版本，请参阅[完整 LICENSE](LICENSE)。

### **By NeonAngelThreads, coding with ❤️**