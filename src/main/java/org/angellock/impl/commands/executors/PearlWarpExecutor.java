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

package org.angellock.impl.commands.executors;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.commands.ICommandAction;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.data.game.entity.object.Direction;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundUseItemOnPacket;

public class PearlWarpExecutor implements ICommandAction {

    @Override
    public void onCommand(CommandResponse responseEntity, AbstractRobot bot) {
        ServerboundUseItemOnPacket packet = new ServerboundUseItemOnPacket(
                Vector3i.from(-7, 68, -41),
                Direction.NORTH,
                Hand.MAIN_HAND,
                1f, 1f, 1f,
                false,
                (int) System.currentTimeMillis()
        );

        System.out.println(packet);
        bot.sendPacket(packet);
    }
}
