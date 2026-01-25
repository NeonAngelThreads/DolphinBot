package org.angellock.impl.state;

import it.unimi.dsi.fastutil.Pair;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.reason.IReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginStateMachine extends StateMachine<String> {

    private static final Logger log = LoggerFactory.getLogger(LoginStateMachine.class);
    public LoginState currentState;
    private final LoginState initialState;
    private String message;
    private IReason resetCondition;

    public LoginStateMachine(LoginState initialState) {
        this.currentState = initialState;
        this.initialState = initialState;
    }

    public void setState(LoginState state) {
        this.currentState = state;
    }

    @Override
    public void reset() {
        this.currentState = initialState;
    }

    @Override
    public LoginStateMachine resetOnlyWhen(IReason reason) {
        this.resetCondition = reason;
        return this;
    }

    public void raise(IReason reason) {
        if (reason == this.resetCondition) {
            this.reset();
        }
    }

    public LoginStateMachine source(LoginState state) {
        this.currentState = state;
        return this;
    }

    public LoginStateMachine whenReceive(String message) {
        this.message = message;
        return this;
    }

    public LoginStateMachine goal(LoginState nextState, Action action) {
        this.msgCache.add(this.message);
        this.transitionMap.put(Pair.of(this.currentState.name(), this.message), nextState.withAction(action));
        return this;
    }

    public LoginStateMachine and(){
        return this;
    }

    public void build(){
        this.reset();
    }

    @Override
    public boolean check(String input) {
        String key = this.lookForCache(input);
        if (!key.isEmpty()) {
            LoginState nextState = this.transitionMap.get(Pair.of(this.currentState.name(), key));
            if (nextState != null) {
                log.info(ConsoleTokens.colorizeText("&l登录状态: &b" + this.currentState));
                this.currentState = nextState;
                Action action = this.currentState.getAction();
                if (action != null) {
                    action.execute();
                    return true;
                }
            }
        }
        return false;
    }

}
