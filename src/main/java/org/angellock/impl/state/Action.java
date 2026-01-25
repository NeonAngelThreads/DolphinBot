package org.angellock.impl.state;

import org.angellock.impl.AbstractRobot;

public abstract class Action{
    AbstractRobot botInstance;
    public Action(AbstractRobot botInstance) {
        this.botInstance = botInstance;
    }
    public abstract void execute();
}
