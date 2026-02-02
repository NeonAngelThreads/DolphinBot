package org.angellock.impl.managers;

import com.google.gson.JsonElement;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.Getter;
import org.angellock.impl.util.ConsoleTokens;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&6ConfigManager"));
    private final Map<String, Object> cache = new HashMap<>();
    private final ResourceHelper configHelper;
    private boolean debugMode = false;
    public ConfigManager(OptionSet optionList, @Nullable String defaultPath){
        for (OptionSpec<?> option: optionList.specs()){
            String stringOpt = option.options().get(0);
            Object valueObject = optionList.valueOf(option);
            this.cache.put(stringOpt, valueObject);
        }
        this.configHelper = new RobotConfig(defaultPath, ".json");
        this.loadConfig();

        String s =(String)this.cache.get("enable-packet-debug");
        this.debugMode = (optionList.has("debug") || Boolean.parseBoolean(s));
    }

    public void printConfigSpec() {
        log.info(ConsoleTokens.standardizeText(ConsoleTokens.GREEN + "Below argument options are enabled: " + ConsoleTokens.DARK_AQUA + cache));
    }
    public ConfigManager(OptionSet optionList){
        this(optionList, null);
    }
    public Map<String, Object> getMCBotConfig(){
        if (this.cache.isEmpty()){
            loadConfig();
        }
        return this.cache;
    }

    public String getConfigValue(String key) {
        return (String) this.cache.get(key);
    }

    private void loadConfig(){
        Map<String, JsonElement> defaultConfig = this.configHelper.readJSONContent();
        if (defaultConfig == null){
            return;
        }
        for (String item: defaultConfig.keySet()){
            if (this.cache.get(item) == null){

                this.cache.put(item, defaultConfig.get(item).getAsString());
            }
        }
    }
    public void reloadConfig(){
        this.flushConfig();
        this.loadConfig();
    }

    private void flushConfig(){
        this.cache.clear();
    }

    public boolean isDebugMode() {
        return debugMode;
    }
}
