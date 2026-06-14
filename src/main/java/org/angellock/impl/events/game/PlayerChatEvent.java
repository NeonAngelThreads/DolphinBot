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

package org.angellock.impl.events.game;

import lombok.Getter;
import org.angellock.impl.events.HandlerMapper;
import org.angellock.impl.events.bukkit.AbstractEvent;
import org.angellock.impl.util.math.Position;
import org.geysermc.mcprotocollib.protocol.data.game.entity.type.EntityType;

@Getter
public class PlayerChatEvent extends AbstractEvent {
    private static final HandlerMapper HANDLERS = new HandlerMapper();
    private final String player;
    private final String message;

    public PlayerChatEvent(String player, String message) {
        this.player = player;
        this.message = message;
    }
    @Override
    public HandlerMapper getMapper() {
        return HANDLERS;
    }
    public static HandlerMapper getHandlers() {
        return HANDLERS;
    }

}
