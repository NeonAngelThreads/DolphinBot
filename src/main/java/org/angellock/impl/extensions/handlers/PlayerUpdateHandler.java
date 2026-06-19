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

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.api.handlers.PlayerLogInfoHandler;
import org.angellock.impl.ingame.Player;
import org.angellock.impl.ingame.PlayerTracker;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.PlayerInfoHelper;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerUpdateHandler extends PlayerLogInfoHandler.UpdateHandler {
    protected static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&ePlayers"));
    public PlayerUpdateHandler(PlayerInfoHelper helper, AbstractRobot bot) {
        this.addExtraAction((updatePacket) -> {
            PlayerListEntry[] players = updatePacket.getEntries();
            for (PlayerListEntry player : players) {
                GameProfile playerProfile = player.getProfile();
                PlayerTracker.putPlayer(new Player(playerProfile));

                if (playerProfile != null) {
                    log.info(bot.getBotLabel(), ConsoleTokens.colorizeText("&7[&a+&7]{}"), helper.getLogMsg(playerProfile));
                }
            }
        });
    }
}
