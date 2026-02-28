## 语言： `简体中文` / [English](README.md)
# DolphinBot - 重装上阵

<p align="center">
   <img src="assets/logo.jpg" width="350" height="350">
   <br>
    <a href="https://www.oracle.com/java/technologies/downloads/">
        <img src="https://forthebadge.com/images/badges/made-with-java.svg" alt="java"/>
    </a>
</p>

   <div align="center">
   ✨ 一款轻量级、可靠、智能的MC机器人，适用于广泛Minecraft服务器，具有高可扩展性和高性能。它集成了Bukkit等插件加载器和易于使用的Bukkit风格API，允许您自定义事件处理。 ✨
   </div>
<p align="center">
  <a href="https://github.com/NeonAngelThreads/DolphinBot/releases">
    <img src="https://img.shields.io/github/v/release/NeonAngelThreads/DolphinBot" alt="Release"/>
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
  <p align="center">
     <a href="https://github.com/NeonAngelThreads/DolphinBot/blob/master/PluginDocs.md">📖开发文档</a>
     ·
     <a href="https://github.com/NeonAngelThreads/DolphinBot/issues">🐛提交建议/错误报告</a>
  </p>
</p>

## 为什么选择 DolphinBot？
   - **高可靠性**, 网络利用率低，长时间运行无需担心连接丢失，断开连接后自动重连。  
   - **高可编程性**, DolphinBot 实现了类似 **SpringBoot** 的 **状态机**，让您可以轻松地为不同的服务器配置 **自定义登录流程**。
   - **高可扩展性**, DolphinBot 嵌入了成熟的 DolphinAPI，其中包含基于 mc 协议库的各种`数据包监听器`、`事件系统`和易于使用的`事件处理接口`，它集成了类似 Bukkit 的插件 API，使您可以在很短的时间内开发自定义插件。
     
   - **高级日志系统**，DolphinAPI 还实现了 `TextComponent` 序列化器，用于解析服务器消息的丰富颜色和样式，并提供更多有用的调试信息。
   - **高性能**，DolphinBot允许您在单个Java客户端上启动多个机器人实例，同时保持超低的 CPU 和内存占用率。
   - **易于使用**，直接运行，您可以将机器人配置文件放入配置文件中，而不是在命令行中定义，快速启动。
### **快捷方式**: [自定义插件开发指南](PluginDocs.md)
## 功能特性:
   - 使用 CommandBuilder 的 DolphinAPI，可以轻松注册以 `!` 开头的自定义命令。
   - 加入服务器后支持直接`/load`**热注入**插件，和`/reload`热重载插件。
   - 友好的终端体验，支持丰富颜色和样式。
   - 支持配置机器人集群，并立即启动。
   - 支持彩色控制台日志字符串表达式 `colorizeText("&6Hello &lWorld")`。
   - `2b2t.xin` 中自动回答问题，以加快登录过程。
   - 支持自定义登录流程。
   - **Web控制台API** - 内置HTTP API服务器，支持远程机器人管理和控制。
   - **实时日志流** - 基于WebSocket的日志流，支持ANSI彩色输出。
   - **Tab补全** - Web终端支持自动补全，与桌面终端体验一致。
   - **多终端管理** - 每个机器人拥有独立的终端实例，支持标签页切换。

## 截图:
### 在 Windows server 2019 表现效果:
<p align="center">
   <img src="assets/dolphinbot1.png">
</p>

