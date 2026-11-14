### Language: 
[简体中文](README_CN.md) / [한국어](README_KO.md) / [Español](README_ES.md) / **Français** / [Русский](README_RU.md) / [Português](README_PT.md) / [English](README.md)
# DolphinBot
DolphinBot est un framework de bots Minecraft sans interface graphique basé sur GeyserMC MCProtocolLib 1.21.11-SNAPSHOT (protocole 774).
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
   ✨ Un framework de bots léger, hautement extensible et multi-version pour serveurs Minecraft généraux. Il intègre un gestionnaire de plugins de type Bukkit et des API faciles à utiliser, permettant de personnaliser la gestion des événements. ✨
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
     <a href="https://github.com/NeonAngelThreads/DolphinBot/blob/master/PluginDocs.md">📖Docs</a>
     ·
     <a href="https://github.com/NeonAngelThreads/DolphinBot/issues">🐛Suggérer/bug</a>
</p> 


## Pourquoi DolphinBot ?
   - **Multi-version** : ViaVersion intégré permet de prendre en charge les serveurs de 1.7.x à 1.21.11 et 26.1.
   - **Haute fiabilité** : faible utilisation réseau, reconnexion automatique, exécution longue durée.
   - **Haute programmabilité** : **machines à états** finies inspirées de **SpringBoot**, pour configurer **plusieurs processus de connexion** selon les serveurs.
   - **Haute extensibilité** : DolphinAPI mature avec `listeners de paquets`, `systèmes d’événements` et `gestionnaires d’événements` basés sur `mc protocol lib`;
     API de plugins de type Bukkit pour développer rapidement des plugins personnalisés.
   - **Journalisation avancée** : sérialiseur `TextComponent` pour couleurs/styles riches des messages serveur et informations de débogage.
   - **Haute performance** : plusieurs instances de bots dans un seul client avec faible CPU/mémoire.
   - **Facile à utiliser** : profils de bots dans le fichier de configuration, sans ligne de commande.
### **Raccourcis** : [Guide de développement de plugins personnalisés](PluginDocs.md)
## Fonctionnalités :
   - Enregistrement facile de commandes personnalisées commençant par `!` via CommandBuilder DolphinAPI.
   - **Injection à chaud** de plugins pendant la connexion au serveur.
   - Configuration de clusters de bots et démarrage simultané.

