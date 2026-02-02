package org.angellock.impl.events.dolphin;

import org.angellock.impl.events.HandlerMapper;
import org.angellock.impl.events.bukkit.Event;

public class CommandNotFoundEvent extends Event  {
    private final String currentCommand;

    public String getCurrentCommand() {
        return currentCommand;
    }

    public CommandNotFoundEvent(String currentCommand) {
        this.currentCommand = currentCommand;
    }

    @Override
    public HandlerMapper getMapper() {
        return null;
    }

}
