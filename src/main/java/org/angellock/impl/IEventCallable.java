package org.angellock.impl;

import org.angellock.impl.events.bukkit.Event;

public interface IEventCallable<T> {
    T callEvent(Event event);
}
