package org.angellock.impl.commands;

import java.util.ArrayList;
import java.util.List;

public class Command extends AbstractCommand {

    private final List<String> users = new ArrayList<>();

    public Command(String name, ICommandAction action, List<String> users) {
        super(name, action);
        this.users.addAll(users);
    }

    public List<String> getUsers(){
        return this.users;
    }

    public void setAction(ICommandAction action){
        this.action = action;
    }

    @Override
    public boolean activate(CommandResponse entity){
        if (users.contains(entity.getSender()) || users.isEmpty()) {
            this.action.onCommand(entity);
            return true;
        }
        return false;
    }
}
