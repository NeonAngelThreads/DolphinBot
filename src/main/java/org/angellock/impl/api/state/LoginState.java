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

public enum LoginState {
    
    LOGIN(0),
    REGISTER(1),
    VERIFY(2),
    IDLE(3),
    DISCONNECTED(4),
    JOIN(5);

    private final int stateValue;
    private StateAction action;

    private LoginState(int stateValue) {
        this.stateValue = stateValue;
    }

    public LoginState withAction(StateAction action) {
        this.action = action;
        return this;
    }

    public StateAction getAction() {
        return action;
    }

    public int getStateValue() {
        return stateValue;
    }
}