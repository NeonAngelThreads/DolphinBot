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
import io.netty.channel.Channel;
import lombok.Getter;
import net.kyori.adventure.text.TranslatableComponent;
import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.commands.CommandSerializer;
import org.angellock.impl.commands.CommandSpec;
import org.angellock.impl.events.IConnectListener;
import org.angellock.impl.events.IDisconnectListener;
import org.angellock.impl.events.handlers.*;
import org.angellock.impl.events.packets.AddEntityPacket;
import org.angellock.impl.events.packets.EntityMovePacket;
import org.angellock.impl.events.packets.PlayerPositionPacket;
import org.angellock.impl.events.packets.debugger.PacketDebugger;
import org.angellock.impl.events.types.EntityEmergedEvent;
import org.angellock.impl.events.types.EntityMovedEvent;
import org.angellock.impl.events.types.JoinedGameEvent;
import org.angellock.impl.ingame.IPlayer;
import org.angellock.impl.ingame.Player;
import org.angellock.impl.ingame.PlayerTracker;
import org.angellock.impl.managers.BotInfoHelper;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.managers.TerminalCommandManager;
import org.angellock.impl.plugin.Plugin;
import org.angellock.impl.plugin.PluginManager;
import org.angellock.impl.plugin.SessionProvider;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.PlainTextSerializer;
import org.angellock.impl.util.TextComponentSerializer;
import org.angellock.impl.util.math.Position;
import org.geysermc.mcprotocollib.network.BuiltinFlags;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.*;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.network.tcp.TcpClientSession;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;
import org.geysermc.mcprotocollib.protocol.data.game.entity.type.EntityType;
import org.geysermc.mcprotocollib.protocol.data.status.handler.ServerPingTimeHandler;
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundKeepAlivePacket;
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundPongPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundMoveEntityPosPacket;
import org.geysermc.mcprotocollib.protocol.packet.status.serverbound.ServerboundStatusRequestPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRobot implements ISendable, SessionProvider, IOptionalProcedures, IPlayer {
    protected TcpClientSession serverSession;
    protected static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&aDolphinBot"));
    private final ScheduledExecutorService reconnectScheduler = Executors.newScheduledThreadPool(1);
    protected final Random randomizer = new Random();
    protected final PluginManager pluginManager;
    protected MinecraftProtocol minecraftProtocol;
    protected ConfigManager config;
    protected long connectDuration;
    protected boolean isByPassedVerification = true;
    protected GameMode serverGamemode = GameMode.ADVENTURE;
    private ChatMessageManager messageManager;
    private BotManager botManager;
    protected Position loginPos = new Position();
    protected @Getter BotInfoHelper infoHelper = new BotInfoHelper();

    protected final TerminalCommandManager commandManager = new TerminalCommandManager();
    protected final CommandSpec commands = new CommandSpec(this);

    public AbstractRobot(ConfigManager configManager, PluginManager pluginManager){
        this.config = configManager;
        String playerName = this.config.getConfigValue("username");
        String serverAddress = this.config.getConfigValue("server");
        int serverPort = Integer.parseInt(this.config.getConfigValue("port"));
        this.connectDuration = Long.parseLong(this.config.getConfigValue("reconnect-delay"));
        this.infoHelper.setPassword(this.config.getConfigValue("password"));

        this.pluginManager = pluginManager;

        this.infoHelper.setServer(serverAddress);
        this.infoHelper.setName(playerName);
        this.infoHelper.setPort(serverPort);
        this.infoHelper.setTIME_OUT(Integer.parseInt(this.config.getConfigValue("connect-timing-out")));
        this.infoHelper.setReconnectionDelay(Integer.parseInt(this.config.getConfigValue("reconnect-delay")));

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

    public ConfigManager config() {
        return this.config;
    }

    public String getPassword(){
        return this.infoHelper.getPassword();
    }

    public ChatMessageManager getMessageManager() {
        return messageManager;
    }

    public void connect(){
        onPreLogin();
        this.serverSession = new TcpClientSession(this.infoHelper.getServer(), this.infoHelper.getPort(), minecraftProtocol);

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
        if (this.config.isDebugMode()){
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
            Thread.sleep(this.infoHelper.getReconnectionDelay());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.scheduleConnect(0);
    }

    public void scheduleConnect(int wait) {
        this.reconnectScheduler.schedule(() -> this.connect(), wait, TimeUnit.SECONDS);
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

    public Random getRandomizer() {
        return randomizer;
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
