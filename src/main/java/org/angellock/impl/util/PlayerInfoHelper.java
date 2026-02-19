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

package org.angellock.impl.util;

import org.angellock.impl.EnumSystemEvents;
import org.geysermc.mcprotocollib.auth.GameProfile;

import java.util.List;
import java.util.UUID;

public class PlayerInfoHelper {
    private String onlineSuffix = TranslatableUtil.getFormattedMessage(EnumSystemEvents.PLAYER_INFO_ONLINE);
    private String crackedSuffix = TranslatableUtil.getFormattedMessage(EnumSystemEvents.PLAYER_INFO_CRACKED);
    private boolean captureSkins;

    public PlayerInfoHelper(boolean captureSkins) {
        this.captureSkins = captureSkins;
    }

    public String getLogMsg(GameProfile player){

        List<GameProfile.Property> playerProperty = player.getProperties();
        String state = (playerProperty.isEmpty()) ? this.crackedSuffix : this.onlineSuffix;
        String playerName = player.getName();
        UUID playerUUID = player.getId();
//        if (!playerProperty.isEmpty() && this.captureSkins) {
//            log.info(ConsoleTokens.colorizeText("&e{} 的正版皮肤: &7{}"), playerName, playerProperty);
//        }
        //TODO Move this code to skin recorder class

        return ConsoleTokens.colorizeText("&b" + playerName + state + "&7" + playerUUID);

    }
}
