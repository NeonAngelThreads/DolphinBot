package org.angellock.impl.events;

import org.angellock.impl.IEventCallable;
import org.angellock.impl.events.bukkit.Event;
import org.angellock.impl.events.dolphin.CommandNotFoundEvent;
import org.angellock.impl.util.ConsoleTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.HashMap;

public class SystemEventLogger implements IEventCallable<String> {
    private static final Logger log = LoggerFactory.getLogger("EventLogger");
    private static final HashMap<Class<? extends Event>, String> systemEventMessages = new HashMap<>();

    public SystemEventLogger() {
        systemEventMessages.put(CommandNotFoundEvent.class, ConsoleTokens.colorizeText("&6Command &c&n&l\"{}\" &r&6not found."));
    }

    public void callEvent(Event event, String marker, Object... objs) {
        String baseMsg = this.callEvent(event);
        if (baseMsg != null) {
            Marker mk = MarkerFactory.getMarker(marker);
            log
                    .atInfo()
                    .addMarker(mk)
                    .log(baseMsg, objs);
        }
    }

    @Override
    public String callEvent(Event event) {
        return systemEventMessages.get(event.getClass());
    }
}
