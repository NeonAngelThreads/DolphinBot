package org.angellock.impl;


import com.google.gson.JsonElement;
import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.commands.CommandSerializer;
import org.angellock.impl.commands.CommandSpec;
import org.angellock.impl.events.IConnectListener;
import org.angellock.impl.events.IDisconnectListener;
import org.angellock.impl.events.handlers.SystemChatHandler;
import org.angellock.impl.events.packets.AddEntityPacket;
import org.angellock.impl.events.packets.PlayerChatPacketHandler;
import org.angellock.impl.events.packets.PlayerPositionPacket;
import org.angellock.impl.ingame.IPlayer;
import org.angellock.impl.ingame.Player;
import org.angellock.impl.ingame.PlayerTracker;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.providers.Plugin;
import org.angellock.impl.providers.PluginManager;
import org.angellock.impl.providers.SessionProvider;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.PlainTextSerializer;
import org.angellock.impl.util.math.Position;
import org.geysermc.mcprotocollib.network.BuiltinFlags;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.tcp.TcpClientSession;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;
import org.geysermc.mcprotocollib.protocol.data.game.entity.type.EntityType;
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
    protected final long ReconnectionDelay;
    protected final int TIME_OUT;
    protected MinecraftProtocol minecraftProtocol;
    protected ConfigManager config;
    protected long connectDuration = 0;
    protected boolean isByPassedVerification = true;
    protected GameMode serverGamemode = GameMode.ADVENTURE;
    private ChatMessageManager messageManager;
    private BotManager botManager;
    protected Position loginPos = new Position(0d,0d,0d);
    protected String server;
    protected int port;
    protected String name;
    private String profileName;
    protected List<String> owners = new ArrayList<>();
    protected String password;
    protected final CommandSpec commands = new CommandSpec(this);

    public AbstractRobot(ConfigManager configManager, PluginManager pluginManager){
        this.config = configManager;
        String playerName = this.config.getConfigValue("username");
        String serverAddress = this.config.getConfigValue("server");
        int serverPort = Integer.parseInt(this.config.getConfigValue("port"));
        this.password = this.config.getConfigValue("password");

        this.pluginManager = pluginManager;

        this.server = serverAddress;
        this.name = playerName;
        this.port = serverPort;
        this.TIME_OUT = Integer.parseInt(this.config.getConfigValue("connect-timing-out"));
        this.ReconnectionDelay = Integer.parseInt(this.config.getConfigValue("reconnect-delay"));

    }

    public AbstractRobot withName(String userName){
        this.name = userName;
        return this;
    }

    public AbstractRobot withOwners(String... owners) {
        this.owners = List.of(owners);
        return this;
    }

    public AbstractRobot withOwners(List<JsonElement> owners) {
        List<String> stringOwners = new ArrayList<>();
        for (JsonElement obj : owners) {
            stringOwners.add(obj.getAsString());
        }
        this.owners = stringOwners;
        return this;
    }

    public AbstractRobot withPassword(String password){
        this.password = password;
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
        this.minecraftProtocol = new MinecraftProtocol(this.name);
        return this;
    }

    public BotManager getBotManager() {
        return botManager;
    }

    public String getPassword(){
        return this.password;
    }

    public ChatMessageManager getMessageManager() {
        return messageManager;
    }

    public void connect(){
        onPreLogin();
        this.serverSession = new TcpClientSession(this.server, this.port, minecraftProtocol);

        this.messageManager = new ChatMessageManager(this.serverSession);

        this.serverSession.addListener((IConnectListener) event -> onJoin());

        this.serverSession.addListener((IDisconnectListener) event -> {
            onQuit(event.getReason().toString());
        });

        this.serverSession.addListener(new SystemChatHandler().addExtraAction((chatPacket -> {
            PlainTextSerializer componentSerializer = new PlainTextSerializer();
            String commandMsg = componentSerializer.serialize(chatPacket.getContent());
            CommandSerializer serializer = new CommandSerializer();
            CommandResponse meta = serializer.serialize(commandMsg);

            this.commands.executeCommand(meta);
        })));
        this.serverSession.addListener(new PlayerChatPacketHandler().addExtraAction((chat) -> {
            PlainTextSerializer nameSerializer = new PlainTextSerializer();

            String sender = nameSerializer.serialize(chat.getName());
            String commandMsg = chat.getContent();
            CommandSerializer serializer = new CommandSerializer();
            CommandResponse meta = serializer.serialize(commandMsg, sender);

            this.commands.executeCommand(meta);
        }));

        this.serverSession.addListener(new AddEntityPacket().addExtraAction((entityPacket -> {
            if (entityPacket.getType() == EntityType.PLAYER) {
                Player player = PlayerTracker.getPlayerByUUID(entityPacket.getUuid());
                if (player != null) {
                    log.info(ConsoleTokens.colorizeText("[PlayerTracker]: &3A player was detected: &d{}"), player.getProfile().getName());
                    player.setPosition(entityPacket.getX(), entityPacket.getY(), entityPacket.getZ());
                }
            }
        })));
        this.serverSession.addListener(new PlayerPositionPacket().addExtraAction((packet->{
            log.info(ConsoleTokens.colorizeText("&b&lSuccessfully logged-in to server world."));
            log.info(ConsoleTokens.colorizeText("&7Logged-in At Position &b{}"), packet.getPosition());
            this.loginPos.from(packet.getPosition());
        })));

        this.serverSession.setFlag(BuiltinFlags.READ_TIMEOUT, -1);
        this.serverSession.setFlag(BuiltinFlags.WRITE_TIMEOUT, -1);
        this.serverSession.connect(true, false);

//        Channel channel = this.serverSession.getChannel();
//        final UserConnection user = new UserConnectionImpl(channel, true);
//        new ProtocolPipelineImpl(user);
//        channel.pipeline().addBefore("encoder", CommonTransformer.HANDLER_ENCODER_NAME, new MCPEncodeHandler(user)).addBefore("decoder", CommonTransformer.HANDLER_DECODER_NAME, new MCPDecodeHandler(user));

        this.connectDuration = System.currentTimeMillis();
        try {
            boolean connect = true;
            boolean shouldWait = false;

            while (true) {
                try {
                    Thread.sleep(100L);
                    if (!this.serverSession.isConnected()){
                        this.connectDuration = System.currentTimeMillis();
                        break;
                    } else if (connect) {
                        this.pluginManager.loadAllPlugins(this);
                        connect = false;
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
                    log.info(ConsoleTokens.colorizeText("&6Unregistered packet error has been triggered!"));
                }
            }
        } finally {
            scheduleReconnect();
        }
    }

    public abstract boolean canSendMessages();

    public void scheduleReconnect() {
        try {
            Thread.sleep(this.ReconnectionDelay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.scheduleConnect(5);
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
        return (this.profileName != null) ? this.profileName: this.name;
    }

    public AbstractRobot withProfileName(String name) {
        this.profileName = name;
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
