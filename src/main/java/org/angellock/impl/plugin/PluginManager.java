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

package org.angellock.impl.plugin;

import lombok.Getter;
import org.angellock.impl.AbstractRobot;
import org.angellock.impl.EnumSystemEvents;
import org.angellock.impl.managers.EventManager;
import org.angellock.impl.managers.TerminalCommandManager;
import org.angellock.impl.managers.utils.Manager;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TranslatableUtil;
import org.geysermc.mcprotocollib.network.event.session.SessionListener;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

public class PluginManager extends Manager implements IPluginInjectable{
    private static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&9PluginManager"));
    private final FilenameFilter pluginFilePattern = (d,name)->name.endsWith(".jar");
    private final Map<String, AbstractPlugin> registeredPlugins = new HashMap<>();
    private final Map<String, File> loadedExternalPlugin = new HashMap<>();
    private final Collection<AbstractPlugin> enabled_base_plugin = new ArrayList<>();
    @Getter
    private final File pluginFolder;
    private static final PluginLoader loader = new PluginLoader();

    private static final EventManager eventManager = new EventManager();

    public PluginManager(){
        this((File) null);
    }

    public PluginManager(@Nullable String pluginDir) {
        this(pluginDir == null ? null : new File(pluginDir));
    }

    public PluginManager(@Nullable File pluginDir) {
        this.pluginFolder = Objects.requireNonNullElseGet(pluginDir, () -> new File(getBaseConfigRoot(), "plugins"));
    }

    public static PluginLoader loader(){
        return loader;
    }

    public static EventManager event(){
        return eventManager;
    }

    public String[] listPlugins(){
        return pluginFolder.list(this.pluginFilePattern);
    }

    private void registerPlugin(Plugin plugin){
        this.registeredPlugins.putIfAbsent(plugin.getName().toLowerCase(), (AbstractPlugin) plugin);
    }

    public void keepScheduleThreadsAlive(){
        for (AbstractPlugin plugin: this.registeredPlugins.values()){
            if (!plugin.schedulerThread.isAlive()){
                plugin.schedulerThread.start();
            }
        }
    }

    public void listRegisterInfo(AbstractRobot botInstance) {
        Set<String> pl = botInstance.getRegisteredCommands().getRegisteredCommands().keySet();
        log.info(TranslatableUtil.getFormattedMessage(EnumSystemEvents.PLUGIN_LOAD_COMMANDS, pl, pl.size()));

        Set<String> tCommand = TerminalCommandManager.registeredCommand.keySet();
        log.info(TranslatableUtil.getFormattedMessage(EnumSystemEvents.PLUGIN_LOAD_TERMINAL_COMMANDS, tCommand, tCommand.size()));
        List<SessionListener> listeners = botInstance.getSession().getListeners();
        log.info(TranslatableUtil.getFormattedMessage(EnumSystemEvents.PLUGIN_LISTENER_LOAD, listeners.size()));
    }

    public void loadAllPlugins(AbstractRobot botInstance){
        if(!this.registeredPlugins.isEmpty()){
            for (AbstractPlugin plugin : this.registeredPlugins.values()) {
                enable(plugin, botInstance);
            }
            listRegisterInfo(botInstance);
            return;
        }
        for (AbstractPlugin aDefault : this.enabled_base_plugin) {
            enable(aDefault, botInstance);
        }

        File[] plugins = this.pluginFolder.listFiles(this.pluginFilePattern);
        File subDir = new File(this.pluginFolder, botInstance.getProfileName());
        if(!this.pluginFolder.exists()){
            boolean successful = this.pluginFolder.mkdir();
            if (!successful){
                log.error(ConsoleTokens.colorizeText("&4Failed to create the plugin folder."));
            }else {
                log.info(ConsoleTokens.colorizeText("&7Successfully created new plugin folder."));
            }
        }
        if (!subDir.exists()){
            boolean successful2 = subDir.mkdir();
            if(successful2){
                log.info(ConsoleTokens.colorizeText("&7Created individual bot plugin folder."));
            }
        }
        if(plugins == null){
            log.error(ConsoleTokens.colorizeText("&6The plugin folder was invalid or not found by removed, plugins will not be loaded. &8At: " + pluginFolder.getPath()));
            return;
        }
        for (File plugin: plugins){
            this.loadPlugin(botInstance, plugin);
        }

        File[] individualPlugins = subDir.listFiles(this.pluginFilePattern);
        if(individualPlugins == null){
            log.error(ConsoleTokens.colorizeText("&4The plugin folder was invalid or not found, plugins will not be loaded. &8At: " + subDir.getPath()));
            return;
        }
        for (File InnerPlugin: individualPlugins){
            this.loadPlugin(botInstance, InnerPlugin);
        }
        listRegisterInfo(botInstance);
    }
    public void disableAllPlugins(AbstractRobot botInstance){
        for (String plugin : this.registeredPlugins.keySet()){
            log.info(TranslatableUtil.getFormattedMessage(EnumSystemEvents.PLUGIN_DISABLE, plugin));
            this.disable(botInstance, plugin);
        }
        botInstance.getSession().getListeners().clear();
    }
    @Override
    public void disable(AbstractRobot botInstance, String pluginName){
        Plugin target = this.registeredPlugins.get(pluginName);
        List<SessionListener> pluginListeners = target.getListeners();

        for (SessionListener listener : pluginListeners) {
            if(botInstance.getSession().getListeners().contains(listener)){
                log.info(TranslatableUtil.getFormattedMessage(EnumSystemEvents.PLUGIN_EVENT_HANDLER_DISABLE, listener.toString()));
                botInstance.getSession().removeListener(listener);
            }
        }
        target.onDisable();
        target.setEnabled(false);
    }

    public void enable(AbstractPlugin plugin, AbstractRobot provider) {
        plugin.onLoad();
        if (!plugin.isEnabled()){
            plugin.setEnabled(true);
            this.registerPlugin(plugin);
            plugin.onEnables(provider);

            List<SessionListener> listeners = plugin.getListeners();
            log.info(TranslatableUtil.getFormattedMessage(EnumSystemEvents.PLUGIN_LOAD, plugin.getName()));

            for (SessionListener listener : listeners) {
                if (!provider.getSession().getListeners().contains(listener)) {
                    log.info(TranslatableUtil.getFormattedMessage(EnumSystemEvents.PLUGIN_EVENT_HANDLER_LOAD, listener.toString()));
                    provider.getSession().addListener(listener);
                }
            }
            log.info(TranslatableUtil.getFormattedMessage(EnumSystemEvents.PLUGIN_LOAD_COMPLETE, plugin.getName(), plugin.getVersion(), plugin.getDescription()));
        }
    }

    public void loadPlugin(AbstractRobot botInstance, File target) {
        Plugin plugin = loader.loadPluginClass(target);
        if (plugin != null) {
            log.info(ConsoleTokens.colorizeText("&2Registering plugin: &b" + plugin.getName()));
            enable((AbstractPlugin) plugin, botInstance);
            this.loadedExternalPlugin.put(plugin.getName().toLowerCase(), target);
        }else {
            log.error(ConsoleTokens.colorizeText("Failed to register the plugin &4" + target));
        }
    }

    public void reloadPlugin(AbstractRobot botInstance, String pluginName){
        File pluginFile = this.loadedExternalPlugin.get(pluginName);
        disable(botInstance, pluginName);
        if (pluginFile.exists()){
            this.loadPlugin(botInstance, pluginFile);
        }
    }

    public Collection<AbstractPlugin> getDefaultPlugins() {
        return enabled_base_plugin;
    }
}
