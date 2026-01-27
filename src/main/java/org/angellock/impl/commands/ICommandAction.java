package org.angellock.impl.commands;
@FunctionalInterface
public interface ICommandAction {
    void onCommand(CommandResponse responseEntity);
}
