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

package org.angellock.impl.extensions;

import net.kyori.adventure.text.TextComponent;
import org.angellock.impl.AbstractRobot;
import org.angellock.impl.EnumSystemEvents;
import org.angellock.impl.commands.CommandBuilder;
import org.angellock.impl.commands.dolphin.completers.LoadPluginCompleter;
import org.angellock.impl.commands.executors.*;
import org.angellock.impl.commands.terminal.TerminalCommand;
import org.angellock.impl.commands.terminal.TerminalCommandBuilder;
import org.angellock.impl.events.handlers.*;
import org.angellock.impl.ingame.Player;
import org.angellock.impl.ingame.PlayerTracker;
import org.angellock.impl.listeners.JoinGameListener;
import org.angellock.impl.listeners.PlayerListener;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.plugin.AbstractPlugin;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TextComponentSerializer;
import org.angellock.impl.util.TranslatableUtil;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.HandPreference;
import org.geysermc.mcprotocollib.protocol.data.game.setting.ChatVisibility;
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
    private static final String VERSION = "1.2.5";
    private static final String NAME = "Base-default-plugin";
    private long lastTitleTime;
    private String lastTitle;
    private String lastMsg;

    private int blockSequence = 0;

    private String onlineSuffix = TranslatableUtil.getFormattedMessage(EnumSystemEvents.PLAYER_INFO_ONLINE);
    private String crackedSuffix = TranslatableUtil.getFormattedMessage(EnumSystemEvents.PLAYER_INFO_CRACKED);

    private Thread tickThread = null;
    private boolean captureSkins;

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
        this.getListeners().clear();
        if (this.tickThread != null) {
            this.tickThread.interrupt();
        }
    }

    @Override
    public void onLoad() {
        log.info("loading plugin: {}, {}", super.getName(), this.getVersion());
        this.captureSkins = ConfigManager.getCoreSettings().getOther().isEnableSkinRecorder();
    }

    @Override
    public void onEnable(AbstractRobot robotEntity) {

        getEvents().registerListeners(new JoinGameListener(), this);

        getTerminalCommands().registerCommand(new TerminalCommand("reload", new ReloadCommandExecutor()));
        getTerminalCommands().registerCommand(new TerminalCommand("load", new LoadCommandExecutor()), new LoadPluginCompleter());
        getTerminalCommands().registerCommand(new TerminalCommand("respawn", new RespawnExecutor()));
        getTerminalCommands().registerCommand(new TerminalCommand("warp", new PearlWarpExecutor()));

        getTerminalCommands().registerCommand(new TerminalCommandBuilder()
                .withName("license")
                .withAliases("lic", "l")
                .withDescription("A command to show the license")
                .withProvider(this)
                .build(new LicenseExecutor())
        );
        getTerminalCommands().registerCommand(new TerminalCommandBuilder()
                .withName("help")
                .withAliases("h", "?", "？")
                .withDescription("Show help.")
                .withProvider(this)
                .build(new HelpExecutor())
        );

        if (robotEntity.config().getDebugSettings().isEnablePacketDebug()) {
            getEvents().registerListeners(new PlayerListener(), this);
        }
        robotEntity.getRegisteredCommands().register(new CommandBuilder().withName("reload").allowedUsers(robotEntity.getInfoHelper().getOwners()).build((response, bot) -> {
            long timeElapse = System.currentTimeMillis();
            robotEntity.getPluginManager().reloadPlugin(robotEntity, response.getCommandList()[1].toLowerCase());
            long time = (System.currentTimeMillis() - timeElapse);
            robotEntity.getMessageManager().putMessage("[INFO]操作已成功完成。耗时" + time + "ms");
        }));

        getListeners().add(new LoginHandler().addExtraAction(packet -> {
            log.info(ConsoleTokens.colorizeText(""));
            this.joinGame(robotEntity);
        }));

        getListeners().add(new BlockChangedAckHandler().addExtraAction((packet -> {
            this.blockSequence = packet.getSequence();
        })));

        getListeners().add(new SystemChatHandler().addExtraAction((packet) -> {
            TextComponentSerializer componentSerializer = new TextComponentSerializer();
            String msg = componentSerializer.serialize(packet.getContent());
            if (!msg.equals(this.lastMsg)) {
                this.lastMsg = msg;
                log.info(robotEntity.getProfileName()+" "+ConsoleTokens.colorizeText(msg));
            }
        }));

        getListeners().add(new PlayerChatPacketHandler().addExtraAction((packet) -> {
            TextComponentSerializer componentSerializer = new TextComponentSerializer();
            String msg = packet.getContent();
            String player = componentSerializer.serialize(packet.getName());
            log.info(ConsoleTokens.colorizeText("&6{}&7>> {}"), player, ConsoleTokens.colorizeText(msg));

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
            if (!PlayerTracker.canRemove(logoutPlayer)) {
                return;
            }
            Player player = PlayerTracker.getPlayerByUUID(logoutPlayer);
            if (player != null) {
                PlayerTracker.delPlayer(logoutPlayer);
                log.info(ConsoleTokens.colorizeText("&7[&4-&7]") + this.getLogMsg(player.getProfile()));
            }
        })));

        if (robotEntity.config().isAntiAFK()) {
            this.tickThread = new Thread(new RunnableAFKAction(robotEntity));
            this.tickThread.start();
        }

    }

    public String getLogMsg(GameProfile player){

        List<GameProfile.Property> playerProperty = player.getProperties();
        String state = (playerProperty.isEmpty()) ? this.crackedSuffix : this.onlineSuffix;
        String playerName = player.getName();
        UUID playerUUID = player.getId();
        if (!playerProperty.isEmpty() && this.captureSkins) {
            log.info(ConsoleTokens.colorizeText("&e{} 的正版皮肤: &7{}"), playerName, playerProperty);
        }
        //TODO Move this code to skin recorder class

        return ConsoleTokens.colorizeText("&b" + playerName + state + "&7" + playerUUID);

    }

    public void joinGame(AbstractRobot player){
        player.sendPacket(new ServerboundClientInformationPacket("en-us", player.config().getMaxChunkView(), ChatVisibility.FULL, true, new ArrayList<>(), HandPreference.LEFT_HAND, true, true));

        player.sendPacket(new ServerboundSetCarriedItemPacket(1));
        player.sendPacket(new ServerboundUseItemPacket(
                            Hand.MAIN_HAND,
                            (int) Instant.now().toEpochMilli(),
                            (float) Math.random()*90,
                            (float) Math.random()*90
                    ));
    }
}
