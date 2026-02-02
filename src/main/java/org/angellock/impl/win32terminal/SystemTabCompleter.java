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
            if (cmd.getName().startsWith(line)) {
                list.add(new Candidate("/"+cmd.getName()));
            }
        }
    }
}
