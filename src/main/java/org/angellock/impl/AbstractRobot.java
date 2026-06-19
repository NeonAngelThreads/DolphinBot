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

package org.angellock.impl;


import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;
import org.angellock.impl.commands.CommandSpec;
import org.angellock.impl.events.IConnectListener;
import org.angellock.impl.events.bukkit.AbstractEvent;
import org.angellock.impl.api.handlers.ChatCommandHandler;
import org.angellock.impl.api.handlers.DisconnectReasonHandler;
import org.angellock.impl.api.handlers.PlayerEmergeHandler;
import org.angellock.impl.api.handlers.ServerChatCommandHandler;
import org.angellock.impl.api.packets.EntityMovePacket;
import org.angellock.impl.api.packets.PlayerPositionPacket;
import org.angellock.impl.api.packets.debugger.PacketDebugger;
import org.angellock.impl.ingame.Player;
import org.angellock.impl.ingame.PlayerTracker;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.managers.ProfileObject;
import org.angellock.impl.managers.TerminalCommandManager;
import org.angellock.impl.plugin.AbstractPlugin;
import org.angellock.impl.plugin.PluginManager;
import org.angellock.impl.plugin.SessionProvider;
import org.angellock.impl.protocol.ProtocolDetector;
import org.angellock.impl.protocol.via.DolphinProxyServer;
import org.angellock.impl.protocol.via.DolphinProxySession;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TranslatableUtil;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.network.BuiltinFlags;
import org.geysermc.mcprotocollib.network.ClientSession;
import org.geysermc.mcprotocollib.network.ProxyInfo;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.factory.ClientNetworkSessionFactory;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRobot implements ISendable, SessionProvider, IOptionalProcedures {
    protected ClientSession serverSession;
    @Getter
    protected static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&aDolphinBot"));
    private final ScheduledExecutorService reconnectScheduler = Executors.newScheduledThreadPool(1);
    @Getter
    protected final PluginManager pluginManager;
    protected MinecraftProtocol minecraftProtocol;
    protected ConfigManager globalConfig;
    protected long connectDuration;
    protected boolean isByPassedVerification = true;
    @Setter
    protected @Getter GameMode serverGamemode = GameMode.ADVENTURE;
    @Getter
    private BotManager botManager = BotManager.getInstance();
    protected ProxyInfo proxyInfo;
    protected @Getter ProfileObject infoHelper = new ProfileObject();
    
    // Cross-version support
    private com.viaversion.viaversion.api.protocol.version.ProtocolVersion detectedServerProtocol;

    @Getter
    protected final TerminalCommandManager commandManager = new TerminalCommandManager();
    protected final CommandSpec commands = new CommandSpec(this);

    public AbstractRobot(ConfigManager configManager, PluginManager pluginManager){
        this.globalConfig = configManager.buildConfig();
        this.infoHelper.setName(this.globalConfig.getConfigValue("username"));
        this.infoHelper.setPassword(this.globalConfig.getConfigValue("password"));
        this.infoHelper.setServer(this.globalConfig.config().getServer());
        this.infoHelper.setPort(this.globalConfig.config().getPort());

        this.pluginManager = pluginManager;

    }

    public AbstractRobot enableProxy(ProxyInfo proxyInfo){
        this.proxyInfo = proxyInfo;
        return this;
    }

    public AbstractRobot withName(String userName){
        if (this.infoHelper.getName() == null) {
            this.infoHelper.setName(userName);
        }
        return this;
    }

    public AbstractRobot withOwners(String... owners) {
        if (this.infoHelper.getOwners().isEmpty()) {
            this.infoHelper.setOwners(List.of(owners));
        }
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
        if (this.infoHelper.getPassword() == null) {
            this.infoHelper.setPassword(password);
        }
        return this;
    }

    public AbstractRobot withDefaultPlugins(List<AbstractPlugin> plugins) {
        this.pluginManager.getDefaultPlugins().addAll(plugins);
        return this;
    }
    public AbstractRobot withBotManager(BotManager botManager){
        this.botManager = botManager;
        return this;
    }
    public AbstractRobot buildProtocol(){
        //this.infoHelper.getName()
        //this.minecraftProtocol = new MinecraftProtocol(new GameProfile(this.infoHelper.getName(), UUID.fromString(this.infoHelper.getName()).toString()), UUID.randomUUID().toString());
        this.minecraftProtocol = new MinecraftProtocol(this.infoHelper.getName());
        return this;
    }

    public DolphinConfig config() {
        return this.globalConfig.config();
    }

    public String getPassword(){
        return this.infoHelper.getPassword();
    }

    public void connect(){
        onPreLogin();
        String serverIP = this.infoHelper.getServer();
        int serverPort = this.infoHelper.getPort();

        // Detect server protocol version
        log.info("Detecting server protocol version for {}:{}", serverIP, serverPort);
        detectedServerProtocol = ProtocolDetector.detectProtocolVersion(serverIP, serverPort);

        // Client protocol version is fixed for the embedded mcprotocollib build.
        // mcprotocollib 1.21.11-SNAPSHOT uses protocol version 774 (Minecraft 1.21.11)
        com.viaversion.viaversion.api.protocol.version.ProtocolVersion clientProtocol =
                com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getProtocol(774); // mcprotocollib 1.21.11

        // Log version information
        log.info("Server protocol: {} (ID: {})",
                detectedServerProtocol.getName(), detectedServerProtocol.getVersion());
        log.info("Client protocol: {} (ID: {}) - using built-in protocol",
                clientProtocol.getName(), clientProtocol.getVersion());

        // ── CRITICAL FIX (GitHub Issue #686 Solution): Use server-version PacketCodec when translating ──
        // When client and server protocols differ, we MUST create a MinecraftProtocol with a
        // PacketCodec configured for the TARGET SERVER's version (not the client's version).
        //
        // Why? Because mcprotocollib generates packets using the codec's protocol version.
        // If we use the default codec (version 774), all packets will be in 1.21.11 format,
        // and the server won't understand them → "Outdated client" error.
        //
        // By using the server's version, mcprotocollib generates packets that the server
        // can understand directly. ViaVersion then translates these packets to the format
        // the client (mcprotocollib) expects.
        //
        // This implements the multi-version strategy from:
        // https://github.com/GeyserMC/MCProtocolLib/issues/686
        if (detectedServerProtocol.getVersion() != clientProtocol.getVersion()) {
            log.info("Client protocol ({}) != server protocol ({}). Starting ViaVersion translation proxy.",
                    clientProtocol.getName(), detectedServerProtocol.getName());

            try {
                // Initialize ViaVersion globally (once)
                DolphinProxyServer.initVia();

                // Get or start the proxy server
                DolphinProxyServer proxy = DolphinProxyServer.getInstance();
                if (!proxy.isRunning()) {
                    proxy.start(25567);  // Use different port to avoid conflicts
                }

//                // Create a session for this bot
                DolphinProxySession session = proxy.createSession(
                        this.infoHelper.getName(),
                        serverIP, serverPort,
                        clientProtocol,
                        detectedServerProtocol);

                // Override connection target to go through our local proxy instead.
                String proxyHost = "127.0.0.1";
                int proxyPort = 25567;

                log.info("Bot '{}' connecting via translation proxy at {}:{} → {}:{}",
                        this.infoHelper.getName(), proxyHost, proxyPort,
                        serverIP, serverPort);

                // Connect mcprotocollib through the proxy
                this.serverSession =
                        ClientNetworkSessionFactory.factory()
                        .setRemoteSocketAddress(new InetSocketAddress(proxyHost, proxyPort))
                        .setProtocol(this.minecraftProtocol)
                        .setProxy(this.proxyInfo)
                        .create();

            } catch (Exception e) {
                log.error("Failed to start translation proxy for bot '{}': {}",
                        this.infoHelper.getName(), e.getMessage(), e);
                this.connectDuration = System.currentTimeMillis();
                return;
            }
        } else {
            // Protocols match – direct connection (no proxy needed)
            this.serverSession = ClientNetworkSessionFactory.factory()
                    .setRemoteSocketAddress(new InetSocketAddress(serverIP, serverPort))
                    .setProtocol(this.minecraftProtocol)
                    .setProxy(this.proxyInfo)
                    .create();
        }

        this.serverSession.addListener((IConnectListener) event -> onJoin());
        this.serverSession.addListener(new DisconnectReasonHandler(this));
        this.serverSession.addListener(new ServerChatCommandHandler(this.commands, this));
        this.serverSession.addListener(new ChatCommandHandler(this.commands, this));
        this.serverSession.addListener(new EntityMovePacket());
        this.serverSession.addListener(new PlayerEmergeHandler(this));
        this.serverSession.addListener(new PlayerPositionPacket((RobotPlayer) this));
        if (this.config().getDebugSettings().isEnablePacketDebug()) { this.serverSession.addListener(new PacketDebugger()); }
        
        this.serverSession.setFlag(BuiltinFlags.WRITE_TIMEOUT, -1);
        log.info("Bot '{}' attempting to connect...", this.infoHelper.getName());
        this.serverSession.connect(true);

        log.info("Bot '{}' connected, starting main event loop...", this.infoHelper.getName());
        this.connectDuration = System.currentTimeMillis();

        this.mainTickingEventLoop();
    }

    public void callHandleableEvent(AbstractEvent event){
        this.getPluginManager().event().broadcastEvent(event);
    }

    /**
     * Get the detected server protocol version
     */
    public com.viaversion.viaversion.api.protocol.version.ProtocolVersion getDetectedProtocol() {
        return detectedServerProtocol;
    }

    public abstract void mainTickingEventLoop();

    public abstract boolean canSendMessages();

    public void scheduleReconnect() {
        TranslatableUtil.infoTranslatableOf(EnumSystemEvents.RECONNECT);
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

    public Marker getBotLabel(){
        return MarkerFactory.getMarker(this.getInfoHelper().getName());
    }

    public Map<UUID, Player> getOnlinePlayers() {
        return PlayerTracker.getOnlinePlayers();
    }

    public CommandSpec getRegisteredCommands() {
        return this.commands;
    }
}
