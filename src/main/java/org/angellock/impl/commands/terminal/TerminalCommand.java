package org.angellock.impl.commands.terminal;

import org.angellock.impl.commands.AbstractCommand;
import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.commands.ICommandAction;

public class TerminalCommand extends AbstractCommand {

    public TerminalCommand(String name, ICommandAction executor) {
        super(name, executor);
    }

    @Override
    public boolean activate(CommandResponse entity) {
        if (entity.isFromTerminal()){
            action.onCommand(entity);
            return true;
        }
        return false;
    }
}
