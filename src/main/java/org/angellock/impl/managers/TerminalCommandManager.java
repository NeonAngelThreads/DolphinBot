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

package org.angellock.impl.managers;

import lombok.Getter;
import org.angellock.impl.AbstractRobot;
import org.angellock.impl.commands.AbstractCommand;
import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.commands.ICommandCompleter;
import org.angellock.impl.commands.terminal.TerminalCommand;
import org.angellock.impl.util.ConsoleTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class TerminalCommandManager {
    private static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&9TerminalCommandSystem"));
    @Getter
    public static HashMap<String, TerminalCommand> registeredCommand = new HashMap<>();

    private static HashMap<String, TerminalCommand> aliasCommand = new HashMap<>();

    public void registerCommand(TerminalCommand command){
        for (String alias : command.getAliases()) {
            aliasCommand.put(alias, command);
        }
        registeredCommand.put(command.getName().toLowerCase(), command);
    }
    public void registerCommand(TerminalCommand command, ICommandCompleter completer){
        command.setCompleter(completer);
        this.registerCommand(command);
    }
    public TerminalCommand getCommand(String commandName){
        return registeredCommand.get(commandName.toLowerCase());
    }

    public static Logger log() {
        return log;
    }

    public boolean callCommand(String msg, AbstractRobot bot) {
        String[] commandList = msg
                .replaceFirst("/", "")
                .strip()
                .split(" ");

        if (commandList.length > 0){
            AbstractCommand cmd = registeredCommand.get(commandList[0].toLowerCase());
            CommandResponse commandResponse = new CommandResponse(commandList, "<Terminal>");
            if (cmd != null){
                cmd.activate(commandResponse, bot);
                return true;
            } else {
                AbstractCommand aliaCmd = aliasCommand.get(commandList[0]);
                if (aliaCmd != null) {
                    aliaCmd.activate(commandResponse, bot);
                    return true;
                }
            }
        }
        return false;
    }
}
