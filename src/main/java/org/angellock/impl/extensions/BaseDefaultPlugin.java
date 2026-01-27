package org.angellock.impl.extensions;

import net.kyori.adventure.text.TextComponent;
import org.angellock.impl.AbstractRobot;
import org.angellock.impl.commands.executors.ReloadCommandExecutor;
import org.angellock.impl.commands.terminal.TerminalCommand;
import org.angellock.impl.events.handlers.LoginHandler;
import org.angellock.impl.events.handlers.PlayerLogInfoHandler;
import org.angellock.impl.events.handlers.SystemChatHandler;
import org.angellock.impl.events.handlers.TitlePacketHandler;
import org.angellock.impl.ingame.Player;
import org.angellock.impl.ingame.PlayerTracker;
import org.angellock.impl.plugin.AbstractPlugin;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TextComponentSerializer;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.HandPreference;
import org.geysermc.mcprotocollib.protocol.data.game.setting.ChatVisibility;
import org.geysermc.mcprotocollib.protocol.data.game.setting.ParticleStatus;
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundClientInformationPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundSetCarriedItemPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundUseItemPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BaseDefaultPlugin extends AbstractPlugin {
    protected static final Logger log = LoggerFactory.getLogger("BotEntity");
    private static final String VERSION = "0.0.0";
    private static final String NAME = "Base-default-plugin";
    private long lastTitleTime;
    private String lastTitle;
    private String lastMsg;

    @Override
    public String getPluginName() {
        return NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public String getDescription() {
        return "null";
    }

    @Override
    public void onDisable() {
        log.info("disabling plugin: {}, {}", super.getName(), this.getVersion());
    }

    @Override
    public void onLoad() {
        log.info("loading plugin: {}, {}", super.getName(), this.getVersion());
    }

    @Override
    public void onEnable(AbstractRobot robotEntity) {

        getTerminalCommands().registerCommand(new TerminalCommand("reload", new ReloadCommandExecutor()));

        getListeners().add(new LoginHandler().addExtraAction(packet -> {
            log.info(ConsoleTokens.colorizeText("&l&bSuccessfully logged-in to server world."));

        }));

        getListeners().add(new SystemChatHandler().addExtraAction((packet) -> {
            TextComponentSerializer componentSerializer = new TextComponentSerializer();
            String msg = componentSerializer.serialize(packet.getContent());
            if (!msg.equals(this.lastMsg)) {
                this.lastMsg = msg;
                log.info(ConsoleTokens.colorizeText(msg));
            }
        }));

        getListeners().add(new PlayerLogInfoHandler.UpdateHandler().addExtraAction((updatePacket) -> {
            PlayerListEntry[] players = updatePacket.getEntries();

            for (PlayerListEntry player : players) {
                GameProfile playerProfile = player.getProfile();
                PlayerTracker.putPlayer(new Player(playerProfile));
                if (playerProfile != null) {
                    log.info(ConsoleTokens.colorizeText("&7[&a+&7]") + this.getLogMsg(playerProfile));
                }
            }
        }));

        getListeners().add(new TitlePacketHandler().addExtraAction((titleTextPacket)-> {
            String currentText = ((TextComponent) titleTextPacket.getText()).content();
            if (!currentText.equals(this.lastTitle) || System.currentTimeMillis() - this.lastTitleTime > 1500) {
                TextComponentSerializer serializer = new TextComponentSerializer();
                String titleMsg = serializer.serialize(titleTextPacket.getText());
                log.info(ConsoleTokens.colorizeText("&7&l[&6FromTitle&7] &R" + titleMsg));
                this.lastTitleTime = System.currentTimeMillis();
                this.lastTitle = currentText;
            }
        }));

        getListeners().add(new PlayerLogInfoHandler.RemoveHandler().addExtraAction((packet -> {
            if(packet.getProfileIds().isEmpty()) {
                return;
            }
            UUID logoutPlayer = packet.getProfileIds().get(0);
            if (PlayerTracker.getPlayerByUUID(logoutPlayer) == null) {
                return;
            }
            Player player = PlayerTracker.getPlayerByUUID(logoutPlayer);

            log.info(ConsoleTokens.colorizeText("&7[&4-&7]") + this.getLogMsg(player.getProfile()));
        })));

    }

    public String getLogMsg(GameProfile player){

        List<GameProfile.Property> playerProperty = player.getProperties();
        String state = (playerProperty.isEmpty()) ? " &7[&4盗版&7] " : " &7[&a正版&7] ";
        String playerName = player.getName();
        UUID playerUUID = player.getId();

        return ConsoleTokens.colorizeText("&b" + playerName + state + "&7" + playerUUID);

//        if(!playerProperty.isEmpty()){
//            log.info(ConsoleTokens.standardizeText(ConsoleTokens.YELLOW + playerName + "的正版皮肤: " + ConsoleTokens.GRAY + playerProperty));
//        }
        //TODO Move this code to skin recorder class
    }

    public void joinGame(AbstractRobot player){
        player.sendPacket(new ServerboundClientInformationPacket("en-us", 12, ChatVisibility.FULL, true, new ArrayList<>(), HandPreference.LEFT_HAND, true, true, ParticleStatus.ALL));
                    player.sendPacket(new ServerboundSetCarriedItemPacket(1));
                    player.sendPacket(new ServerboundUseItemPacket(
                            Hand.MAIN_HAND,
                            (int) Instant.now().toEpochMilli(),
                            (float) Math.random()*90,
                            (float) Math.random()*90
                    ));
    }
}
