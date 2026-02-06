/*
 * This file is a part of DolphinBot, see <https://github.com/NeonAngelThreads/DolphinBot>
 *
 *     Copyright (C) 2025-2026 NeonAngelThreads
 *
 *     This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 *     published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should
 *     have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc.,
 *      51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact with me> Bilibili space: https://space.bilibili.com/386644641
 */

package org.angellock.impl.events.handlers;

import org.angellock.impl.events.AbstractEventProcessor;
import org.angellock.impl.events.packets.AddEntityPacket;
import org.angellock.impl.events.types.EntityEmergedEvent;
import org.angellock.impl.ingame.Player;
import org.angellock.impl.ingame.PlayerTracker;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.math.Position;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.data.game.entity.type.EntityType;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.spawn.ClientboundAddEntityPacket;

import static org.angellock.impl.plugin.PluginManager.event;

public class PlayerEmergeHandler extends AddEntityPacket {
    public PlayerEmergeHandler() {
        this.addExtraAction((entityPacket -> {
            if (entityPacket.getType() == EntityType.PLAYER) {
                Player player = PlayerTracker.getPlayerByUUID(entityPacket.getUuid());
                if (player != null) {
                    //log.info(ConsoleTokens.colorizeText("[PlayerTracker]: &3A player was detected: &d{}"), player.getProfile().getName());
                    event().broadcastEvent(new EntityEmergedEvent(EntityType.PLAYER, new Position(entityPacket.getX(), entityPacket.getY(), entityPacket.getZ())));
                    player.setPosition(entityPacket.getX(), entityPacket.getY(), entityPacket.getZ());
                }
            }
        }));
    }
}
