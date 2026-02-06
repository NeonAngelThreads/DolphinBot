package org.angellock.impl.util.math;

import org.cloudburstmc.math.vector.Vector3d;

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

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getY() {
        return Y;
    }

    public void setY(double y) {
        Y = y;
    }

    public double getZ() {
        return Z;
    }

    public void setZ(double z) {
        Z = z;
    }
}
