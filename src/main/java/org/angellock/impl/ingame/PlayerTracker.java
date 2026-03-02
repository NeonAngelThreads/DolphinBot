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

import lombok.Getter;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTracker {
    @Getter
    private final static Map<UUID, Player> onlinePlayers = new HashMap<>();
    @Getter
    private final static Map<Integer, UUID> UUIDMapping = new HashMap<>();
    private final static Map<String, UUID> playerUUIDMapping = new HashMap<>();
    public @Nullable
    static Player getPlayerById(int entityID) {
        UUID uuid = UUIDMapping.get(entityID);
        if (uuid != null) {
            return onlinePlayers.get(uuid);
        }
        return null;
    }

    public static boolean canRemove(UUID playerID) {
        return onlinePlayers.get(playerID) != null;
    }

    public static Player getPlayerByUUID(UUID uuid) {
        return onlinePlayers.get(uuid);
    }

    public @Nullable
    static Player getPlayerByName(String name) {
        UUID uuid = playerUUIDMapping.get(name);
        if (uuid != null) {
            return onlinePlayers.get(uuid);
        }
        return null;
    }

    public static void putPlayer(@NotNull Player player) {
        @Nullable GameProfile profile = player.getProfile();
        if (profile != null) {
            UUID uuid = profile.getId();
            onlinePlayers.put(uuid, player);
            playerUUIDMapping.put(profile.getName(), uuid);
            UUIDMapping.put(player.getId(), uuid);
        }
    }

    public static void delPlayer(@NotNull UUID player) {
        onlinePlayers.remove(player);
    }

    public static Map<String, UUID> getPlayerUUIDMapping() {
        return playerUUIDMapping;
    }

}
