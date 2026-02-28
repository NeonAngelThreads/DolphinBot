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
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.api.websocket.APIResponseHandler;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.util.reason.KickReason;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class BotStopHandler extends APIResponseHandler {
    private final String method;
    public BotStopHandler(String method) {
        this.method = method;
    }

    @Override
    public void handleResponse(HttpExchange exchange) throws IOException {
        try {
            String botName = (String) getResponseBody(exchange).get("botName");
            RobotPlayer bot = BotManager.getBotByProfileName(botName);
            if (bot != null && bot.getSession() != null) {
                bot.getSession().disconnect(KickReason.CLOSED_BY_API.name());
                this.sendResponse(exchange, 200, Map.of("success", true, "message", "Bot stopped successfully"));
            } else {
                this.sendResponse(exchange, 404, Map.of("success", false, "message", "Bot not found"));
            }
        } catch (Exception e) {
            this.sendResponse(exchange, 400, Map.of("success", false, "message", "Invalid request: " + e.getMessage()));
        }
    }

    @Override
    public boolean isMethodAllowed(HttpExchange exchange) {
        return exchange.getRequestMethod().equalsIgnoreCase(this.method);
    }
}
