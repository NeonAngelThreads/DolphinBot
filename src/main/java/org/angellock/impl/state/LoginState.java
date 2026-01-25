package org.angellock.impl.state;

import java.util.HashMap;

public enum LoginState {
    
    LOGIN(0),
    REGISTER(1),
    VERIFY(2),
    IDLE(3),
    DISCONNECTED(4),
    JOIN(5);

    private final int stateValue;
    private Action action;

    private LoginState(int stateValue) {
        this.stateValue = stateValue;
    }

    public LoginState withAction(Action action) {
        this.action = action;
        return this;
    }

    public Action getAction() {
        return action;
    }

    public int getStateValue() {
        return stateValue;
    }
}