package org.angellock.impl.commands;

public abstract class AbstractBuilder<T> {
    protected String commandName = "";
    protected String description;

    protected String[] aliases = new String[0];

    public AbstractBuilder<T> withDescription(String description){
        this.description = description;
        return this;
    }

    public AbstractBuilder<T> withAliases(String[] aliases){
        this.aliases = aliases;
        return this;
    }

    public abstract T build(ICommandAction action);
}
