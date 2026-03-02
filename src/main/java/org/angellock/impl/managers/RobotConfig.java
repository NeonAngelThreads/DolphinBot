/*
 * DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 * Copyright (C) 2025 NeonAngelThreads (https://github.com/NeonAngelThreads)
 *
 *    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 *    License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any
 *    later version.
 *
 *    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 *    implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 *    License for more details. You should have received a copy of the GNU General Public License along with this
 *    program. If not, see <https://www.gnu.org/licenses/>.
 *
 * https://space.bilibili.com/386644641
 */

package org.angellock.impl.managers;

import lombok.Setter;
import org.angellock.impl.DolphinConfig;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;


@Setter
public class RobotConfig extends ResourceHelper{
    private String fileName;
    public RobotConfig(@Nullable String defaultPath, String fileType) {
        super(defaultPath, fileType);
    }

    public DolphinConfig loadBotConfig() {
        return this
                .Helper
                .fromJson(this.readDataFrom(this.configPath), DolphinConfig.class);
    }

    public DolphinConfig loadBotConfig(Map<String, Object> commandLineOptions) {
        return this
                .Helper
                .fromJson(this.readDataFrom(this.configPath), DolphinConfig.class)
                .mergeCommandOptions(commandLineOptions);
    }

    @Override
    public String getFileName() {
        return "bot.config.global";
    }
}
