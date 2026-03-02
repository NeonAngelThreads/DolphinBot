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

package org.angellock.impl.api.websocket.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.api.websocket.APIResponseHandler;
import org.angellock.impl.managers.BotManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotsHandler extends APIResponseHandler {

    private final String method;

    public BotsHandler(String method) {
        this.method = method;
    }

    @Override
    public void handleResponse(HttpExchange exchange) throws IOException {
        List<Map<String, Object>> bots = new ArrayList<>();
        for (Map.Entry<String, RobotPlayer> entry : BotManager.bots().entrySet()) {
            RobotPlayer bot = entry.getValue();
            Map<String, Object> botInfo = new HashMap<>();
            botInfo.put("name", bot.getInfoHelper().getName());
            botInfo.put("profileName", bot.getProfileName());
            botInfo.put("address", String.format("%s:%s",bot.getInfoHelper().getServer(), bot.getInfoHelper().getPort()));
            botInfo.put("isConnected", bot.getSession() != null && bot.getSession().isConnected());
            botInfo.put("gameMode", bot.getServerGamemode() != null ? bot.getServerGamemode().name() : "UNKNOWN");
            botInfo.put("position", Map.of(
                    "x", bot.getPosition().getX(),
                    "y", bot.getPosition().getY(),
                    "z", bot.getPosition().getZ()
            ));
            bots.add(botInfo);
        }
        this.sendResponse(exchange, 200, Map.of("success", true, "data", bots));
    }

    @Override
    public boolean isMethodAllowed(HttpExchange exchange) {
        return exchange.getRequestMethod().equalsIgnoreCase(this.method);
    }
}
