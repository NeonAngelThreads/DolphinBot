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

package org.angellock.impl.util.math;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cloudburstmc.math.vector.Vector3d;

@Setter
@Getter
@ToString
public class Position {
    private double X;
    private double Y;
    private double Z;

    public Position(double x, double y, double z) {
        X = x;
        Y = y;
        Z = z;
    }

    public Position() {
        X = 0;
        Y = 0;
        Z = 0;
    }

    public double getDistance(double x1, double y1, double z1){
        return Math.sqrt((x1-X)*(x1-X) + (y1-Y)*(y1-Y) + (z1-Z)*(z1-Z));
    }
    public double getDistance(Position position){
        return Math.sqrt((position.X-X)*(position.X-X) + (position.Y-Y)*(position.Y-Y) + (position.Z-Z)*(position.Z-Z));
    }
    public Position add(double x, double y, double z) {
        this.X += x;
        this.Y += y;
        this.Z += z;
        return this;
    }

    public void from(Vector3d vector3d){
        this.X = vector3d.getX();
        this.Y = vector3d.getY();
        this.Z = vector3d.getZ();
    }

    public void from(double x, double y, double z){
        this.X = x;
        this.Y = y;
        this.Z = z;
    }

}
