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

package org.angellock.impl;

import lombok.Getter;

@Getter
public enum EnumSystemEvents {
    COMMAND_NOT_FOUND("command.notfound"),
    RECONNECT("server.reconnect"),
    CONNECT("server.connect"),
    DISCONNECT("server.disconnect"),
    PACKET_ERROR("packet.error"),
    DOLPHIN_TIMING_RESET("dolphin.timing.reset"),
    SERVER_PLAYER_GAMEMODE("server.player.gamemode"),
    SERVER_WORLD_JOIN("server.world.join"),
    SERVER_CONNECTION_ESTABLISHED("server.connection.established"),
    LOGIN_STATEMACHINE_TRANSIT("dolphin.statemachine.login.transit"),
    PLAYER_INFO_CRACKED("info.cracked"),
    PLAYER_INFO_ONLINE("info.online");

    private final String spaceName;

    EnumSystemEvents(String spaceName) {
        this.spaceName = spaceName;
    }
}
