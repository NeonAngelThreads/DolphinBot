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

import lombok.Getter;
import lombok.Setter;
import org.angellock.impl.AbstractRobot;
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.commands.CommandSpec;
import org.angellock.impl.events.EventDispatcher;
import org.angellock.impl.managers.TerminalCommandManager;
import org.angellock.impl.managers.utils.Manager;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.wrapper.LoggerWrapper;
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
    @Getter
    private String simpleName;
    @Getter
    private Manifest pluginManifest;
    private boolean enabled = false;
    private final List<SessionListener> listeners = new ArrayList<>();
    private RobotPlayer targetBot;
    private final LoggerWrapper log = new LoggerWrapper();
    protected Thread schedulerThread;
    @Setter
    @Getter
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
        log.setLoggerName(this.simpleName);
    }

    public EventDispatcher getEvents(){
        return this.targetBot.getPluginManager().event().dispatcher();
    }

    public LoggerWrapper getLogger(){
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

    @Override
    public void onPreEnable(RobotPlayer targetBot){
        this.targetBot = targetBot;
        this.log.setBot(targetBot);
        if (this.listeners.isEmpty()) {
            try {
                onEnable(this.targetBot);
                onEnable((AbstractRobot) this.targetBot);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Deprecated
    public void onEnable(final AbstractRobot entityBot){};
    public abstract void onEnable(final RobotPlayer entityBot);

    public abstract String getPluginName();
    @Override
    public String getName(){
        if(!this.getPluginName().isEmpty() && this.getPluginName() != null){
            return this.getPluginName();
        }
        else {
            String pluginName = getPluginManifest().getPluginName();
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
    public void setPluginManifest(Manifest pluginManifest){
        this.pluginManifest = pluginManifest;
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
