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

package org.angellock.impl.extensions.handlers;

import org.angellock.impl.events.handlers.PlayerChatPacketHandler;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TextComponentSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerChatDisplay extends PlayerChatPacketHandler {
    protected static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&3Chat"));
    public PlayerChatDisplay() {
        this.addExtraAction((packet) -> {
            TextComponentSerializer componentSerializer = new TextComponentSerializer();
            String msg = packet.getContent();
            String player = componentSerializer.serialize(packet.getName());
            log.info(ConsoleTokens.colorizeText("&6{}&7>> {}"), player, ConsoleTokens.colorizeText(msg));
        });
    }
}