[//]: # (   - Prise en charge des expressions de logs console colorés `colorizeText&#40;"&6Hello &lWorld"&#41;`.)
   - Réponses automatiques sur `2b2t.xin` pour accélérer la connexion.
   - Configuration de proxy pour plusieurs bots.
   - **Web Console API** - serveur HTTP API intégré pour gestion/contrôle distant des bots.
   - **Flux de logs temps réel** - logs via WebSocket avec couleurs ANSI.
   - **Complétion Tab** - autocomplétion dans le terminal web comme sur bureau.
   - **Gestion multi-terminaux** - terminal indépendant par bot avec onglets.

## Captures :
### Exécution sous Windows Server 2019 :
<p align="center">
   <img src="assets/dev2.png">
</p>

## Introduction :
   **Catégories de fonctionnalités :**
-  [`Multi-version`](#cross-version-support)
- [`Plugins à rechargement à chaud`](#hot-swapping-plugins-in-game)
- [`Interactions terminal`](#interactions-in-terminal)
- [`Débogueur de paquets`](#config-file-setting-)
- [`Configuration proxy`](#config-file-setting-)
- [`Tableau de bord Web DolphinBot`](#web-console)

**API d’événements implémentées :**
- [`Machine à états de connexion programmable`](PluginDocs.md#3-programmable-login-state-machine)
- [`Système de commandes`](PluginDocs.md#42-commands-system)
- [`Gestionnaires de paquets`](PluginDocs.md#4-deep-understand-dolphinapis)
- [`Système de gestionnaires d’événements`](PluginDocs.md#3-register-event-handlers)
   - [`Événements joueur`](PluginDocs.md#player-events)
- [`Chat Unicode forcé`](PluginDocs.md#unicode-string-helper)

## Prise en charge multi-version
**DolphinBot peut désormais changer dynamiquement entre plusieurs versions de Minecraft.**   
DolphinBot détecte automatiquement le protocole du serveur et choisit la meilleure version. Liste :

| Versions prises en charge | Plage d’ID protocole | État |
|--------------------|-------------------|--------------|
| 26.1.x             | 775-777           | ✓            |
| 1.21.x             | 767-774           | ✓ Base       |
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

Vous pouvez déboguer ViaVersion dans DolphinBot pour inspecter la traduction des paquets.  
Pour activer le mode debug ViaVersion, ajoutez l’option JVM `-Dvia.debug=true`.
```bash 
java -Dvia.debug=true -jar "DolphinBot-[version].jar"
```
## Interactions terminal
- Envoyer des messages en jeu ou exécuter des commandes depuis le terminal DolphinBot.
- Commandes intégrées :
-
  |         Commande terminal         | Description                                       |
  |:---------------------------------:|---------------------------------------------------|
  | `reload <plugin Name> [bot name]` | Recharge à chaud un plugin spécifié                  |
  |  `load <pLugin Name> [bot name]`  | Charge à chaud un plugin spécifié                    |
  |        `reconnect` (`rc`)         | Se reconnecte au serveur en jeu.                  |
  |     `license`    (`l`, `lic`)     | Affiche la licence dans le terminal                  |
  |        `help`   (`h`, `?`)        | Affiche le menu et l’usage des commandes |
  |         `respawn` (`rs`)          | Fait réapparaître le bot.                                  |
   
- Utilisez `TAB` pour l’autocomplétion.
## Démarrage
Cette section explique :
  - **1. Démarrer directement un seul bot en ligne de commande.**  
  - **2. Spécifier le profil du bot via fichier de configuration.**  
  - **3. Démarrer plusieurs bots simultanément avec proxy.**  
  - **4. Configurer les options avancées.**  
  - **5. Créer un plugin personnalisé.**  
  - **6. Utiliser Web Console pour la gestion distante.**  

1. **Télécharger le client**  
   Téléchargez `DolphinBot-[version]-full.jar`.  

> [!IMPORTANT]
> Prérequis : **Java >= 17**

2. **Configuration du bot**  
### Configurer un profil 
   Deux méthodes :
   - Démarrage simple rapide : **ligne de commande**  
   - Plusieurs bots et options avancées : **fichier de configuration**    

1. **Configuration par ligne de commande**  
     Le profil doit être défini dans la commande de démarrage.  
     Exemple :
     ```bash
     java -jar "DolphinBot-[version]-full.jar" --username=[username] --password=[password] --skin-recorder=[enable/disable]
     ```
   | Argument  | Description                                                                |
   |--------------------|----------------------------------------------------------------------------|
   | `--username`       | Nom affiché du bot en jeu.                                            |
   | `--password`       | Mot de passe de connexion ou d’inscription.                                            |
   | `--auto-reconnect` | Reconnexion après kick/déconnexion. |
   | `--skin-recorder`  | Capturer et sauvegarder automatiquement les skins des joueurs.                  |
   | `--server`         | Adresse du serveur cible.                                                     |
   | `--port`           | Port du serveur cible.                                                        |

   Exemple :
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
> La ligne de commande est prioritaire sur le fichier de configuration ; les doublons du fichier sont ignorés.  
   
> [!TIP]
> Options supplémentaires :  
   `--owner` : spécifie qui peut utiliser ce bot.  
   
  **Exemple :**   
   `--owner=Melibertan`, plusieurs noms séparés par `;`.  
   **Exemple :**  
   `--owner=owner1;owner2;owner3;...`  

   ### Configuration par fichier    
   Fichiers : configuration globale `bot.config.global.json` et profils `bot.profiles.json`.  
      Déplacez les arguments de profil dans `bot.profiles.json` selon le format ci-dessous ; toutes les valeurs seront chargées.
      DolphinBot applique d’abord la ligne de commande ; les doublons du fichier sont ignorés.    
      Le chemin est optionnel via `--config-file` pour un répertoire ou fichier.  
      Exemple :  
   ```bash 
   java -jar "DolphinBot-[version].jar" --config-file=path/to/config.json
   ```
   Si le chemin est un répertoire, Dolphin y extrait la configuration par défaut.  
   ```bash
   java -jar "DolphinBot-[version].jar" --config-file=path/to/config_directory
   ```
   Sans `--config-file`, DolphinBot crée un fichier par défaut dans le dossier du jar.  
   ```bash 
   java -jar "DolphinBot-[version].jar"
   ```
   **Bots multiples et proxy**  
      Dans le fichier de profils, créez le champ `profiles` dans `bot.profiles.json` pour plusieurs bots.  
               
> [!NOTE]
> Certains serveurs interdisent plusieurs bots depuis la même IP ; le proxy permet d’utiliser différents réseaux/IP de sortie.  
     
Pour configurer le proxy par bot, modifiez `proxy` :
        
> [!WARNING]
> Plusieurs bots peuvent déclencher anti-bot/anti-triche ; certains serveurs stricts l’interdisent.
    
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
          
   1. `enabled_plugins` indique les plugins activés sur le bot.   
   2. `proxy` (optionnel) définit le proxy du bot ; `enabled` active ce réglage,   
              `info` contient :
      
      | Champ       | Description                                     |
      |-------------|-------------------------------------------------|
      | `address`   | IP distante ou nom d’hôte du proxy. |
      | `port`      | Port du proxy.                              |
      | `type`      | Mode proxy. (`HTTP`, `SOCKS4`, `SOCKS5`)        |

> [!TIP]
> "username" et "password" sont optionnels ; ajoutez-les si le proxy exige une authentification.

   Pour charger `bot#1` seul :  
   ```bash
   java -jar "DolphinBot-[version].jar" --config-file=path/to/config_directory --profiles="bot#1"
   ```
   ```bash
   java -jar "DolphinBot-[version].jar" --profiles="bot#1"
   ```   
   Pour plusieurs bots, séparez les noms dans `--profiles` par `;`.

   **Exemples :**
   ```bash
   java -jar "DolphinBot-[version].jar" --profiles="bot#1;bot#2;bot#3;..."
   ```

> [!NOTE]
> Sans `--profiles`, tous les bots du fichier de profils sont chargés.  

**Propriétaires :**  
   Limitez l’usage à certains joueurs via `owner`.  
   **Exemple :**
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
### Configuration avancée (optionnel)
   Pour plus d’options, modifiez `bot.config.global.json`.  
   Chaque option équivaut à la ligne de commande ; toutes les valeurs, même inconnues, sont analysées.  
   Exemple :
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
   ### Options de configuration :
   
   | Configuration                 | Description                                                                |
   |------------------------|----------------------------------------------------------------------------|
   | `server`               | Adresse du serveur.                                               |
   | `port`                 | Port du serveur.                                                  |
   | `auto-reconnecting`    | Reconnexion après kick/déconnexion. |
   | `enable-skin-recorder` | Active l’enregistreur de skins.                                              |
   | `packet-filter-delay`  | Délai max de réception entre paquets cibles (ms).                   |
   | `max-chunk-view`       | Échelle max de réception des paquets de chunk.                                       |
   | `connect-timing-out`   | Millisecondes avant timeout de connexion.           |
   | `reconnect-delay`      | Délai minimal de refroidissement avant reconnexion (ms).                |
   | `msg-send-delay`       | Délai d’envoi des messages en jeu.                                     |
   | `enable-packet-debug`  | Active le débogueur de paquets.                                            |
   | `packet-warning`       | Affiche les erreurs de paquets.                                              |
   | `language`             | Paramètre de langue UI. (Actuellement `zh`, `en`)          |


## Web Console
DolphinBot inclut une Web Console intégrée pour la gestion/contrôle distant. Gérez vos bots via une interface web moderne sans accès terminal direct.

### Démarrer Web Console
Ajoutez `--api` au démarrage :

```bash
java -jar "DolphinBot-[version]-full.jar" --api [port]
```

Le serveur API démarre sur le port indiqué (défaut : 25560), le WebSocket logs sur port+1 (défaut : 25561).

### Fonctionnalités
- **Tableau de bord** - état temps réel, mode de jeu et position des bots
- **Terminaux indépendants** - un terminal par bot avec onglets
- **Flux de logs temps réel** - logs ANSI dans le terminal web
- **Complétion Tab** - identique au terminal bureau
- **UI/UX moderne** - dialogues modaux, toasts, responsive
- **Statistiques** - progression circulaire du taux en ligne avec couleurs

### Accès
Une fois l’API lancée :
```
http://localhost:8080
```

La Web Console est une application Spring Boot séparée dans `web-console/`. Voir [Documentation Web Console](web-console/README.md).

### Endpoints API
| Endpoint | Méthode | Description              |
|-----------|----------|--------------------------|
| `/api/health` | GET | Vérification santé             |
| `/api/bots` | GET | Lister les bots            |
| `/api/bots/start` | POST | Démarrer un bot              |
| `/api/bots/stop` | POST | Arrêter un bot              |
| `/api/bots/send-command` | POST | Envoyer commande au bot    |
| `/api/config` | GET/POST | Lire/mettre à jour configuration |
| `/api/bot/create`      |   POST       | créer un bot           |             
| `/api/bot/delete`      |     POST         | supprimer un bot     |      

### Endpoints WebSocket
| Endpoint | Description |
|-----------|-------------|
| `ws://localhost:25561` | Flux de logs et complétion Tab |

## FAQ

- Puis-je créer un plugin pour DolphinBot ?   
    Oui, système de plugins facile avec API type Bukkit ; [guide complet.](PluginDocs.md)


- La configuration est-elle difficile ?  
    Non, la configuration initiale suffit dans la plupart des cas.


- Comment signaler un problème ou demander une fonction ?  
    Ouvrez une issue GitHub. Des étapes détaillées ou reproductions sont appréciées.


- Puis-je rejoindre le développement ?  
    Bien sûr ! Vous pouvez devenir le **deuxième contributeur** de DolphinBot.

### Notre premier contributeur :
1. huangdihd - (Correction d’un bug critique dans le commit `#372990a`)
## Communauté
  - Un bug ? Issues et suggestions bienvenues !  
  Mon espace Bilibili :
  https://m.bilibili.com/space/386644641  
  - Si vous aimez DolphinBot, une **star** aide beaucoup !

## Licence
GPL-3.0 ou ultérieure, voir [LICENSE complète](LICENSE).

### **By NeonAngelThreads, coding with ❤️**