/*
 * DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 * Copyright (C) 2025 NeonAngelThreads (https://github.com/NeonAngelThreads)
 *
 *    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 *    License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any
 *    later version.
 *
 *    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 *    implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 *    License for more details. You should have received a copy of the GNU General Public License along with this
 *    program. If not, see <https://www.gnu.org/licenses/>.
 *
 * https://space.bilibili.com/386644641
 */

package org.angellock.impl.api.state;

import it.unimi.dsi.fastutil.Pair;
import lombok.Getter;
import org.angellock.impl.EnumSystemEvents;
import org.angellock.impl.util.TranslatableUtil;
import org.angellock.impl.util.reason.IReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginStateMachine extends StateMachine<String> {
    @Getter
    private LoginState currentState;
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

    public LoginStateMachine goal(LoginState nextState, StateAction action) {
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
            StateAction action = this.currentState.getAction();
            if (action != null){
                action.execute();
            }
            TranslatableUtil.infoTranslatableOf(EnumSystemEvents.LOGIN_STATEMACHINE_TRANSIT, this.currentState);
            if (nextState != null) {
                this.currentState = nextState;
                StateAction currentAction = currentState.getAction();
                if (currentAction != null){
                    currentAction.execute();
                }
                return true;
            }
        }
        return false;
    }

}
