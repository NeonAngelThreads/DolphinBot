package org.angellock.impl.ingame;

import org.angellock.impl.ingame.abstracts.IWalkable;
import org.angellock.impl.util.math.Position;

public interface IPlayer extends IWalkable {
    double getDistanceFromOthers(IPlayer player);

    Position getPosition();
}
