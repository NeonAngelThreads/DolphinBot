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
import org.angellock.impl.extensions.executors.ChatReloadExecutor;
import org.angellock.impl.extensions.handlers.*;
import org.angellock.impl.extensions.tasks.RunnableAFKAction;
import org.angellock.impl.ingame.Player;
import org.angellock.impl.ingame.PlayerTracker;
import org.angellock.impl.listeners.JoinGameListener;
import org.angellock.impl.listeners.PlayerListener;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.plugin.AbstractPlugin;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.PlayerInfoHelper;
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
    private static final String VERSION = "1.2.5";
    private static final String NAME = "Base-default-plugin";
    private Thread tickThread = null;
    private PlayerInfoHelper helper;

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
        this.getListeners().clear();
        if (this.tickThread != null) {
            this.tickThread.interrupt();
        }
    }

    @Override
    public void onLoad() {
        boolean captureSkins = ConfigManager.getCoreSettings().getOther().isEnableSkinRecorder();
        this.helper = new PlayerInfoHelper(captureSkins);
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
        getCommands().register(
                new CommandBuilder()
                        .withName("reload")
                        .allowedUsers(
                                robotEntity.getInfoHelper().getOwners()
                        ).build(new ChatReloadExecutor())
        );

        getListeners().add(new LoginHandler().addExtraAction(packet -> this.joinGame(robotEntity)));
        getListeners().add(new SystemChatDisplay(robotEntity));
        getListeners().add(new PlayerChatDisplay(robotEntity));
        getListeners().add(new PlayerUpdateHandler(this.helper, robotEntity));
        getListeners().add(new TitleMessageDisplay(robotEntity));
        getListeners().add(new PlayerRemoveHandler(this.helper, robotEntity));

        if (robotEntity.config().isAntiAFK()) {
            this.tickThread = new Thread(new RunnableAFKAction(robotEntity));
            this.tickThread.start();
        }

    }

    public void joinGame(AbstractRobot player){
        player.sendPacket(new ServerboundClientInformationPacket("en-us", player.config().getMaxChunkView(), ChatVisibility.FULL, true, new ArrayList<>(), HandPreference.RIGHT_HAND, true, true));

        player.sendPacket(new ServerboundSetCarriedItemPacket(1));
        player.sendPacket(new ServerboundUseItemPacket(
                            Hand.MAIN_HAND,
                            (int) Instant.now().toEpochMilli(),
                            (float) Math.random()*90,
                            (float) Math.random()*90
                    ));
    }
}
