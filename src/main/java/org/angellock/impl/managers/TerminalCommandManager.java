package org.angellock.impl.managers;

import org.angellock.impl.commands.AbstractCommand;
import org.angellock.impl.commands.Command;
import org.angellock.impl.commands.terminal.TerminalCommand;

import java.util.ArrayList;

public class TerminalCommandManager {
    public static ArrayList<AbstractCommand> registeredCommand = new ArrayList<>();

    public void registerCommand(TerminalCommand command){
        registeredCommand.add(command);
    }

    public static ArrayList<AbstractCommand> getRegisteredCommand(){
        return registeredCommand;
    }
}
