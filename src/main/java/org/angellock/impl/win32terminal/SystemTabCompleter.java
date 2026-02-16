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

package org.angellock.impl.win32terminal;

import org.angellock.impl.commands.AbstractCommand;
import org.angellock.impl.commands.ICommandCompleter;
import org.angellock.impl.commands.terminal.TerminalCommand;
import org.angellock.impl.managers.TerminalCommandManager;
import org.angellock.impl.plugin.PluginManager;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SystemTabCompleter implements Completer {
    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        String line = parsedLine.line();
        String[] subCommands = line.replaceFirst("/","").strip().split(" ");
        HashMap<String, TerminalCommand> commands = TerminalCommandManager.getRegisteredCommand();
        if (subCommands.length > 1) {
            TerminalCommand command = commands.get(subCommands[0]);
            if (command != null) {
                ICommandCompleter commandCompleter = command.getCompleter();
                if (commandCompleter == null) {
                    return;
                }
                List<String> sub = commandCompleter.complete(subCommands);
                for (String subCommand : sub) {
                    list.add(new Candidate(subCommand));
                }
            }
        }
        for (TerminalCommand cmd : commands.values()) {
            for (String alias : cmd.getAliases()) {
                if (alias.contains(line)) {
                    list.add(new Candidate( "/" + alias, new AttributedStringBuilder()
                            .style(AttributedStyle.BOLD.foreground(AttributedStyle.RED).background(AttributedStyle.BLUE)).append(alias).append("    ")
                            .toAnsi(),
                            cmd.getName(),
                            null,
                            null,
                            null,
                            true));
                }
            }
        }
    }
}
