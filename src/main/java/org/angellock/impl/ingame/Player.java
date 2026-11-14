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

package org.angellock.impl.ingame;

import lombok.Getter;
import lombok.Setter;
import org.angellock.impl.ingame.abstracts.IPlayer;
import org.angellock.impl.util.math.Position;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;

@Getter
@SuppressWarnings("unused")
public class Player implements IPlayer{
    private int id;
    @Setter
    private GameProfile profile;
    private Position position;

    public Player(int id, Position position) {
        this.id = id;
        this.position = position;
    }

    public Player(GameProfile profile) {
        this.profile = profile;
    }

    @Override
    public void interactBlock(double x, double y, double z) {
    }

    @Override
    public void interactBlock(int x, int y, int z) {

    }

    @Override
    public void useItem(Hand hand) {

    }

    public void setPosition(Position position) {
        this.position = position;
    }
    public void setPosition(double x, double y, double z) {
        if (this.position != null) {
            this.position.setX(x);
            this.position.setY(y);
            this.position.setZ(z);
        } else {
            this.position = new Position(x, y, z);
        }
    }

    public void pushVelocity(double x, double y, double z) {
        this.position.add(x, y, z);
    }

    @Override
    public double getDistanceFromOthers(IPlayer player) {
        return position.getDistance(player.getPosition());
    }

    @Override
    public float getYRot() {
        return 0;
    }

    @Override
    public float getXRot() {
        return 0;
    }

    @Override
    public void setRotation(float yRot, float xRot) {

    }

    @Override
    public void setFeetPosition(double x, double y, double z) {

    }

    @Override
    public int nextUseSequence() {
        return 0;
    }

    @Override
    public void aimAt(double targetX, double targetY, double targetZ) {

    }
}
