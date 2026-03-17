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
    COMMAND_NAME("command.name"),
    COMMAND_ALIASES("command.aliases"),
    COMMAND_PROVIDER("command.provider"),
    COMMAND_USAGE("command.usage"),
    COMMAND_DESCRIPTION("command.description"),
    PLUGIN_LISTENER_LOAD("plugin.listener.load"),
    PLUGIN_LOAD("plugin.load"),
    PLUGIN_EVENT_HANDLER_LOAD("plugin.event.handler.load"),
    PLUGIN_DISABLE("plugin.disable"),
    PLUGIN_EVENT_HANDLER_DISABLE("plugin.event.handler.remove"),
    RECONNECT("server.reconnect"),
    CONNECT("server.connect"),
    DISCONNECT("server.disconnect"),
    PACKET_ERROR("packet.error"),
    DOLPHIN_TIMING_RESET("dolphin.timing.reset"),
    SERVER_PLAYER_GAMEMODE("server.player.gamemode"),
    PLUGIN_ERROR("plugin.error"),
    SERVER_WORLD_JOIN("server.world.join"),
    CHAT_MESSAGE_SEND("chat.message.send"),
    CHAT_COMMAND_SEND("chat.command.send"),
    SERVER_CONNECTION_ESTABLISHED("server.connection.established"),
    LOGIN_STATEMACHINE_TRANSIT("dolphin.statemachine.login.transit"),
    PLUGIN_LOAD_COMMANDS("plugin.load.command"),
    PLUGIN_LOAD_TERMINAL_COMMANDS("plugin.load.terminal.command"),
    PLUGIN_LOAD_COMPLETE("plugin.load.complete"),
    PLAYER_INFO_CRACKED("info.cracked"),
    PLAYER_INFO_ONLINE("info.online"),
    CHAT_COMMAND_DETECTED("chat.command.detected"),
    PROXY_CONFIG_INVALID("proxy.config.invalid"),
    DOLPHIN_BOTS_LOAD("dolphin.bot.load"),
    PROXY_CONFIG_LOAD("proxy.config.load"),
    CONFIG_FILE_LOADED("bot.config.file.loaded"),
    COMMANDLINE_LOADED("commandline.enabled");

    private final String spaceName;

    EnumSystemEvents(String spaceName) {
        this.spaceName = spaceName;
    }
}