## 介绍:
   **特色功能:**  
   - [`热重载插件`](#hot-swapping-plugins-in-game)
- [`终端交互`](#终端交互)
- [`数据包调试器`](#config-file-setting)
- [`代理设置`](#config-file-setting)
- [`DolphinBot Web 控制台`](#web控制台)

**已实现事件 API:**
- [`可编程状态机`](PluginDocs.md#3-programmable-login-state-machine)
- [`命令系统`](PluginDocs.md#42-commands-system)
- [`数据包处理程器`](PluginDocs.md#4-deep-understand-dolphinapis)
- [`事件处理系统`](PluginDocs.md#3-register-event-handlers)
   - [`玩家事件API`](PluginDocs.md#player-events)
- [`强制 Unicode 聊天`](PluginDocs.md#unicode-string-helper)

## 终端交互
- 您可以通过 DolphinBot 终端发送游戏消息，或执行命令。
- 内置命令:
- 
  | 终端命令                        | 用途描述          |
  |-----------------------------|---------------|
  | `reload <插件名称> [机器人名称]` | 热重载指定的插件      |
  | `load <插件名称> [机器人名称]`   | 热加载（热注入）指定的插件 |
  |                  `respawn`                   | 在游戏中重生机器人                       |
  |          `license`    (`l`, `lic`)           | 在终端显示许可证                    |
  |               `help`   (`h`, `?`)              | 显示命令菜单和每个命令的用法   |
   
- 对于这些命令，您可以按`TAB`键自动补全。
## 快速入门
在本节中，您将了解以下操作方法：    
  - **1. 如何直接使用命令行启动单个机器人。**  
  - **2. 如何在不使用命令行的情况下通过配置文件指定机器人配置文件？**  
  - **3. 如何同时启动多个机器人**  
  - **4. 如何配置高级选项**  
  - **5. 如何制作自定义插件**  
  - **6. 如何使用Web控制台进行远程管理**  

1. **下载客户端**  
   下载 jar 归档文件: `DolphinBot-[version]-full.jar`.  
   系统要求：**Java 版本 >= 17**
2. **机器人配置**  
**配置配置文件**  
   设置机器人配置有两种不同的方法：
   - 如果您为了简单快速地启动并且只启动一个机器人，可以使用**命令行设置**。   
   - 如果您想同时启动多个机器人并访问高级选项，可以使用**配置文件设置**。    

   1. **命令行设置**  
        游戏内配置文件应在以下启动命令行中定义。  
        参数列表示例：
        ```bash
        java -jar "DolphinBot-[version]-full.jar" --username=[username] --password=[password] --skin-recorder=[enable/disable]
        ```
      `--username` : 游戏内显示机器人名称。   
      `--password` : 登录或注册密码。  
      `--auto-reconnect` : 被踢出服务器或因某些原因断开连接后是否重新连接。  
      `--skin-recorder` : 是否自动捕获并保存在线玩家的皮肤。  
      `--server` : 目标服务器地址。  
      `--port` : 目标服务器端口。  
      Example:
        ```bash
        java -jar "DolphinBot-[version]-full.jar" --username=[username] --password=[password] --server=0.0.0.0 --port=25565
        ```
      或者：
        ```bash
        java -jar "DolphinBot-[version]-full.jar" --username=Dolphin1 --password=123 --server=2b2t.xin --port=25565 --owner=Melibertan
        ```
      命令配置文件将被加载：
      <p align="center">
        <img src="assets/dolphinbot-profile.png" alt="profile list">
      </p>
      [warn]
      **警告：** 命令行参数的权限高于配置文件，这意味着如果选项重复，则只会识别命令行参数，且忽略配置文件中的选项。  
      您还可以通过添加参数来指定更多选项：  
      `--owner` : 仅指定哪些人可以使用此机器人。  
   2. **配置文件设置**  
         配置文件包括功能配置文件 `mc.bot.config.json` 和账户配置文件 `bot.profiles.json`  
         您还可以按照以下格式将上述配置文件参数移至配置文件`bot.profiles.json`中，其中的所有配置值都将被加载。  
         DolphinBot 将首先应用命令行选项，配置文件中重复的选项将被忽略。  
         指定配置文件路径是可选的，使用选项 `--config-file` 来定位配置目录或文件。  
         例如:  
         ```bash 
         java -jar "DolphinBot-[version].jar" --config-file=path/to/config.json
         ```
         如果您指定的路径是目录而不是文件，DolphinBot 会将配置文件提取到该目录中作为默认配置。
         ```bash
         java -jar "DolphinBot-[version].jar" --config-file=path/to/config_directory
         ```
         如果缺少 `--config-file` 参数，DolphinBot 将在 jar 目录中创建一个默认文件。  
         ```bash 
         java -jar "DolphinBot-[version].jar"
         ```
         
         在配置文件中，您可以在 `bot.profiles.json` 中创建 `profiles` 键，以指定要记录到服务器的多个机器人配置文件。    
         
> [!NOTE]
> 一些服务器可能禁止从同一IP启动多个机器人，代理设置旨在帮助您从不同的网络环境运行多个机器人或需要不同的出口IP。
         
要为每个机器人配置代理设置，您需要编辑 `proxy` 字段。示例如下：
        
> [!WARNING]
> 定义多个机器人可能会触发反机器人或反作弊机制，一些策略严格的服务器可能会禁止这种行为。
    
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
          
   1. `enabled_plugins` 字段表示应该在机器人上启用哪些插件。   
   2. `proxy` 字段（可选）表示每个机器人的代理配置，字段 `enabled` 标记是否激活此代理设置，   
               字段 `info` 包含：
      
      | 字段       | 描述                                     |
      |-------------|-------------------------------------------------|
      | `address`   | 代理服务器的远程IP地址或主机名。 |
      | `port`      | 代理服务器端口。                              |
      | `type`      | 代理模式。(`HTTP`, `SOCKS4`, `SOCKS5`)        |

> [!TIP]
> "username", "password" 是可选的，如果远程代理服务器需要认证，则需要添加这些。  

在这种情况下，如果您想将 `bot#1` 作为唯一的机器人加载，则应添加以下参数：  
```bash
java -jar "DolphinBot-[version].jar" --config-file=path/to/config_directory --profiles="bot#1"
```  
or
```bash
java -jar "DolphinBot-[version].jar" --profiles="bot#1"
```   
如果要同时启动多个机器人，请在 `--profiles` 选项中指定多个配置文件名称列表，

每个配置文件名称之间应以“;”分隔。

**Examples:**  
```bash
java -jar "DolphinBot-[version].jar" --profiles="bot#1;bot#2"
```  
```bash
java -jar "DolphinBot-[version].jar" --profiles="bot#1;bot#2;bot#3;..."
```
- **警告**: 如果没有 `--profiles` 选项，则默认情况下会加载配置文件中的所有机器人。

**Owners 参数:**  
   如果你想限制某个机器人只能由指定的玩家使用，你可以将玩家姓名作为列表添加到 `owner` 中。  
   **示例:**
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

或者您也可以直接通过命令行进行定义。
**Example:**
`--owner=Melibertan`, 当然，您也可以定义多个名称。:  
每个owner名字应以“;”分隔。.  
**Example:**
`--owner=owner1;owner2;owner3;...`
   1. **高级配置（可选）**  
      如果你想访问更高级的配置，可以编辑`mc.bot.config.json`。  
      每个配置选项都与命令行定义的选项相对应，所有配置值（包括无法识别的选项）都将被解析，因此您可以添加自定义配置选项。  
      配置此文件的示例:
      ```json
      {
        "server": "2b2t.xin",
        "port": 25565,
        "auto-reconnect": true,
        "packet-filter-delay": 0,
        "msg-send-delay": 0,
        "max-chunk-view": 12,
        "anti-AFK": true,
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
      **配置选项:**
   
      | 配置项                 | 描述              |
      |------------------------|--------------------------|
      | `server`               | 用于定义服务器地址。               |
      | `port`                 | 用于定义服务器端口。               |
      | `auto-reconnecting`    | 被踢出服务器或因某些原因断开连接后是否重新连接。 |
      | `enable-skin-recorder` | 是否启用皮肤记录器。               |
      | `packet-filter-delay`  | 每个目标数据包之间的最大接收延迟（毫秒）。    |
      | `max-chunk-view`       | 最大区块数据包接收规模。             |
      | `connect-timing-out`   | 确定连接超时需要多少毫秒？            |
      | `reconnect-delay`      | 服务器重新连接时冷却所需的最小延迟（毫秒）。   |
      | `msg-send-delay`       | 游戏内消息发送延迟。               |
      | `enable-packet-debug`  | 是否启用数据包调试器。              |
      | `packet-warning`       | 是否显示数据包错误。              |
      | `language`             | UI语言设置。（目前支持 `zh`、`en` 语言）          |
      | `anti-AFK`             | 是否绕过AFK（挂机）检测。                       |
## Hot Swapping Plugins In-Game
Dolphin bot 支持在服务器中**热重载**和**热加载**（**热注入**）插件，无需退出整个客户端并重新连接到服务器。
您可以在服务器聊天中发送 `!reload <pluginName>` dolphin 命令。  
或者，您可以在终端中输入 `reload plugin.jar` 来热重载插件。

## Web控制台
DolphinBot现在包含内置的Web控制台，支持远程机器人管理和控制。这允许您通过现代化的网页界面管理机器人，无需直接访问终端。

### 启动Web控制台
要启用Web控制台，只需在启动DolphinBot时添加`--api`参数：

```bash
java -jar "DolphinBot-[version]-full.jar" --api [端口]
```

API服务器将在指定端口启动（默认：25560），WebSocket日志服务器将在端口+1启动（默认：25561）。

### Web控制台功能
- **机器人管理仪表板** - 查看所有机器人实例，包括实时状态、游戏模式和位置
- **独立终端** - 每个机器人拥有自己的终端实例，支持基于标签页的切换
- **实时日志流** - 所有日志都流式传输到Web终端，支持完整的ANSI彩色
- **Tab补全** - 自动补全功能与桌面终端完全一致
- **现代UI/UX** - 美观的模态对话框、气泡通知和响应式设计
- **机器人统计** - 可视化圆形进度显示在线率，根据状态颜色编码

### 访问Web控制台
API服务器运行后，您可以通过以下地址访问Web控制台：
```
http://localhost:8080
```

Web控制台作为独立的Spring Boot应用程序提供，位于`web-console/`目录中。详细设置和使用说明请参阅[Web控制台文档](web-console/README.md)。

### API端点
内置HTTP API提供以下端点：

| 端点                     | 方法 | 描述       |
|------------------------|----------|----------|
| `/api/health`          | GET | 健康检查     |
| `/api/bots`            | GET | 列出所有机器人  |
| `/api/bots/start`      | POST | 启动机器人    |
| `/api/bots/stop`       | POST | 停止机器人    |
| `/api/bots/send-command` | POST | 向机器人发送命令 |
| `/api/config`          | GET/POST | 获取/更新配置  |
| `/api/bot/create`      |   POST       | 创建新机器人   |
| `/api/bot/delete`      |     POST         | 删除机器人       |

### WebSocket端点
| 端点 | 描述 |
|-----------|-------------|
| `ws://localhost:25561` | 日志流和Tab补全 |

## FAQ

- 我可以为 DolphinBot 制作插件吗？   
    当然可以，DolphinBot 有一个易于使用的插件系统，聚合了类似 Bukkit 的 API，以下是[完整的开发指南](PluginDocs.md)


- 配置配置文件难吗？  
    不，初始配置足以满足大多数情况的需求。


- 我该如何提交问题（Issue）或提出功能请求？  
    请在 GitHub Issues 栏中提交问题。请根据模板提供详细步骤或复现步骤。


- 我可以参与DolphinBot的开发吗？  
    当然可以！您可以成为 DolphinBot 团队的**第二位贡献者**！您可以随时自由加入。   

### 我们的第一位贡献者:
1. huangdihd - (修复了提交(`#372990a`)中的一个关键bug)
## 社区
  - 遇到bug了吗？欢迎提出问题和建议！  
  我的Bilibili空间：
  https://m.bilibili.com/space/386644641  
  - 如果你喜欢 DolphinBot，欢迎点一颗小小的 Star！

## 开源协议
GPL-3.0 或更高版本，请参阅[完整开源许可](LICENSE).

### **By NeonAngelThreads, coding with ❤️**
