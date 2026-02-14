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

package org.angellock.impl.events.handlers;

import org.angellock.impl.events.game.EntityEmergedEvent;
import org.angellock.impl.events.packets.AddEntityPacket;
import org.angellock.impl.ingame.Player;
import org.angellock.impl.ingame.PlayerTracker;
import org.angellock.impl.util.math.Position;
import org.geysermc.mcprotocollib.protocol.data.game.entity.type.EntityType;

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
