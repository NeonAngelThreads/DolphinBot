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
import org.angellock.impl.DolphinConfig;
import org.angellock.impl.util.ConsoleTokens;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&6ConfigManager"));
    private final Map<String, Object> commandLineOptions = new HashMap<>();
    @Getter
    private static DolphinConfig coreSettings;
    private final RobotConfig configHelper;
    public ConfigManager(OptionSet optionList, @Nullable String defaultPath){
        this.configHelper = new RobotConfig(defaultPath, ".json");

        for (OptionSpec<?> option: optionList.specs()){
            String stringOpt = option.options().get(0);
            Object valueObject = optionList.valueOf(option);
            this.commandLineOptions.put(stringOpt, valueObject);
        }
        coreSettings = this.configHelper.loadBotConfig(this.commandLineOptions);
    }

    public void printConfigSpec() {
        log.info(ConsoleTokens.colorizeText("&2Below command line options are enabled: &3{}"), commandLineOptions);
        log.info(ConsoleTokens.colorizeText("&bLoaded DolphinBot config file: &3{}"), this.config());
    }
    public ConfigManager(OptionSet optionList){
        this(optionList, null);
    }

    @Deprecated
    public Map<String, Object> getMCBotConfig(){
        if (this.commandLineOptions.isEmpty()) {
            loadConfig();
        }
        return this.commandLineOptions;
    }

    public DolphinConfig config() {
        return coreSettings;
    }

    public String getConfigValue(String key) {
        return (String) this.commandLineOptions.get(key);
    }

    private void loadConfig(){
        coreSettings = this.configHelper.loadBotConfig();
//        Map<String, JsonElement> defaultConfig = this.configHelper.readJSONContent();
////        if (defaultConfig == null){
////            return;
////        }
//        for (String item: this.commandLineOptions.keySet()){
//            if (this.commandLineOptions.get(item) == null){
//
//                this.commandLineOptions.put(item, defaultConfig.get(item).getAsString());
//            }
//        }
    }
    public void reloadConfig(){
        this.flushConfig();
        this.loadConfig();
    }

    private void flushConfig(){
        this.commandLineOptions.clear();
    }

}
