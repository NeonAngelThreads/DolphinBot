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
