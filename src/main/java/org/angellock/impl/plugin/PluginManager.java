package org.angellock.impl.plugin;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.events.EventDispatcher;
import org.angellock.impl.events.bukkit.Event;
import org.angellock.impl.managers.EventManager;
import org.angellock.impl.managers.TerminalCommandManager;
import org.angellock.impl.managers.utils.Manager;
import org.angellock.impl.util.ConsoleTokens;
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
    private final Collection<Plugin> enabled_base_plugin = new ArrayList<>();
    private final File pluginFolder;
    private static final PluginLoader loader = new PluginLoader();

    private static final EventManager eventManager = new EventManager();

    public PluginManager(){
        this((File) null);
    }

    public PluginManager(@Nullable String pluginDir) {
        this(new File((pluginDir == null)? "" : pluginDir));
    }

    public PluginManager(@Nullable File pluginDir) {
        if (pluginDir == null || !pluginDir.exists() || !pluginDir.isDirectory()){
            log.warn(ConsoleTokens.colorizeText("&eThe plugin folder was invalid or not existed: &c" + pluginDir
                    + "&6Trying to locate the fallback directory: &d" + getBaseConfigRoot()));
            this.pluginFolder = new File(getBaseConfigRoot(), "plugins");

        }
        else {
            this.pluginFolder = pluginDir;
        }
    }

    public static PluginLoader loader(){
        return loader;
    }

    public static EventManager event(){
        return eventManager;
    }

    public File getPluginFolder() {
        return pluginFolder;
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

    public void loadAllPlugins(AbstractRobot botInstance){
        if(!this.registeredPlugins.isEmpty()){
            for (Plugin plugin: this.registeredPlugins.values()){
                enable(plugin, botInstance);
            }
            return;
        }
        for (Plugin aDefault : this.enabled_base_plugin) {
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
            log.error(ConsoleTokens.colorizeText("&4The plugin folder was invalid or not found by removed, plugins will not be loaded. &8At: " + subDir.getPath()));
            return;
        }
        for (File InnerPlugin: individualPlugins){
            this.loadPlugin(botInstance, InnerPlugin);
        }

    }
    public void disableAllPlugins(AbstractRobot botInstance){
        for (String plugin : this.registeredPlugins.keySet()){
            this.disable(botInstance, plugin);
        }
    }
    @Override
    public void disable(AbstractRobot botInstance, String pluginName){
        Plugin target = this.registeredPlugins.get(pluginName);
        List<SessionListener> pluginListeners = target.getListeners();

        for (SessionListener listener : pluginListeners) {
            if(botInstance.getSession().getListeners().contains(listener)){
                log.info(ConsoleTokens.colorizeText("&7[&bEventBus&7] &7Removing Handler Object &l{}"), listener.toString());
                botInstance.getSession().removeListener(listener);
            }
        }
        target.onDisable();
        target.setEnabled(false);
    }

    public void enable(Plugin plugin, AbstractRobot provider){
        plugin.onLoad();
        if (!plugin.isEnabled()){
            plugin.setEnabled(true);
            this.registerPlugin(plugin);
            plugin.onEnables(provider);

            List<SessionListener> listeners = plugin.getListeners();
            log.info(ConsoleTokens.colorizeText("&7[&bEventBus&7] &eRegistering Listener From Plugin &6{}"), plugin.getName());

            for (SessionListener listener : listeners) {
                if (!provider.getSession().getListeners().contains(listener)) {
                    log.info(ConsoleTokens.colorizeText("&7[&bEventBus&7] &eInjecting Event Handler Object &l&7{}"), listener.toString());
                    provider.getSession().addListener(listener);
                }

            }
            log.info(ConsoleTokens.colorizeText("&aSuccessfully registered plugin &2{}, &dversion: &5{}, &bdescription: &3{}"), plugin.getName(), plugin.getVersion(), plugin.getDescription());
        }
    }

    public void loadPlugin(AbstractRobot botInstance, File target) {
        Plugin plugin = loader.loadPluginClass(target);
        if (plugin != null) {
            log.info(ConsoleTokens.colorizeText("&2Registering plugin: &b" + plugin.getName()));
            enable(plugin, botInstance);
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

    public Collection<Plugin> getDefaultPlugins() {
        return enabled_base_plugin;
    }
}
