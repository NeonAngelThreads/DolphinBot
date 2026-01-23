# DolphinBot-Reloaded

<p align="center">
   <img src="assets/logo.jpg" width="350" height="350">
   <br>
    <a href="https://www.oracle.com/java/technologies/downloads/">
        <img src="https://forthebadge.com/images/badges/made-with-java.svg" alt="java"/>
    </a>
</p>

   <div align="center">
   ✨ A lightweight, reliable, intelligent MC bot for general minecraft server, with high-scalability and performance. It integrated plugin loaders like bukkit and easy-used interface styled APIs, allows you to customize event handles. ✨
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
     <a href="https://github.com/NeonAngelThreads/DolphinBot/blob/master/PluginDocs.md">📖Docs</a>
     ·
     <a href="https://github.com/NeonAngelThreads/DolphinBot/issues">🐛Submit Suggestion/Bug</a>
  </p>
</p>

## Why DolphinBot?
   - **High performance**, DolphinBot allows you to start multiple bot instances at one client with low CPU usage.   
   - **High reliability**, ability to bypass 2b2t's human verification, auto-reconnect when disconnected, long term running with no worries for losing connection.  
   - **High extensibility**, DolphinBot embedded mature DolphinAPI that contains variety of `packet listeners`, `event systems` and easy-used `event handlers` based on `mc protocol lib`,
     It integrates a bukkit-like plugin API, allowing you to develop custom plugins at very short time.  
   - **Advanced logging system**, DolphinAPI also implemented `TextComponent` serializer to parse rich colors and styles for server messages, with more useful information.
   - **Easy to use**, directly run, you can put the bot profile(s) into config file instead of defining on command-line, fast start.
### **Shortcuts**: [Custom plugin developing guideline](PluginDocs.md)
## Features:
   - Easy to register custom commands starting with `!` by using CommandBuilder DolphinAPIs
   - Bypassing human verification in most servers including `2b2t.xin`.
   - Supporting to configure the bot clusters, and start at once.
   - Supporting colourful console logging strings expression `colorizeText("&6Hello &lWorld")`.
   - Automatic answer questions in `2b2t.xin` for speeding up login process.
   - Supporting to reload a plugin while the client is running on server.

## Screenshots:
### Running on Windows server 2019:
<p align="center">
   <img src="assets/dolphinbot1.png">
</p>

