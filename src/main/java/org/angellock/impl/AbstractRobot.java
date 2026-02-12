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

package org.angellock.impl;


import com.google.gson.JsonElement;
import lombok.Getter;
import org.angellock.impl.commands.CommandSpec;
import org.angellock.impl.events.IConnectListener;
import org.angellock.impl.events.IDisconnectListener;
import org.angellock.impl.events.handlers.ChatCommandHandler;
import org.angellock.impl.events.handlers.KeepAliveHandler;
import org.angellock.impl.events.handlers.PlayerEmergeHandler;
import org.angellock.impl.events.handlers.ServerChatCommandHandler;
import org.angellock.impl.events.packets.EntityMovePacket;
import org.angellock.impl.events.packets.PlayerPositionPacket;
import org.angellock.impl.events.packets.debugger.PacketDebugger;
import org.angellock.impl.ingame.Player;
import org.angellock.impl.ingame.PlayerTracker;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.managers.ProfileObject;
import org.angellock.impl.managers.TerminalCommandManager;
import org.angellock.impl.plugin.Plugin;
import org.angellock.impl.plugin.PluginManager;
import org.angellock.impl.plugin.SessionProvider;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.PlainTextSerializer;
import org.angellock.impl.util.math.Position;
import org.geysermc.mcprotocollib.network.BuiltinFlags;
import org.geysermc.mcprotocollib.network.ProxyInfo;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.tcp.TcpClientSession;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRobot implements ISendable, SessionProvider, IOptionalProcedures {
    protected TcpClientSession serverSession;
    protected static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&aDolphinBot"));
    private final ScheduledExecutorService reconnectScheduler = Executors.newScheduledThreadPool(1);
    protected final PluginManager pluginManager;
    protected MinecraftProtocol minecraftProtocol;
    protected ConfigManager globalConfig;
    protected long connectDuration;
    protected boolean isByPassedVerification = true;
    protected GameMode serverGamemode = GameMode.ADVENTURE;
    private ChatMessageManager messageManager;
    private BotManager botManager;
    protected Position loginPos = new Position();
    protected ProxyInfo proxyInfo;
    protected @Getter ProfileObject infoHelper = new ProfileObject();

    protected final TerminalCommandManager commandManager = new TerminalCommandManager();
    protected final CommandSpec commands = new CommandSpec(this);

    public AbstractRobot(ConfigManager configManager, PluginManager pluginManager){
        this.globalConfig = configManager;
        this.infoHelper.setName(this.globalConfig.getConfigValue("username"));
        this.infoHelper.setPassword(this.globalConfig.getConfigValue("password"));

        this.pluginManager = pluginManager;

    }

    public AbstractRobot enableProxy(ProxyInfo proxyInfo){
        this.proxyInfo = proxyInfo;
        return this;
    }

    public AbstractRobot withName(String userName){
        this.infoHelper.setName(userName);
        return this;
    }

    public AbstractRobot withOwners(String... owners) {
        this.infoHelper.setOwners(List.of(owners));
        return this;
    }

    public AbstractRobot withOwners(List<JsonElement> owners) {
        List<String> stringOwners = new ArrayList<>();
        for (JsonElement obj : owners) {
            stringOwners.add(obj.getAsString());
        }
        this.infoHelper.setOwners(stringOwners);
        return this;
    }

    public AbstractRobot withPassword(String password){
        this.infoHelper.setPassword(password);
        return this;
    }
    public AbstractRobot withDefaultPlugins(List<Plugin> plugins){
        this.pluginManager.getDefaultPlugins().addAll(plugins);
        return this;
    }
    public AbstractRobot withBotManager(BotManager botManager){
        this.botManager = botManager;
        return this;
    }
    public AbstractRobot buildProtocol(){
        this.minecraftProtocol = new MinecraftProtocol(this.infoHelper.getName());
        return this;
    }

    public TerminalCommandManager getCommandManager() {
        return commandManager;
    }

    public DolphinConfig config() {
        return this.globalConfig.config();
    }

    public String getPassword(){
        return this.infoHelper.getPassword();
    }

    public ChatMessageManager getMessageManager() {
        return messageManager;
    }

    public void connect(){
        onPreLogin();
        if (this.proxyInfo != null) {
            this.serverSession = new TcpClientSession(this.config().getServer(), this.config().getPort(), minecraftProtocol, this.proxyInfo);
        } else {
            this.serverSession = new TcpClientSession(this.config().getServer(), this.config().getPort(), minecraftProtocol);
        }

        this.messageManager = new ChatMessageManager(this);

        this.serverSession.addListener((IConnectListener) event -> onJoin());

        this.serverSession.addListener((IDisconnectListener) event -> {
            PlainTextSerializer serializer = new PlainTextSerializer();
            String text = serializer.serialize(event.getReason());
            if (text.isBlank()) {
                text = (event.getReason().toString());
            }
            onQuit(text);
        });

        this.serverSession.addListener(new ServerChatCommandHandler(this.commands));
        this.serverSession.addListener(new ChatCommandHandler(this.commands));
        this.serverSession.addListener(new EntityMovePacket());
        this.serverSession.addListener(new PlayerEmergeHandler());
        this.serverSession.addListener(new PlayerPositionPacket(this));
        this.serverSession.addListener(new KeepAliveHandler());
        if (this.config().getDebugSettings().isEnablePacketDebug()) {
            this.serverSession.addListener(new PacketDebugger());
        }

        this.serverSession.setFlag(BuiltinFlags.READ_TIMEOUT, -1);
        this.serverSession.setFlag(BuiltinFlags.WRITE_TIMEOUT, -1);
        this.serverSession.connect(true, false);

        this.connectDuration = System.currentTimeMillis();
        try {
            boolean connect = true;
            boolean shouldWait = false;

            while (true) {
                try {
                    Thread.sleep(20L);
                    if (!this.serverSession.isConnected()){
                        this.connectDuration = System.currentTimeMillis();
                        break;
                    } else if (connect) {
                        if (System.currentTimeMillis() - this.connectDuration > 100L){
                            this.pluginManager.loadAllPlugins(this);
                            connect = false;
                        }
                    } else if (!shouldWait) {
                        if (this.messageManager.pollMessage()) {
                            shouldWait = true;
                        }
                    } else if (canSendMessages()) {
                        shouldWait = false;
                    }
                }
                catch (InterruptedException e){
                    this.serverSession.disconnect("Interrupted");
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException ignore) {
                    log.warn(ConsoleTokens.colorizeText("&6Unregistered packet error has been triggered!"));
                }
            }
        } finally {
            scheduleReconnect();
        }
    }

    public abstract boolean canSendMessages();

    public void scheduleReconnect() {
        try {
            Thread.sleep(this.config().getReconnectDelay());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.scheduleConnect();
    }

    public void scheduleConnect() {
        this.reconnectScheduler.schedule(this::connect, 0, TimeUnit.SECONDS);
    }

    public void setBypassed(boolean bypassed) {
        this.isByPassedVerification = bypassed;
    }

    @Override
    public void sendPacket(MinecraftPacket packet) {
        this.serverSession.send(packet);
    }

    @Override
    public Session getSession(){
        return this.serverSession;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public long getConnectTime() {
        return connectDuration;
    }

    public boolean isByPassedVerification() {
        return isByPassedVerification;
    }

    public String getProfileName() {
        return (this.infoHelper.getProfileName() != null) ? this.infoHelper.getProfileName(): this.infoHelper.getName();
    }

    public AbstractRobot withProfileName(String name) {
        this.infoHelper.setProfileName(name);
        return this;
    }

    public GameMode getServerGamemode() {
        return serverGamemode;
    }

    public void setServerGamemode(GameMode serverGamemode) {
        this.serverGamemode = serverGamemode;
    }

    public Map<UUID, Player> getOnlinePlayers() {
        return PlayerTracker.getOnlinePlayers();
    }

    public CommandSpec getRegisteredCommands() {
        return this.commands;
    }
}
