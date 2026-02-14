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

package org.angellock.impl.commands;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.util.ConsoleTokens;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandSpec {
    protected static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&9&lDolphinCommandExecutor"));
    private final Map<String, Command> registeredCommands = new HashMap<>();
    private final AbstractRobot bot;

    public CommandSpec(AbstractRobot bot) {
        this.bot = bot;
    }

    public void register(Command command){
        this.registeredCommands.put(command.getName().toLowerCase(), command);
    }

    public @Nullable Command getCommand(String commandName) {
        String standardizedCommand = commandName.toLowerCase();
        return this.registeredCommands.get(standardizedCommand);
    }

    public void executeCommand(CommandResponse response) {
        if (response != null) {
            log.info("CommandList: {}, sender: {}", Arrays.toString(response.getCommandList()), response.getSender());
            Command cmd = this.getCommand(response.getCommandList()[0]);
            if (cmd != null) {
                boolean success = cmd.activate(response, bot);
                if (!success) {
                    this.bot.getMessageManager().putMessage("[ERR]未能执行该命令。发送者未在owners白名单.请在命令行中配置。");
                }
            }
        }
    }
}
