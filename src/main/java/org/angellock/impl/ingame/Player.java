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

import org.angellock.impl.util.math.Position;
import org.geysermc.mcprotocollib.auth.GameProfile;

public class Player implements IPlayer{
    private int id;
    private GameProfile profile;
    private Position position;

    public Player(int id, Position position) {
        this.id = id;
        this.position = position;
    }

    public Player(GameProfile profile) {
        this.profile = profile;
    }

    public GameProfile getProfile() {
        return profile;
    }

    public void setProfile(GameProfile profile) {
        this.profile = profile;
    }

    public int getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public void interactBlock(double x, double y, double z) {

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


}
