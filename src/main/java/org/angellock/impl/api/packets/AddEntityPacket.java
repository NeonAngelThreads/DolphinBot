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

package org.angellock.impl.api.packets;

import org.angellock.impl.events.AbstractEventProcessor;
import org.angellock.impl.api.events.game.EntityEmergedEvent;
import org.angellock.impl.util.math.Position;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundAddEntityPacket;


public class AddEntityPacket extends AbstractEventProcessor<ClientboundAddEntityPacket> {
    public AddEntityPacket() {
        this.preAction = ((packet) -> {
            Position position = new Position(packet.getX(), packet.getY(), packet.getZ());
            EntityEmergedEvent entityEmergedEvent = new EntityEmergedEvent(packet.getType(), position);
            dispatch(entityEmergedEvent);
        });
    }

    @Override
    protected boolean isTargetPacket(Packet minecraftPacket) {
        return (minecraftPacket instanceof ClientboundAddEntityPacket);
    }
}
