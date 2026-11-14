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

package org.angellock.impl.ingame.abstracts;

import org.angellock.impl.util.math.Position;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;

@SuppressWarnings("unused")
public interface IPlayer {
    double getDistanceFromOthers(IPlayer player);

    Position getPosition();

    float getYRot();

    float getXRot();

    void setRotation(float yRot, float xRot);

    void setFeetPosition(double x, double y, double z);

    int nextUseSequence();

    void aimAt(double targetX, double targetY, double targetZ);

    void interactBlock(double x, double y, double z);

    void interactBlock(int x, int y, int z);

    void useItem(Hand hand);
}
