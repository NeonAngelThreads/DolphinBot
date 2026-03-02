/*
 *  DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 *  Copyright (C) 2025 NeonAngelThreads (https://github.com/NeonAngelThreads)
 *
 *     This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 *     License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any
 *     later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 *     implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 *     License for more details. You should have received a copy of the GNU General Public License along with this
 *     program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  https://space.bilibili.com/386644641
 */

package org.angellock.impl;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import lombok.Getter;
import org.angellock.impl.api.HttpAPIServer;
import org.angellock.impl.dolphin.GUIWindowManager;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TranslatableUtil;
import org.angellock.impl.win32terminal.AnsiEscapes;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Start {
    private static final Logger log = LoggerFactory.getLogger(Start.class);
    private static final String ARCHIVE_VERSION = AnsiEscapes.shiftVersionTags(Optional.ofNullable(Start.class.getPackage()
                    .getImplementationVersion()).orElse(ConsoleTokens.colorizeText("&dDEVELOPMENT")));
    private static Thread terminalInput;
    private static volatile boolean exit = false;
    private static final boolean win32 = System.getProperty("os.name").toLowerCase().contains("windows");
    private static GUIWindowManager guiManager;
    @Getter
    private static OptionSet GLOBAL_CONFIG;
    public static void main(String[] args) {
        OptionParser optionParser = new OptionParser();

        AnsiEscapes.enableAnsiSupport();
        optionParser.allowsUnrecognizedOptions();

        optionParser.accepts("owner").withRequiredArg().ofType(String.class);
        optionParser.accepts("username").withRequiredArg().ofType(String.class);
        optionParser.accepts("password").withRequiredArg().ofType(String.class);
        optionParser.accepts("server").withRequiredArg().ofType(String.class);
        optionParser.accepts("port").withRequiredArg().ofType(String.class);
        optionParser.accepts("skin-recorder").withRequiredArg().ofType(String.class);
        optionParser.accepts("gui");
        optionParser.accepts("api").withOptionalArg().ofType(Integer.class).defaultsTo(25560);
        ArgumentAcceptingOptionSpec<String> profilesArg = optionParser.accepts("profiles").withOptionalArg().ofType(String.class);
        ArgumentAcceptingOptionSpec<String> pluginDir = optionParser.accepts("plugin-dir").withOptionalArg().ofType(String.class);
        ArgumentAcceptingOptionSpec<String> configFile = optionParser.accepts("config-file").withOptionalArg().ofType(String.class);
        NonOptionArgumentSpec<String> unrecognizedOptions = optionParser.nonOptions();
        GLOBAL_CONFIG = optionParser.parse(args);


        List<?> badOptions = GLOBAL_CONFIG.valuesOf(unrecognizedOptions);
        if (!badOptions.isEmpty()){
            log.warn(ConsoleTokens.colorizeText("&6Omitted option arguments " + badOptions));
        }

        String defaultConfigPath = Optional
                .ofNullable(GLOBAL_CONFIG.valueOf(configFile))
                .orElse("not-set");

        if (Files.exists(Paths.get(defaultConfigPath))) {
            log.info(ConsoleTokens.colorizeText("&dThe default config file path was specified: &5&l" + defaultConfigPath));
        } else {
            log.error(ConsoleTokens.colorizeText("&4The specified config file path is invalid: " + defaultConfigPath));
            defaultConfigPath = null;
        }
        @Nullable String profiles = (GLOBAL_CONFIG.valueOf(profilesArg));

        ConfigManager config = new ConfigManager();
        ConfigManager.setDefaultPath(defaultConfigPath);
        ConfigManager.initGlobalSettings();
        BotManager botManager = new BotManager(defaultConfigPath, ".json", config)
                .globalPluginManager(GLOBAL_CONFIG.valueOf(pluginDir))
                .loadProfiles(profiles);
        BotManager.setInstance(botManager);
        Map<String, RobotPlayer> bots = BotManager.bots();
        getTerminal(bots.values().iterator().next());

        // Start HTTP API Server
        int apiPort = (int) GLOBAL_CONFIG.valueOf("api");
        try {
            new HttpAPIServer(apiPort);
        } catch (Exception e) {
            log.error("Failed to start HTTP API Server on port " + apiPort, e);
        }

        if (GLOBAL_CONFIG.has("gui")){
            guiManager = new GUIWindowManager(botManager);
            guiManager.startGUI();
        } else {
            AnsiEscapes.printArt(ARCHIVE_VERSION);
            config.printConfigSpec();
            log.info(TranslatableUtil.getFormattedMessage(EnumSystemEvents.DOLPHIN_BOTS_LOAD));
            botManager.startAll();
        }

    }

    private static void getTerminal(AbstractRobot dolphinBot) {
        LineReader reader = AnsiEscapes.getReader();
        terminalInput = new Thread(() -> {
            while (true) {
                try {
                    String s = reader.readLine(ConsoleTokens.colorizeText("&lTerminal>&b"));
                    ChatMessageManager messageManager = dolphinBot.getMessageManager();
                    if (messageManager != null) {
                        dolphinBot.getMessageManager().putMessage(s);
                    }
                } catch (UserInterruptException w) {
                    if (exit) {
                        System.exit(0);
                    } else {
                        log.warn("To exit DolphinBot, press Ctrl + C again.");
                        exit = true;
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    log.info(ConsoleTokens.colorizeText("&8Failed to send message: &7{}"), e.getMessage());
                }
            }
        });
        terminalInput.start();
    }

    public static String getArchiveVersion() {
        return ARCHIVE_VERSION;
    }

    public static boolean isWindows() {
        return win32;
    }
}