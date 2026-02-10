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
    public void interactBlock(double x, double y, double z, int s) {

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
