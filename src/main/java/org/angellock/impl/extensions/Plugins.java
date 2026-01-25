package org.angellock.impl.extensions;

import java.lang.reflect.InvocationTargetException;

import org.angellock.impl.plugin.Plugin;

public enum Plugins {
    QUEUE_PLUGIN("QuestionAnswerer", QuestionAnswererPlugin.class),
    BASE_PLUGIN("MessageDisplay", BaseDefaultPlugin.class),
    VERIFY_PLUGIN("HumanVerify", PlayerVerificationPlugin.class);

    private final String pluginName;
    private final Class<?> pluginInstance;

    Plugins(String pluginName, Class<?> pluginType) {
        this.pluginName = pluginName;

        this.pluginInstance = pluginType;
    }

    public Plugin getPlugin(){
        try {
            return (Plugin) this.pluginInstance.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }

    public static Plugin getPluginFromString(String pluginName){
        for (Plugins plugins: Plugins.values()){
            if(plugins.pluginName.equalsIgnoreCase(pluginName)){
                return plugins.getPlugin();
            }
        }
        return null;
    }
}
