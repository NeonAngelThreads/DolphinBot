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

import org.angellock.impl.RobotPlayer;
import org.angellock.impl.events.AbstractEventProcessor;
import org.angellock.impl.api.events.game.JoinedGameEvent;
import org.angellock.impl.util.math.Position;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.PositionElement;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;

import java.util.List;

public class PlayerPositionPacket extends AbstractEventProcessor<ClientboundPlayerPositionPacket> {
    public PlayerPositionPacket(RobotPlayer robot) {
        this.addExtraAction((packet->{
            // ClientPacketListener.handleMovePlayer: resolve relative flags against the
            // current position/rotation before accepting the teleport.
            List<PositionElement> relatives = packet.getRelatives();
            double x = relatives.contains(PositionElement.X) ? robot.getPosition().getX() + packet.getPosition().getX() : packet.getPosition().getX();
            double y = relatives.contains(PositionElement.Y) ? robot.getPosition().getY() + packet.getPosition().getY() : packet.getPosition().getY();
            double z = relatives.contains(PositionElement.Z) ? robot.getPosition().getZ() + packet.getPosition().getZ() : packet.getPosition().getZ();
            float yRot = relatives.contains(PositionElement.Y_ROT) ? robot.getYRot() + packet.getYRot() : packet.getYRot();
            float xRot = relatives.contains(PositionElement.X_ROT) ? robot.getXRot() + packet.getXRot() : packet.getXRot();

            // Reply AcceptTeleportation + PosRot, otherwise the server keeps
            // awaitingPositionFromClient set and ignores all block interactions.
            robot.onServerTeleport(packet.getId(), x, y, z, yRot, xRot);

            robot.getPluginManager().event().broadcastEvent(new JoinedGameEvent(new Position(x, y, z), robot));
        }));
    }


    @Override
    protected boolean isTargetPacket(Packet minecraftPacket) {
        return (minecraftPacket instanceof ClientboundPlayerPositionPacket);
    }
}
