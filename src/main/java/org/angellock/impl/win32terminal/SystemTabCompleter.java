package org.angellock.impl.win32terminal;

import org.angellock.impl.commands.AbstractCommand;
import org.angellock.impl.commands.terminal.TerminalCommand;
import org.angellock.impl.managers.TerminalCommandManager;
import org.angellock.impl.plugin.PluginManager;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.List;

public class SystemTabCompleter implements Completer {
    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        String line = parsedLine.line();
        System.out.println(line);
        ArrayList<AbstractCommand> commands = TerminalCommandManager.getRegisteredCommand();
        for (AbstractCommand cmd : commands){
            if (cmd.getName().startsWith(line)){
                list.add(new Candidate(cmd.getName()));
            }
        }
    }
}
