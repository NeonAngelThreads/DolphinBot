package org.angellock.impl.commands;

import java.util.ArrayList;
import java.util.List;

public class CommandBuilder extends AbstractBuilder<Command> {
    private List<String> users = new ArrayList<>();

    public CommandBuilder withName(String cmdName){
        this.commandName = cmdName;
        return this;
    }

    public CommandBuilder allowedUsers(List<String> users) {
        this.users = users;
        return this;
    }

    @Override
    public Command build(ICommandAction action) {
        Command command = new Command(this.commandName, action, this.users);
        command.setDescription(this.description);
        command.setAliases(this.aliases);
        return command;
    }
}
