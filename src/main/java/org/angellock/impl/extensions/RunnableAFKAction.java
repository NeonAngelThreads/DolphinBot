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

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.util.TimingUtil;
import org.angellock.impl.util.math.Position;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundUseItemPacket;

import java.time.Instant;
import java.util.Random;

public class RunnableAFKAction implements Runnable {

    private AbstractRobot bot;

    private Random random = TimingUtil.getRandomizer();

    public RunnableAFKAction(AbstractRobot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            Position position = ((RobotPlayer) this.bot).getPosition();
            int x = (int) position.getX() - random.nextInt(-2, 2);
            int y = (int) position.getY() - random.nextInt(-2, 2);
            int z = (int) position.getZ() - random.nextInt(-2, 2);
            bot.sendPacket(new ServerboundUseItemPacket(
                    Hand.MAIN_HAND,
                    (int) Instant.now().toEpochMilli(),
                    (float) Math.random() * 90,
                    (float) Math.random() * 90
            ));
            ((RobotPlayer) bot).interactBlock(x, y, z, (int) System.currentTimeMillis());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }

        }
    }

}
