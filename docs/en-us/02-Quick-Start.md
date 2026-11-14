> [!NOTE] Previous · [01-Structure](./01-Plugin-Structure.md) · [PluginDocs](../PluginDocs.md) · [Next](./03-Lifecycle.md)

# 2. Quick Start
Before start, you need to add DolphinAPI dependency. There are following steps:

* Download DolphinBot-<NEWEST-VERSION>-RELEASE.jar jar archive.
* Add this archive as dependency to your project.  

Build a minimal plugin from scratch. Copy, tweak, package, drop — you'll see it log `Hello, DolphinBot!` on next bot start.

## Maven `pom.xml` template

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.me</groupId>
    <artifactId>my-plugin</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- System scope: depends on a pre-built DolphinBot shaded JAR -->
        <dependency>
            <groupId>io.github.neonangelthreads</groupId>
            <artifactId>dolphinbot</artifactId>
            <version>1.6.0-ALPHA</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/../target/DolphinBot-1.6.0-ALPHA-full-shaded.jar</systemPath>
        </dependency>
    </dependencies>

    <build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>false</filtering>
            </resource>
        </resources>
        <plugins>
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
            </plugin>
            <plugin>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.6.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals><goal>shade</goal></goals>
                        <configuration>
                            <createDependencyReducedPom>false</createDependencyReducedPom>
                            <artifactSet>
                                <excludes>
                                    <!-- Do NOT bundle anything the framework already has -->
                                    <exclude>io.github.neonangelthreads:dolphinbot</exclude>
                                </excludes>
                            </artifactSet>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

## Entry class

```java
package org.me.plugin;

import org.angellock.impl.RobotPlayer;
import org.angellock.impl.plugin.AbstractPlugin;

public class MyPlugin extends AbstractPlugin {
    @Override public String getPluginName()    { return "MyPlugin"; }
    @Override public String getVersion()       { return "1.0.0"; }
    @Override public String getDescription()   { return "Hello DolphinBot"; }

    @Override
    public void onLoad() {
        // Called once when the JAR is first scanned. No bot yet.
        getLogger().info("Loading " + getPluginName());
    }

    @Override
    public void onEnable(RobotPlayer bot) {
        // Called once per bot profile when it joins a server.
        getLogger().info("Hello, " + bot.getProfileName() + "!");
    }

    @Override
    public void onDisable() {
        // Called when the bot disconnects or shuts down.
        getLogger().info("Goodbye, world.");
    }
}
```

## `plugin.json`

Create this at `src/main/resources/plugin.json`:

```json
{
  "entry": "org.me.plugin.MyPlugin",
  "name": "MyPlugin",
  "version": "1.0.0"
}
```

## Build + deploy

```powershell
# Build (DolphinBot root has the wrapper)
$MVN = "C:\Users\<you>\.m2\wrapper\dists\apache-maven-3.9.16-bin\<hash>\apache-maven-3.9.16\bin\mvn.cmd"
& $MVN -f "path/to/MyPlugin/pom.xml" package -DskipTests

# Deploy
Copy-Item MyPlugin\target\my-plugin.jar DolphinBot\target\plugins\my-plugin.jar
```

## Quick check

Start the bot and look for:

```
[INFO] Loading MyPlugin - 1.0.0
[INFO] Hello, <ProfileName>!
```

You're done. Now go read [Lifecycle](./03-Lifecycle.md) or [Actions-Core Architecture](../docs/10-Actions-Core-Architecture.md) for the real meat.