## Introduction:
   **Features category:**  
   - [`Hot-Reloading Plugin`](#hot-swapping-plugins-in-game)
- [`Terminal Interactions`]()

**Implemented Event APIs:**

- [`Command System`](PluginDocs.md#2-commands-system)
- [`Packet Handlers`](PluginDocs.md#3-deep-understand-dolphinapis-)
- [`Event Handler System`](PluginDocs.md#3-register-event-handlers)
   - [`Player Events`](PluginDocs.md#player-events)
- [`Force Unicode Chat`](PluginDocs.md#unicode-string-helper)

Command-line Arguments
## Getting Started
In this section, you will understand below how-tos:  
  - **1. How to directly start a single bot with command-line.**  
  - **2. How to specify bot profile with config file without command-line.**  
  - **3. How to start multiple bot simultaneously**  
  - **4. How to configure advanced options**  
  - **5. How to make a custom plugin**  

1. **Download the Client**  
   Download the jar archive file: `DolphinBot-[version]-full.jar`.  
   Requirements: **Java version >= 17**
2. **Configuration of the Bot**  
**Configuring Profile**  
   There are two different ways to set bot config:
   - If you want to quickly start for simplicity and only one bot started, you can use **Command-line setting**  
   - If you would like to start multiple bot at once, and access advanced options, you can use **Config file setting**    

   1. **Command-line Setting**  
        In-game profile should be defined on below boot command-line.  
        An example of argument list:
        ```bash
        java -jar "DolphinBot-[version]-full.jar" -username=[username] -password=[password] -skin-recorder=[enable/disable]
        ```
      `--username` : in-game displaying name of bot.  
      `--password` : password for login or register.  
      `--auto-reconnect` : whether reconnect to server when got kicked or disconnect by some reasons.  
      `--skin-recorder` : whether automatic capture and save online players' skins.  
      `--server` : target server address.  
      `--port` : target server port.  
      Example:
        ```bash
        java -jar "DolphinBot-[version]-full.jar" --username=[username] --password=[password] --server=0.0.0.0 --port=25565
        ```
      or
        ```bash
        java -jar "DolphinBot-[version]-full.jar" --username=Dolphin1 --password=123 --server=2b2t.xin --port=25565 --owner=Melibertan
        ```
      Command profile will be loaded:
      <p align="center">
        <img src="assets/dolphinbot-profile.png">
      </p>
         
      **Warning:**  command-line has high authority than config file, meaning that if options are duplicated, will only recognize 
      command-line, and ignore config file one.  
      Optionally, you can specify more option by adding argument:  
      `--owner` : Specifying only who can use this bot.
   2. **Config File Setting**  
         Config files include functional config `mc.bot.config.json` and profile config `bot.profiles.json`  
         You can also move above profile arguments into config file ``bot.profiles.json`` following below formats, all config values in it will be loaded.
         DolphinBot will apply command-line options first, duplicated options in config file will be ignored.    
         To specify the path of config file is optional, Use option `-config-file` to locate config directory or file.  
         For example:  
         ```bash 
         java -jar "DolphinBot-[version].jar" -config-file=path/to/config.json
         ```
         If the path you specified is a directory instead of a file, Dolphin will extract config file as default config in this directory.  
         ```bash
         java -jar "DolphinBot-[version].jar" -config-file=path/to/config_directory
         ```
         If the `--config-file` parameter is absented, DolphinBot will create a default file on jar directory.  
         ```bash 
         java -jar "DolphinBot-[version].jar"
         ```
         
         In the profile config file, you can create `profiles` key in `bot.profiles.json` to specify multiple bot profiles to log to a server.  
         **Warning**: Defining multiple bots may trigger the anti-bot or anti-cheat, and some servers with strict policy may prohibit it.
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
                  ]
               },
               "bot#2": {
                  "name": "Player495",  
                  "password": "password",
                  "owner": ["player_name", "other_owner"],
         
                  "enabled_plugins": [
                     "HumanVerify"
                  ]
               },
               "bot#3": {"...": "..."}
            }
         }
         ```
         where `enabled_plugins` key represents which plugins should enable on the bot.
         In this case, if you want to load `bot#1` as your single bot, you should add below argument:  
         ```bash
         java -jar "DolphinBot-[version].jar" -config-file=path/to/config_directory -profiles="bot#1"
         ```  
         or
         ```bash
         java -jar "DolphinBot-[version].jar" -profiles="bot#1"
         ```   
         If you want to start multiple bot simultaneously, specify multiple profile name as a list in option `-profiles`, for
      each profile name, should be split with ";".

         **Examples:**  
         ```bash
         java -jar "DolphinBot-[version].jar" -profiles="bot#1;bot#2"
         ```  
         ```bash
         java -jar "DolphinBot-[version].jar" -profiles="bot#1;bot#2;bot#3;..."
         ```
       - **Warning**: If the `--profiles` option is absented, it will load all bots in profile config by default.

      **Owners:**  
      If you want to limit a bot can be only use by specified player(s) you can put player names into `owner` as list.  
      **Example:**
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

                  "enabled_plugins": [
                     "QuestionAnswerer",
                     "MessageDisplay",
                     "HumanVerify"
                  ]
               }
            }
         }
        ```

      Or you can directly define it by command line.  
      **Example:**
      `--owner=Melibertan`, of course, you also can define multiple names:  
      For each owner name, should be split with ";".  
      **Example:**
      `--owner=owner1;owner2;owner3;...`
   2. **Advanced Configurations (optional)**  
      If you want to access more advanced configs, you can edit `mc.bot.config.json`.  
      Every single config option is equilibrium to option that defined by command line, and all config value including
      unrecognized option will be parsed, so you can add your customize config options.  
      An example for configuring this file:
      ```json
       {
          "server": "2b2t.xin",
          "port": 25565,
          "auto-reconnecting": true,
   
          "packet-filter-delay": 3000,
          "max-chunk-view": 12,
   
          "connect-timing-out": 2000,
          "reconnect-delay": 3000
      }
      ```   
      Config Options:
   
      | Config                | Description                                                                |
      |-----------------------|----------------------------------------------------------------------------|
      | `server`              | For defining server address.                                               |
      | `port`                | For defining server port.                                                  |
      | `auto-reconnecting`   | Whether reconnect to server when got kicked or disconnect by some reasons. |
      | `packet-filter-delay` | Max receiving delay(millis) between every target packet.                   |
      | `max-chunk-view`      | Max scale of chunk packet receiving.                                       |
      | `connect-timing-out`  | How long millis does it take to determine a connection time out.           |
      | `reconnect-delay`     | Min delay(millis) for cooling down when reconnect a server.                |
## Hot Swapping Plugins In-Game
Dolphin bot supports you to hot-reload plugins in server, without quit the entire client and reconnecting to server.
You can send `!reload <pluginName>` dolphin command in server chat.
Console controlling are still in-developing, currently. 

## Interactions from Terminal (Beta)
- You can send in-game messages or execute commands form the dolphin bot terminal.
- Built-in commands (In-dev)

## FAQ

- Can I make my plugin for dolphin bot?   
    Yes, DolphinBot has an easy used plugin system, aggregating bukkit-like API, here is the [Full guideline for development.](PluginDocs.md)


- Is it difficult to configure the config files?  
    No, the initial config is highly sufficient to use for most cases.


- How do I put issues or request features?  
    Open an issue bar on GitHub Issues. Detailed steps or reproduce are appreciated.


- Can I join the DolphinBot development?  
    Sure! you can become the **first contributor** of DolphinBot team! you can join at any time freely. 

## Community
  - Encountered a bug? issues and suggestions are welcome!  
  My Bilibili space:
  https://m.bilibili.com/space/386644641

**By NeonAngelThreads, with ❤️**
