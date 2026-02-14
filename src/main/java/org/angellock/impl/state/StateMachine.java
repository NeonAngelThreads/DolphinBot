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
 *    program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * https://space.bilibili.com/386644641
 */

package org.angellock.impl.state;

import it.unimi.dsi.fastutil.Pair;
import org.angellock.impl.util.reason.IReason;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class StateMachine<T> {
    protected final HashMap<Pair<String, T>, LoginState> transitionMap = new HashMap<>(32);
    protected final ArrayList<String> msgCache = new ArrayList<>();

    public String lookForCache(String msg) {
        for (String s: this.msgCache){
            if (msg.contains(s)){
                return s;
            }
        }
        return "";
    }

    public abstract boolean check(T input);

    public abstract void reset();

    public abstract LoginStateMachine resetOnlyWhen(IReason reason);
}
