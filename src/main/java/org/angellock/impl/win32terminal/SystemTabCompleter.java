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

import org.angellock.impl.commands.ICommandCompleter;
import org.angellock.impl.commands.TerminalCommand;
import org.angellock.impl.ingame.PlayerTracker;
import org.angellock.impl.managers.TerminalCommandManager;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.util.*;

public class SystemTabCompleter implements Completer {
    private static SystemTabCompleter completer;
    public static SystemTabCompleter getInstance() {
        if(completer == null){
            completer = new SystemTabCompleter();
        }
        return completer;
    }
    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        String line = parsedLine.line();
        String[] subCommands = line.replaceFirst("/","").strip().split(" ");
        HashMap<String, TerminalCommand> commands = TerminalCommandManager.getRegisteredCommand();
        if (subCommands.length > 0) {
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

        String inputCommand = line.replaceFirst("/", "");
        for (TerminalCommand cmd : commands.values()) {
            if (cmd.getName().contains(inputCommand)) {
                appendCandidate(list, cmd.getName(), cmd.getDescription());
            }
            for (String alias : cmd.getAliases()) {
                if (alias.contains(inputCommand)) {
                    appendCandidate(list, alias, cmd.getDescription());
                }
            }
        }
        if(list.isEmpty()){
            PlayerTracker.getPlayerUUIDMapping().forEach(
                    (name, uuid) -> appendCandidate(list, name, uuid.toString())
            );
        }
    }

    private static void appendCandidate(List<Candidate> list, String name, String desc) {

        Candidate candidate = new Candidate('/' + name, new AttributedStringBuilder()
                .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN))
                .append(name)
                .toAnsi(),
                name,
                desc,
                null,
                null,
                true);
        list.add(candidate);
    }
}
