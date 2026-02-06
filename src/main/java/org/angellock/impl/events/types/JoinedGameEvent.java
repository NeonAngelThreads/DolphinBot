/*
 * This file is a part of DolphinBot, see <https://github.com/NeonAngelThreads/DolphinBot>
 *
 *     Copyright (C) 2025-2026 NeonAngelThreads
 *
 *     This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 *     published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should
 *     have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc.,
 *      51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact with me> Bilibili space: https://space.bilibili.com/386644641
 */

package org.angellock.impl.events.types;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.events.HandlerMapper;
import org.angellock.impl.events.bukkit.Event;
import org.angellock.impl.util.math.Position;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;

public class JoinedGameEvent extends Event {
    private static final HandlerMapper HANDLERS = new HandlerMapper();

    private final Position position;

    private final AbstractRobot robot;

    public JoinedGameEvent(Position position, AbstractRobot robot) {
        this.position = position;
        this.robot = robot;
    }

    @Override
    public HandlerMapper getMapper() {
        return HANDLERS;
    }

    public static HandlerMapper getHandlers() {
        return HANDLERS;
    }

    public Position getPosition() {
        return position;
    }

    public AbstractRobot getRobot() {
        return robot;
    }
}
