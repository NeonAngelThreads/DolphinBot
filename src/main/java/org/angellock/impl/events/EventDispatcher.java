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

package org.angellock.impl.events;

import org.angellock.impl.events.annotations.EventHandler;
import org.angellock.impl.events.bukkit.ActiveListener;
import org.angellock.impl.events.bukkit.Event;
import org.angellock.impl.plugin.Plugin;
import org.angellock.impl.plugin.PluginManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDispatcher {
    private final Map<Plugin, List<ActiveListener>> pluginMap = new HashMap<>();

    public void registerEvents(IListener listener, Plugin plugin) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            EventHandler annotation = method.getAnnotation(EventHandler.class);
            if (annotation != null) {
                Class<?>[] params = method.getParameterTypes();
                if (!Event.class.isAssignableFrom(params[0])) {
                    throw new IllegalArgumentException("Parameter type in method marked by @EventHandler should be a subclass of Event.class. At method: " + method);
                }
                Class<?> eventParamType = params[0];

                HandlerMapper mapper;
                try {
                    mapper = (HandlerMapper) eventParamType.getMethod("getHandlers").invoke(null);
                } catch (Exception e) {
                    throw new IllegalStateException("Could not find public static method 'getHandlers()': " + eventParamType, e);
                }
                ActiveListener registeredListener = new ActiveListener(listener, method, eventParamType, annotation.priority());

                mapper.register(registeredListener);
                pluginMap.computeIfAbsent(plugin, k -> new ArrayList<>()).add(registeredListener);
            }
        }
    }

    public void callEvent(Event event) {
        HandlerMapper list = event.getMapper();
        for (ActiveListener registeredListener : list.getRegisteredListenersInOrder()) {
            try {
                registeredListener.call(event);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
