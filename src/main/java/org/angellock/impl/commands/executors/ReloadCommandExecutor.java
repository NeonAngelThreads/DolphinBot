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

package org.angellock.impl.commands.executors;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.commands.ICommandAction;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReloadCommandExecutor implements ICommandAction {
    private static final Logger log = LoggerFactory.getLogger(ReloadCommandExecutor.class);

    @Override
    public void onCommand(CommandResponse responseEntity, RobotPlayer bot) {
        int botAmount = BotManager.bots().size();
        if (botAmount == 2) {
            String pluginName = responseEntity.getCommandList()[1];
            log.info(bot.getBotLabel(),"Reloading plugin {}", pluginName);

            PluginManager pm = bot.getPluginManager();
            pm.reloadPlugin(bot, pluginName);

        }
    }

}
