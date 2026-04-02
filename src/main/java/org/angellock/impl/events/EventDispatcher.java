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

package org.angellock.impl.events;

import org.angellock.impl.events.annotations.EventHandler;
import org.angellock.impl.events.bukkit.AbstractEvent;
import org.angellock.impl.events.bukkit.ActiveListener;
import org.angellock.impl.plugin.Plugin;
import org.angellock.impl.util.ConsoleTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDispatcher {
    private final Map<Plugin, List<ActiveListener>> pluginMap = new HashMap<>();
    protected static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&3EventSystem"));

    public void registerListeners(IListener listener, Plugin plugin) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            EventHandler annotation = method.getAnnotation(EventHandler.class);
            if (annotation != null) {
                Class<?>[] params = method.getParameterTypes();
                if (!AbstractEvent.class.isAssignableFrom(params[0])) {
                    log.error("&4Parameter type in method marked by &e@EventHandler &4should be a subclass of Event.class. &cAt method: &b{}", method);
                    return;
                }
                Class<?> eventParamType = params[0];

                HandlerMapper mapper;
                try {
                    mapper = (HandlerMapper) eventParamType.getMethod("getHandlers").invoke(null);
                } catch (Throwable e) {
                    log.error(ConsoleTokens.colorizeText("&cCould not register event of &b{}&4, because: Could not find public static method 'getHandlers()': &6{}, &cException: {}"), eventParamType.getName(), eventParamType, e.toString());
                    return;
                }
                ActiveListener registeredListener = new ActiveListener(listener, method, eventParamType, annotation.priority());

                mapper.register(registeredListener);
                pluginMap.computeIfAbsent(plugin, k -> new ArrayList<>()).add(registeredListener);
            }
        }
    }

    public void callEvent(AbstractEvent event) throws Exception{
        HandlerMapper list = event.getMapper();
        for (ActiveListener registeredListener : list.getRegisteredListenersInOrder()) {
            try {
                registeredListener.call(event);
            } catch (InvocationTargetException | IllegalAccessException e) {
                log.warn(ConsoleTokens.colorizeText("&6Error: could not pass event {}, details: {}"), event, e.getMessage());
            }
        }
    }
}
