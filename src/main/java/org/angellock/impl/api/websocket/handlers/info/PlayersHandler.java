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

package org.angellock.impl.api.websocket.handlers.info;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import io.netty.handler.codec.base64.Base64Decoder;
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.api.websocket.APIResponseHandler;
import org.angellock.impl.ingame.PlayerTracker;
import org.angellock.impl.managers.BotManager;
import org.geysermc.mcprotocollib.auth.GameProfile;

import java.io.IOException;
import java.util.*;

public class PlayersHandler extends APIResponseHandler {

    private final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    private final Gson gsonHelper = APIResponseHandler.gsonHelper;

    @Override
    public void handleResponse(HttpExchange exchange) throws IOException {
        List<Map<String, Object>> player = new ArrayList<>();
        for (GameProfile profile : PlayerTracker.getPlayerProfiles().values()) {
            UUID uuid = profile.getId();
            List<GameProfile.Property> property = profile.getProperties();
            if (!property.isEmpty()) {
                String json = new String(BASE64_DECODER.decode(property.get(0).getValue()));
                Map<String, JsonElement> jsonElementMap = gsonHelper.fromJson(json, JsonObject.class).asMap();

                String name = jsonElementMap
                            .get("profileName")
                            .getAsString();
                String url = jsonElementMap
                            .get("textures")
                            .getAsJsonObject()
                            .get("SKIN")
                            .getAsJsonObject()
                            .get("url")
                            .getAsString();

                player.add(Map.of("name", name, "skin", url, "uuid", uuid));
                continue;
            }
            player.add(Map.of("name", profile.getName(), "skin", "null", "uuid", uuid));
        }
        sendResponse(exchange, 200, Map.of("data",player));
    }

    @Override
    public boolean isMethodAllowed(HttpExchange exchange) {
        return true;
    }
}
