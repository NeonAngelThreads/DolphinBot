package org.angellock.impl.events;

import org.angellock.impl.events.annotations.EventHandler;
import org.angellock.impl.events.bukkit.ActiveListener;
import org.angellock.impl.events.bukkit.Event;
import org.angellock.impl.plugin.Plugin;

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
                ActiveListener registeredListener = new ActiveListener(method, eventParamType, annotation.priority());

                mapper.register(registeredListener);
                pluginMap.computeIfAbsent(plugin, k -> new ArrayList<>()).add(registeredListener);
            }
        }
    }
}
