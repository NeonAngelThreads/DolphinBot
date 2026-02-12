package org.angellock.impl.managers;

import org.angellock.impl.DolphinConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Map;


public class RobotConfig extends ResourceHelper{

    public RobotConfig(@Nullable String defaultPath, String fileType) {
        super(defaultPath, fileType);
    }

    public DolphinConfig loadBotConfig() {
        return this
                .Helper
                .fromJson(this.readDataFrom(this.configPath), DolphinConfig.class);
    }
    @Override
    public String getFileName() {
        return "mc.bot.config";
    }

    public DolphinConfig loadBotConfig(Map<String, Object> commandLineOptions) {
        return this
                .Helper
                .fromJson(this.readDataFrom(this.configPath), DolphinConfig.class)
                .mergeCommandOptions(commandLineOptions);
    }
}
