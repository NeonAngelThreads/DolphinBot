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

package org.angellock.impl;

import lombok.Setter;
import org.angellock.impl.events.annotations.EventHandler;
import org.angellock.impl.events.bukkit.AbstractEvent;
import org.angellock.impl.ingame.IPlayer;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.plugin.PluginManager;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TranslatableUtil;
import org.angellock.impl.util.math.Position;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.data.game.entity.object.Direction;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundUseItemOnPacket;

import java.util.Optional;

public class RobotPlayer extends AbstractRobot implements IPlayer {
    private long connectTime;
    private long lastMsgTime = 0L;
    private final long msgDelay;
    protected @Setter Position loginPos = new Position();

    public RobotPlayer(ConfigManager configManager, PluginManager pluginManager) {
        super(configManager, pluginManager);

        this.msgDelay = Long.parseLong(Optional
                .ofNullable(
                        this.globalConfig.getConfigValue("msg-send-delay"))
                .orElse("3000")
        );
    }

    @Override
    public boolean canSendMessages() {
        long t = System.currentTimeMillis();
        if (t - lastMsgTime > msgDelay) {
            this.lastMsgTime = t;
            return true;
        }
        return false;
    }

    @Override
    public void onJoin() {
        log.info(this.getBotLabel(), TranslatableUtil.getFormattedMessage(EnumSystemEvents.SERVER_CONNECTION_ESTABLISHED, this.getProfileName()));
    }

    @Override
    public void onQuit(String reason) {
        long millis = System.currentTimeMillis() - this.connectTime;
        log.info(this.getBotLabel(), ConsoleTokens.colorizeText("[{}] &7Session Duration: &f{}ms"), this.getProfileName(), millis);
        log.info(TranslatableUtil.getFormattedMessage(EnumSystemEvents.DISCONNECT, reason));
        this.getPluginManager().disableAllPlugins(this);
        this.getSession().getChannel().close();
        this.getSession().getChannel().deregister();
        this.getSession().getChannel().closeFuture();
        TranslatableUtil.infoTranslatableOf(EnumSystemEvents.DOLPHIN_TIMING_RESET);
    }

    public void callHandleableEvent(AbstractEvent event){
        this.getPluginManager().event().broadcastEvent(event);
    }

    @Override
    public void onKicked() {
        return;
    }

    @Override
    public void onPreLogin() {
        this.connectTime = System.currentTimeMillis();
        log.info(TranslatableUtil.getFormattedMessage(EnumSystemEvents.CONNECT, this.config().getServer(), this.config().getPort()));
    }

    @Override
    public double getDistanceFromOthers(IPlayer player) {
        return this.getPosition().getDistance(player.getPosition());
    }

    @Override
    public Position getPosition() {
        return this.loginPos;
    }

    @Override
    public void interactBlock(double x, double y, double z) {
        this.interactBlock((int) x, (int) y, (int) z, (int) System.currentTimeMillis());
    }

    public void interactBlock(int x, int y, int z, int i3) {
        this.sendPacket(new ServerboundUseItemOnPacket(Vector3i.from(x, y, z), Direction.NORTH, Hand.MAIN_HAND, 0f, 0f, 0f, false, i3));
    }
}
