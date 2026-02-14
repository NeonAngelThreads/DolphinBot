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

package org.angellock.impl.commands.dolphin.completers;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.commands.ICommandCompleter;
import org.angellock.impl.managers.BotManager;

import java.util.ArrayList;
import java.util.List;

public class LoadPluginCompleter implements ICommandCompleter {

    @Override
    public List<String> complete(String[] cmdList) {

        AbstractRobot bot = BotManager
                .bots()
                .values()
                .iterator()
                .next();
        String[] plugins = bot.getPluginManager().listPlugins();

        List<String> list = new ArrayList<>();
        if (cmdList[0].equalsIgnoreCase("load")) {
            for (String plugin : plugins) {
                if (plugin.contains(cmdList[1])) {
                    list.add(plugin);
                }
            }
        }
        else {
            String expectBot = cmdList[2].toLowerCase();
            bot = BotManager.bots().get(expectBot);
            if (bot != null){
                list.addAll(BotManager.bots().keySet());
            }
        }
        return list;
    }
}
