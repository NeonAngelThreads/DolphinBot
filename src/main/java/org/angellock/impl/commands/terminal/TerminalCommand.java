package org.angellock.impl.commands.terminal;

import org.angellock.impl.commands.AbstractCommand;
import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.commands.ICommandAction;
import org.angellock.impl.commands.ICommandCompleter;

public class TerminalCommand extends AbstractCommand {
    protected ICommandCompleter completer;

    public ICommandCompleter getCompleter() {
        return completer;
    }

    public void setCompleter(ICommandCompleter completer) {
        this.completer = completer;
    }

    public TerminalCommand(String name, ICommandAction executor) {
        super(name, executor);
    }

    public TerminalCommand(String name, ICommandAction action, ICommandCompleter completer) {
        super(name, action);
        this.completer = completer;
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
