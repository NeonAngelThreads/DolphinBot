/*
 *  DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 *  Copyright (C) 2025 NeonAngelThreads (https://github.com/NeonAngelThreads)
 *
 *     This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 *     License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any
 *     later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 *     implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 *     License for more details. You should have received a copy of the GNU General Public License along with this
 *     program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  https://space.bilibili.com/386644641
 */

package org.angellock.impl.events.bukkit;

import org.angellock.impl.events.EventPriority;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActiveListener {
    private final Object listenerInstance;
    private final Method action;
    private final Class<?> eventType;
    private EventPriority priority = EventPriority.NORMAL;

    public ActiveListener(Object listenerInstance, Method action, Class<?> eventType) {
        action.setAccessible(true);
        this.listenerInstance = listenerInstance;
        this.action = action;
        this.eventType = eventType;
    }

    public ActiveListener(Object listenerInstance, Method action, Class<?> eventType, EventPriority priority) {
        this(listenerInstance, action, eventType);
        this.priority = priority;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void call(AbstractEvent event) throws InvocationTargetException, IllegalAccessException {
        if (this.eventType.isInstance(event)) {
            this.action.invoke(this.listenerInstance, event);
        }
    }
}
