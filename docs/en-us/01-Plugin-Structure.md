> [!NOTE] Previous · [PluginDocs](../../PluginDocs.md) · [Next](./02-Quick-Start.md)

# 1. Plugin Structure

Every DolphinBot plugin is a **shaded JAR** (all dependencies bundled) with a manifest file at the jar root. The framework scans every JAR in `plugins/`, reads the manifest, and loads the entry class.

## Manifest file — `plugin.json`

```json
{
  "entry": "org.me.plugin.MyPlugin",
  "name": "MyPlugin",
  "version": "1.0.0"
}
```

| Field | Required | Notes |
|-------|:--------:|-------|
| `entry`  | ✅ | Fully-qualified class name of the entry class (extends `AbstractPlugin`) |
| `name`   | ✅ | Display name used in log output |
| `version` | ✅ | Free-form string |

> [!TIP] Entry-class fallback
> If `plugin.json` is missing, DolphinBot scans every class in the JAR for one that extends `AbstractPlugin`. Multiple matches → all are skipped with a warning.

## On-disk layout

```text
DolphinBot/
├── target/
│   ├── DolphinBot-1.6.0-ALPHA-full-shaded.jar
│   └── plugins/
│       ├── my-plugin.jar                ← applies to every bot profile
│       ├── Actions-Core-1.0.0.jar       ← another global plugin
│       └── <ProfileName>/               ← applies to one bot only
│           └── per-bot-plugin.jar
└── ...
```

> [!WARNING] File lock
> Plugins are loaded eagerly at bot startup. If a plugin JAR is modified while a bot is running, the file may remain locked by the JVM. Stop the bot before replacing plugin JARs.

## Shaded JAR requirement

DolphinBot loads plugins with a **shared classloader** that already contains MCProtocolLib and the framework. Your plugin must:

1. Bundle only its own code + any third-party deps **not already in the framework JAR**
2. **NOT** bundle MCProtocolLib, ViaProxy, Geyser, or any DolphinBot class — these are on the app classpath

> [!IMPORTANT]
> In practice this means you **must** use Maven Shade Plugin with `<scope>provided</scope>` for `DolphinBot-*-full-shaded.jar`. See [Quick Start § Maven POM template](./02-Quick-Start.md#maven-pomxml-template).

## Minimum viable JAR

```text
my-plugin.jar
├── plugin.json                    ← manifest at JAR root
└── org/
    └── me/
        └── plugin/
            └── MyPlugin.class     ← compiled entry class
```

No nested JARs, no fat-classpath hacks — just your classes + `plugin.json`.
