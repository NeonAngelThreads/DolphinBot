package org.angellock.impl;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.angellock.impl.dolphin.GUIWindowManager;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.win32terminal.AnsiEscapes;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import javax.swing.*;

public class Start {
    private static final Logger log = LoggerFactory.getLogger(Start.class);
    private static final String ARCHIVE_VERSION = AnsiEscapes.shiftVersionTags(Optional
            .ofNullable(Start.class
                    .getPackage()
                    .getImplementationVersion()
            ).orElse("LATEST"));
    private static final boolean win32 = System
            .getProperty("os.name")
            .toLowerCase()
            .contains("windows");
    private static GUIWindowManager guiManager;
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
        ArgumentAcceptingOptionSpec<String> profilesArg = optionParser.accepts("profiles").withOptionalArg().ofType(String.class);
        ArgumentAcceptingOptionSpec<String> pluginDir = optionParser.accepts("plugin-dir").withOptionalArg().ofType(String.class);
        ArgumentAcceptingOptionSpec<String> configFile = optionParser.accepts("config-file").withOptionalArg().ofType(String.class);
        NonOptionArgumentSpec<String> unrecognizedOptions = optionParser.nonOptions();
        OptionSet parsedOption = optionParser.parse(args);

        List<?> badOptions = parsedOption.valuesOf(unrecognizedOptions);
        if (!badOptions.isEmpty()){
            log.warn(ConsoleTokens.colorizeText("&6Omitted option arguments " + badOptions));
        }

        String defaultConfigPath = Optional
                .ofNullable(parsedOption.valueOf(configFile))
                .orElse("not-set");

        if (Files.exists(Paths.get(defaultConfigPath))) {
            log.info(ConsoleTokens.colorizeText("&dThe default config file path was specified: &5&l" + defaultConfigPath));
        } else {
            log.error(ConsoleTokens.colorizeText("&4The specified config file path is invalid: " + defaultConfigPath));
            defaultConfigPath = null;
        }
        @Nullable String profiles = (parsedOption.valueOf(profilesArg));

        ConfigManager config = new ConfigManager(parsedOption, defaultConfigPath);
        BotManager botManager = new BotManager(defaultConfigPath, ".json", config)
                .globalPluginManager(parsedOption.valueOf(pluginDir))
                .loadProfiles(profiles);

        if (parsedOption.has("gui")){
            guiManager = new GUIWindowManager(botManager);
            guiManager.startGUI();
        } else {
            AnsiEscapes.printArt(ARCHIVE_VERSION);
            config.printConfigSpec();
            log.info(ConsoleTokens.colorizeText("&8Loading bots..."));
            botManager.startAll();
        }

    }

    public static String getArchiveVersion() {
        return ARCHIVE_VERSION;
    }

    public static boolean isWindows() {
        return win32;
    }
}