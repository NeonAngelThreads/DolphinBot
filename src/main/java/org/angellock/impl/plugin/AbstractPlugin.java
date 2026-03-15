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

package org.angellock.impl.plugin;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.commands.CommandSpec;
import org.angellock.impl.events.EventDispatcher;
import org.angellock.impl.managers.TerminalCommandManager;
import org.angellock.impl.managers.utils.Manager;
import org.angellock.impl.util.ConsoleTokens;
import org.geysermc.mcprotocollib.network.event.session.SessionListener;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPlugin extends Manager implements Plugin {
    private Path dataPath;
    private String simpleName;
    private Manifest manifest;
    private boolean enabled = false;
    private Manifest pluginManifest;
    private final List<SessionListener> listeners = new ArrayList<>();
    private AbstractRobot targetBot;
    private static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&6&lPlugins"));
    protected Thread schedulerThread;
    private ClassLoader classLoader;

    public AbstractPlugin(@Nullable String defaultDataPath){
        this();
        if (defaultDataPath != null) {
            Path path = Path.of(defaultDataPath);
            if (Files.exists(path)) {
                this.dataPath = path;
            }
        }
    }

    public AbstractPlugin(){
        String path = getBaseConfigRoot();
        this.dataPath = Path.of(path);
        this.simpleName = this.getClass().getSimpleName();
    }

    public EventDispatcher getEvents(){
        return this.targetBot.getPluginManager().event().dispatcher();
    }

    public String getSimpleName() {
        return simpleName;
    }

    public Manifest getManifest(){
        return this.pluginManifest;
    }

    public static Logger getLogger(){
        return log;
    }

    public CommandSpec getCommands(){
        return this.targetBot.getRegisteredCommands();
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public TerminalCommandManager getTerminalCommands(){
        return this.targetBot.getCommandManager();
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public void onEnables(AbstractRobot targetBot){
        this.targetBot = targetBot;
        if (this.listeners.isEmpty()) {
            try {
                onEnable(this.targetBot);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
    public abstract void onEnable(final AbstractRobot entityBot);

    public abstract String getPluginName();
    @Override
    public String getName(){
        if(!this.getPluginName().isEmpty() && this.getPluginName() != null){
            return this.getPluginName();
        }
        else {
            String pluginName = getManifest().getPluginName();
            if(!pluginName.isEmpty()){
                return pluginName;
            }
        }
        return getSimpleName();
    }
    @Override
    public @Nullable Path getDataFolder(){
        return this.dataPath;
    }

    @Override
    public boolean isEnabled(){
        return this.enabled;
    }

    @Override
    public List<SessionListener> getListeners(){
        return this.listeners;
    }
    @Override
    public void setManifest(Manifest manifest){
        this.manifest = manifest;
    }
    @Override
    public void setEnabled(boolean state){
        this.enabled = state;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else {
            return obj instanceof AbstractPlugin &&
                    this.getName().equals(((AbstractPlugin) obj).getName()) &&
                    this.simpleName.equals(((AbstractPlugin) obj).getSimpleName()) ;
        }
    }
}
