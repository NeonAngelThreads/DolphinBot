package org.angellock.impl.plugin;

import org.angellock.impl.AbstractRobot;

public interface IPluginInjectable {
    void enable(Plugin plugin, AbstractRobot provider);
    void disable(AbstractRobot botInstance, String pluginName);
}
