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

import org.angellock.impl.commands.*;
import org.angellock.impl.commands.executors.ReloadCommandExecutor;
import org.angellock.impl.commands.terminal.TerminalCommand;
import org.angellock.impl.events.SystemEventLogger;
import org.angellock.impl.events.dolphin.CommandNotFoundEvent;
import org.angellock.impl.util.ConsoleTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class TerminalCommandManager {
    private static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&9TerminalCommandSystem"));
    public static HashMap<String, TerminalCommand> registeredCommand = new HashMap<>();

    public void registerCommand(TerminalCommand command){
        registeredCommand.put(command.getName().toLowerCase(), command);
    }
    public void registerCommand(TerminalCommand command, ICommandCompleter completer){
        command.setCompleter(completer);
        this.registerCommand(command);
    }
    public TerminalCommand getCommand(String commandName){
        return registeredCommand.get(commandName.toLowerCase());
    }

    public static HashMap<String, TerminalCommand> getRegisteredCommand() {
        return registeredCommand;
    }

    public static Logger log() {
        return log;
    }

    public boolean callCommand(String msg){
        String[] commandList = msg
                .replaceFirst("/", "")
                .strip()
                .split(" ");

        if (commandList.length > 0){
            AbstractCommand cmd = registeredCommand.get(commandList[0].toLowerCase());
            if (cmd != null){
                CommandResponse commandResponse = new CommandResponse(commandList, "<Terminal>");
                cmd.activate(commandResponse);
                return true;
            }
        }
        return false;
    }
}
