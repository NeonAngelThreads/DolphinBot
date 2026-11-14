### Language: 
[简体中文](README_CN.md) / **한국어** / [Español](README_ES.md) / [Français](README_FR.md) / [Русский](README_RU.md) / [Português](README_PT.md) / [English](README.md)
# DolphinBot
DolphinBot은 GeyserMC MCProtocolLib 1.21.11-SNAPSHOT(프로토콜 774) 기반의 헤드리스 Minecraft 봇 프레임워크입니다.
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
   ✨ 일반 Minecraft 서버를 위한 경량·고확장·크로스 버전 봇 프레임워크입니다. Bukkit 유사 플러그인 관리자와 사용하기 쉬운 API를 갖추고 있으며 이벤트 처리를 사용자 정의할 수 있습니다. ✨
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
     <a href="https://github.com/NeonAngelThreads/DolphinBot/blob/master/PluginDocs.md">📖문서</a>
     ·
     <a href="https://github.com/NeonAngelThreads/DolphinBot/issues">🐛제안/버그 제출</a>
</p> 


## DolphinBot을 선택하는 이유
   - **크로스 버전**: ViaVersion 통합으로 1.7.x부터 1.21.11 및 26.1까지 다중 버전 서버 지원.
   - **높은 신뢰성**: 낮은 네트워크 사용량, 연결 끊김 시 자동 재연결, 장기 실행 가능.
   - **높은 프로그래밍 가능성**: **SpringBoot** 유사 유한 **상태 머신**을 구현하여 서버별 **다중 로그인 프로세스** 구성 가능.
   - **높은 확장성**: 성숙한 DolphinAPI 내장. 다양한 `패킷 리스너`, `이벤트 시스템`, `mc protocol lib` 기반 `이벤트 핸들러` 제공.
     Bukkit 유사 플러그인 API를 통합하여 빠르게 사용자 플러그인 개발 가능.
   - **고급 로깅 시스템**: `TextComponent` 직렬화기로 서버 메시지의 풍부한 색상/스타일 파싱 및 유용한 디버그 정보 제공.
   - **고성능**: 단일 클라이언트에서 다중 봇 인스턴스를 낮은 CPU/메모리로 실행.
   - **사용 용이**: 봇 프로필을 구성 파일에 넣어 명령줄 없이 빠르게 시작.
### **바로가기**: [사용자 플러그인 개발 가이드](PluginDocs.md)
## 기능:
   - CommandBuilder DolphinAPI로 `!`로 시작하는 사용자 명령 쉽게 등록.
   - 서버 연결 중 플러그인 **핫 인젝션** 가능.
   - 봇 클러스터 구성 및 한 번에 시작 지원.
