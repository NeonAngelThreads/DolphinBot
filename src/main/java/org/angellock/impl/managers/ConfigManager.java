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

package org.angellock.impl.managers;

import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.Getter;
import lombok.Setter;
import org.angellock.impl.DolphinConfig;
import org.angellock.impl.EnumSystemEvents;
import org.angellock.impl.Start;
import org.angellock.impl.events.AbstractEventProcessor;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TranslatableUtil;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&6ConfigManager"));
    private final Map<String, Object> commandLineOptions = new HashMap<>();
    private DolphinConfig coreSettings;
    @Getter
    private static DolphinConfig globalSettings;
    private RobotConfig configHelper;
    @Setter
    private static String defaultPath = null;
    private OptionSet optionSet = Start.getGLOBAL_CONFIG()  ;
    public ConfigManager buildConfig(){
        this.configHelper = new RobotConfig(defaultPath, ".json");
        for (OptionSpec<?> option: this.optionSet.specs()){
            String stringOpt = option.options().get(0);
            Object valueObject = this.optionSet.valueOf(option);
            this.commandLineOptions.put(stringOpt, valueObject);
        }
        this.coreSettings = this.configHelper.loadBotConfig(this.commandLineOptions);
        return this;
    }

    public static DolphinConfig global(){
        if (globalSettings == null){
            ConfigManager.initGlobalSettings();
        }
        return globalSettings;
    }

    public void printConfigSpec() {
        log.info(TranslatableUtil.getFormattedMessage(EnumSystemEvents.COMMANDLINE_LOADED, commandLineOptions));
        log.info(TranslatableUtil.getFormattedMessage(EnumSystemEvents.CONFIG_FILE_LOADED, this.config()));
    }
    public ConfigManager(OptionSet optionList){
        this.optionSet = optionList;
    }

    public static void initGlobalSettings() {
        ConfigManager.globalSettings = new RobotConfig(defaultPath, ".json").loadBotConfig();
    }

    public ConfigManager() {
    }

    @Deprecated
    public Map<String, Object> getMCBotConfig(){
        if (this.commandLineOptions.isEmpty()) {
            loadConfig();
        }
        return this.commandLineOptions;
    }

    public DolphinConfig config() {
        return coreSettings == null ? globalSettings : this.coreSettings;
    }

    public String getConfigValue(String key) {
        return (String) this.commandLineOptions.get(key);
    }

    private void loadConfig(){
        coreSettings = this.configHelper.loadBotConfig();
    }
    public void reloadConfig(){
        this.flushConfig();
        this.loadConfig();
    }

    private void flushConfig(){
        this.commandLineOptions.clear();
    }

}
