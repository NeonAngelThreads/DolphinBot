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
 *    program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * https://space.bilibili.com/386644641
 */

package org.angellock.impl.extensions;

import org.angellock.impl.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;

public enum Plugins {
    QUEUE_PLUGIN("QuestionAnswerer", QuestionAnswererPlugin.class),
    BASE_PLUGIN("MessageDisplay", BaseDefaultPlugin.class),
    VERIFY_PLUGIN("HumanVerify", PlayerVerificationPlugin.class);

    private final String pluginName;
    private final Class<?> pluginInstance;

    Plugins(String pluginName, Class<?> pluginType) {
        this.pluginName = pluginName;

        this.pluginInstance = pluginType;
    }

    public Plugin getPlugin(){
        try {
            return (Plugin) this.pluginInstance.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }

    public static Plugin getPluginFromString(String pluginName){
        for (Plugins plugins: Plugins.values()){
            if(plugins.pluginName.equalsIgnoreCase(pluginName)){
                return plugins.getPlugin();
            }
        }
        return null;
    }
}
