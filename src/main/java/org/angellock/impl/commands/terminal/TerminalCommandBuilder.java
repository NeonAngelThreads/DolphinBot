package org.angellock.impl.commands.terminal;

import org.angellock.impl.commands.AbstractBuilder;
import org.angellock.impl.commands.ICommandAction;

public class TerminalCommandBuilder extends AbstractBuilder<TerminalCommand> {
    @Override
    public TerminalCommand build(ICommandAction action) {
        TerminalCommand command = new TerminalCommand(this.commandName, action);
        command.setAliases(this.aliases);
        command.setDescription(this.description);

        return command;
    }
}