[//]: # (   - 색상 콘솔 로그 문자열 표현식 `colorizeText&#40;"&6Hello &lWorld"&#41;` 지원.)
   - `2b2t.xin`에서 로그인 속도를 높이기 위해 질문 자동 응답.
   - 여러 봇에 대한 프록시 설정 지원.
   - **Web Console API** - 원격 봇 관리/제어용 내장 HTTP API 서버.
   - **실시간 로그 스트리밍** - ANSI 색상 지원 WebSocket 기반 로그 스트리밍.
   - **Tab 완성** - 웹 터미널에서 데스크톱과 동일한 자동 완성.
   - **다중 터미널 관리** - 각 봇별 독립 터미널 인스턴스 및 탭 전환.

## 스크린샷:
### Windows Server 2019에서 실행:
<p align="center">
   <img src="assets/dev2.png">
</p>

## 소개:
   **기능 분류:**
-  [`크로스 버전`](#cross-version-support)
- [`핫 리로드 플러그인`](#hot-swapping-plugins-in-game)
- [`터미널 상호작용`](#interactions-in-terminal)
- [`패킷 디버거`](#config-file-setting-)
- [`프록시 설정`](#config-file-setting-)
- [`DolphinBot Web 대시보드`](#web-console)

**구현된 Event API:**
- [`프로그래밍 가능 로그인 상태 머신`](PluginDocs.md#3-programmable-login-state-machine)
- [`명령 시스템`](PluginDocs.md#42-commands-system)
- [`패킷 핸들러`](PluginDocs.md#4-deep-understand-dolphinapis)
- [`이벤트 핸들러 시스템`](PluginDocs.md#3-register-event-handlers)
   - [`플레이어 이벤트`](PluginDocs.md#player-events)
- [`강제 Unicode 채팅`](PluginDocs.md#unicode-string-helper)

## 크로스 버전 지원
**이제 DolphinBot은 여러 Minecraft 버전을 동적으로 전환할 수 있습니다.**   
DolphinBot은 서버 프로토콜 버전을 자동 감지하고 최적 버전을 선택합니다. 지원 버전 목록:

| 지원 버전 | 프로토콜 ID 범위 | 지원 상태 |
|--------------------|-------------------|--------------|
| 26.1.x             | 775-777           | ✓            |
| 1.21.x             | 767-774           | ✓ 기본       |
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

DolphinBot에서 ViaVersion을 디버그하여 패킷 변환 방식을 확인할 수 있습니다.  
ViaVersion 디버그 모드를 활성화하려면 Java에 JVM 옵션 `-Dvia.debug=true`를 추가하세요.
```bash 
java -Dvia.debug=true -jar "DolphinBot-[version].jar"
```
## 터미널 상호작용
- DolphinBot 터미널에서 게임 내 메시지를 보내거나 명령을 실행할 수 있습니다.
- 내장 명령:
-
  |         터미널 명령         | 설명                                       |
  |:---------------------------------:|---------------------------------------------------|
  | `reload <plugin Name> [bot name]` | 지정 플러그인 핫 리로드                  |
  |  `load <pLugin Name> [bot name]`  | 지정 플러그인 핫 로드                    |
  |        `reconnect` (`rc`)         | 게임에서 서버에 재연결.                  |
  |     `license`    (`l`, `lic`)     | 터미널에 라이선스 표시                  |
  |        `help`   (`h`, `?`)        | 명령 메뉴와 각 명령 사용법 표시 |
  |         `respawn` (`rs`)          | 봇 리스폰.                                  |
   
- 이 명령들은 `TAB`으로 자동 완성할 수 있습니다.
## 시작하기
이 섹션에서는 다음을 설명합니다:
  - **1. 명령줄로 단일 봇을 직접 시작하는 방법.**  
  - **2. 명령줄 없이 구성 파일로 봇 프로필을 지정하는 방법.**  
  - **3. 프록시 설정으로 여러 봇을 동시에 시작하는 방법.**  
  - **4. 고급 옵션을 구성하는 방법.**  
  - **5. 사용자 플러그인을 만드는 방법.**  
  - **6. Web Console로 원격 관리하는 방법.**  

1. **클라이언트 다운로드**  
   jar 아카이브 다운로드: `DolphinBot-[version]-full.jar`.  

> [!IMPORTANT]
> 요구 사항: **Java 버전 >= 17**

2. **봇 구성**  
### 프로필 구성 
   봇 구성을 설정하는 두 가지 방법:
   - 간단히 빠르게 시작하려면 **명령줄 설정** 사용  
   - 여러 봇을 한 번에 시작하고 고급 옵션을 사용하려면 **구성 파일 설정** 사용    

1. **명령줄 설정**  
     게임 내 프로필은 아래 시작 명령줄에서 정의해야 합니다.  
     인수 목록 예:
     ```bash
     java -jar "DolphinBot-[version]-full.jar" --username=[username] --password=[password] --skin-recorder=[enable/disable]
     ```
   | 명령 인수  | 설명                                                                |
   |--------------------|----------------------------------------------------------------------------|
   | `--username`       | 봇의 게임 내 표시 이름.                                            |
   | `--password`       | 로그인 또는 등록 비밀번호.                                            |
   | `--auto-reconnect` | 킥 또는 기타 이유로 연결 끊김 시 재연결 여부. |
   | `--skin-recorder`  | 온라인 플레이어 스킨 자동 캡처/저장 여부.                  |
   | `--server`         | 대상 서버 주소.                                                     |
   | `--port`           | 대상 서버 포트.                                                        |

   예:
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
> 명령줄이 구성 파일보다 우선합니다. 옵션이 중복되면 명령줄만 인식하고 구성 파일 항목은 무시합니다.  
   
> [!TIP]
> 선택적으로 인수를 추가해 더 많은 옵션 지정 가능:  
   `--owner` : 이 봇을 사용할 수 있는 사람 지정.  
   
  **예:**   
   `--owner=Melibertan`, 여러 이름도 정의 가능. 각 소유자 이름은 `;`로 구분.  
   **예:**  
   `--owner=owner1;owner2;owner3;...`  

   ### 구성 파일 설정    
   구성 파일에는 기능 구성 `bot.config.global.json`과 프로필 구성 `bot.profiles.json`이 포함됩니다.  
      위 프로필 인수를 아래 형식에 따라 `bot.profiles.json`으로 이동할 수도 있으며 모든 구성 값이 로드됩니다.
      DolphinBot은 명령줄 옵션을 먼저 적용하고 구성 파일의 중복 옵션은 무시합니다.    
      구성 파일 경로 지정은 선택 사항이며 `--config-file` 옵션으로 구성 디렉터리 또는 파일을 지정합니다.  
      예:  
   ```bash 
   java -jar "DolphinBot-[version].jar" --config-file=path/to/config.json
   ```
   지정 경로가 파일이 아닌 디렉터리이면 Dolphin은 해당 디렉터리에 기본 구성 파일을 추출합니다.  
   ```bash
   java -jar "DolphinBot-[version].jar" --config-file=path/to/config_directory
   ```
   `--config-file` 매개변수가 없으면 DolphinBot은 jar 디렉터리에 기본 파일을 생성합니다.  
   ```bash 
   java -jar "DolphinBot-[version].jar"
   ```
   **다중 봇 및 프록시 설정**  
      프로필 구성 파일에서 `bot.profiles.json`의 `profiles` 필드를 만들어 서버에 로그인할 여러 봇 프로필을 지정할 수 있습니다.  
               
> [!NOTE]
> 일부 서버는 동일 IP에서 여러 봇 시작을 금지할 수 있습니다. 프록시 설정은 다른 네트워크 환경 또는 서로 다른 출구 IP가 필요한 경우 여러 봇 실행을 돕습니다.  
     
각 봇의 프록시를 구성하려면 `proxy` 필드를 편집하세요. 예:
        
> [!WARNING]
> 여러 봇 정의는 안티봇 또는 안티치트를 유발할 수 있으며 엄격한 정책의 서버는 이를 금지할 수 있습니다.
    
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
          
   1. `enabled_plugins` 필드는 해당 봇에서 활성화할 플러그인을 나타냅니다.   
   2. `proxy` 필드(선택)는 각 봇의 프록시 구성이며 `enabled`는 이 프록시 설정 활성화 여부,   
              `info`는 다음을 포함합니다:
      
      | 필드       | 설명                                     |
      |-------------|-------------------------------------------------|
      | `address`   | 프록시 서버 원격 IP 주소 또는 호스트 이름. |
      | `port`      | 프록시 서버 포트.                              |
      | `type`      | 프록시 모드. (`HTTP`, `SOCKS4`, `SOCKS5`)        |

> [!TIP]
> "username", "password"는 선택 사항입니다. 원격 프록시 서버가 인증을 요구하면 추가해야 합니다.

   이 경우 `bot#1`을 단일 봇으로 로드하려면 아래 인수를 추가하세요:  
   ```bash
   java -jar "DolphinBot-[version].jar" --config-file=path/to/config_directory --profiles="bot#1"
   ```
   ```bash
   java -jar "DolphinBot-[version].jar" --profiles="bot#1"
   ```   
   여러 봇을 동시에 시작하려면 `--profiles` 옵션에 여러 프로필 이름을 목록으로 지정하고 각 이름을 `;`로 구분하세요.

   **예:**
   ```bash
   java -jar "DolphinBot-[version].jar" --profiles="bot#1;bot#2;bot#3;..."
   ```

> [!NOTE]
> `--profiles` 옵션이 없으면 기본적으로 프로필 구성의 모든 봇을 로드합니다.  

**소유자:**  
   봇을 지정 플레이어만 사용하도록 제한하려면 플레이어 이름을 `owner` 목록에 넣으세요.  
   **예:**
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
### 고급 구성(선택)
   더 많은 고급 구성을 사용하려면 `bot.config.global.json`을 편집하세요.  
   각 구성 옵션은 명령줄 옵션과 동일하며, 인식되지 않는 옵션을 포함한 모든 구성 값이 파싱되므로 사용자 정의 구성 옵션을 추가할 수 있습니다.  
   이 파일 구성 예:
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
   ### 구성 옵션:
   
   | 구성                 | 설명                                                                |
   |------------------------|----------------------------------------------------------------------------|
   | `server`               | 서버 주소 정의.                                               |
   | `port`                 | 서버 포트 정의.                                                  |
   | `auto-reconnecting`    | 킥 또는 기타 이유로 연결 끊김 시 재연결 여부. |
   | `enable-skin-recorder` | 스킨 기록기 활성화 여부.                                              |
   | `packet-filter-delay`  | 각 대상 패킷 사이 최대 수신 지연(밀리초).                   |
   | `max-chunk-view`       | 청크 패킷 수신 최대 범위.                                       |
   | `connect-timing-out`   | 연결 시간 초과 판정까지의 밀리초.           |
   | `reconnect-delay`      | 서버 재연결 시 최소 쿨다운 지연(밀리초).                |
   | `msg-send-delay`       | 게임 내 메시지 전송 지연.                                     |
   | `enable-packet-debug`  | 패킷 디버거 활성화 여부.                                            |
   | `packet-warning`       | 패킷 오류 표시 여부.                                              |
   | `language`             | UI 로케일 설정. (현재 `zh`, `en` 지원)          |


## Web Console
DolphinBot에는 원격 봇 관리/제어용 내장 Web Console이 포함되어 있습니다. 직접 터미널에 접근하지 않고 현대적인 웹 인터페이스로 봇을 관리할 수 있습니다.

### Web Console 시작
Web Console을 활성화하려면 DolphinBot 시작 시 `--api` 매개변수를 추가하세요:

```bash
java -jar "DolphinBot-[version]-full.jar" --api [port]
```

API 서버는 지정 포트(기본: 25560)에서 시작되고 WebSocket 로그 서버는 port+1(기본: 25561)에서 시작됩니다.

### Web Console 기능
- **봇 관리 대시보드** - 모든 봇 인스턴스의 실시간 상태, 게임 모드, 위치 확인
- **독립 터미널** - 각 봇에 독립 터미널 인스턴스 및 탭 기반 전환
- **실시간 로그 스트리밍** - 모든 로그를 ANSI 색상 지원과 함께 웹 터미널로 스트리밍
- **Tab 완성** - 데스크톱 터미널과 동일한 자동 완성
- **현대적 UI/UX** - 아름다운 모달 대화상자, 토스트 알림, 반응형 디자인
- **봇 통계** - 온라인율을 색상 코드 상태와 함께 원형 진행률로 시각화

### Web Console 접속
API 서버 실행 후 다음 주소로 Web Console에 접속:
```
http://localhost:8080
```

Web Console은 `web-console/` 디렉터리의 별도 Spring Boot 애플리케이션으로 제공됩니다. 자세한 설정/사용법은 [Web Console 문서](web-console/README.md)를 참고하세요.

### API 엔드포인트
내장 HTTP API는 다음 엔드포인트를 제공합니다:

| 엔드포인트 | 메서드 | 설명              |
|-----------|----------|--------------------------|
| `/api/health` | GET | 상태 확인             |
| `/api/bots` | GET | 모든 봇 목록            |
| `/api/bots/start` | POST | 봇 시작              |
| `/api/bots/stop` | POST | 봇 중지              |
| `/api/bots/send-command` | POST | 봇에 명령 전송    |
| `/api/config` | GET/POST | 구성 가져오기/업데이트 |
| `/api/bot/create`      |   POST       | 새 봇 생성           |             
| `/api/bot/delete`      |     POST         | 지정 봇 삭제     |      

### WebSocket 엔드포인트
| 엔드포인트 | 설명 |
|-----------|-------------|
| `ws://localhost:25561` | 로그 스트리밍 및 Tab 완성 |

## FAQ

- DolphinBot용 플러그인을 만들 수 있나요?   
    네, DolphinBot은 Bukkit 유사 API를 집약한 사용하기 쉬운 플러그인 시스템을 갖추고 있습니다. [전체 개발 가이드.](PluginDocs.md)


- 구성 파일 설정이 어렵나요?  
    아니요, 초기 구성은 대부분의 경우에 충분합니다.


- 이슈나 기능 요청은 어떻게 하나요?  
    GitHub Issues에 이슈를 열어 주세요. 자세한 단계나 재현 방법을 포함하면 좋습니다.


- DolphinBot 개발에 참여할 수 있나요?  
    물론입니다! DolphinBot 팀의 **두 번째 기여자**가 될 수 있습니다! 언제든 자유롭게 참여하세요.

### 첫 번째 기여자:
1. huangdihd - (커밋 `#372990a`에서 치명적 버그 수정)
## 커뮤니티
  - 버그를 발견했나요? 이슈와 제안을 환영합니다!  
  제 Bilibili 공간:
  https://m.bilibili.com/space/386644641  
  - DolphinBot이 마음에 든다면 **star**가 큰 도움이 됩니다!

## 라이선스
GPL-3.0 또는 이후 버전, [전체 LICENSE](LICENSE) 참조.

### **By NeonAngelThreads, coding with ❤️**