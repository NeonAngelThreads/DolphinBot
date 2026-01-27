package org.angellock.impl.commands;

public abstract class AbstractCommand {
    protected final String name;
    protected ICommandAction action;
    protected String description;
    protected String[] aliases;

    public AbstractCommand(String name, ICommandAction action) {
        this.name = name;
        this.action = action;
        this.aliases = new String[0];
    }

    public AbstractCommand(String name, ICommandAction action, String[] aliases) {
        this.name = name;
        this.action = action;
        this.aliases = aliases;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public abstract boolean activate(CommandResponse entity);
}
