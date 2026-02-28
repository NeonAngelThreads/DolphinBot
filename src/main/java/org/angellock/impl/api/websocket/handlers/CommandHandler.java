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

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CommandHandler extends APIResponseHandler {
    private final String method;
    public CommandHandler(String method) {
        this.method = method;
    }
    @Override
    public boolean isMethodAllowed(HttpExchange exchange) {
        return exchange.getRequestMethod().equalsIgnoreCase(this.method);
    }

    @Override
    public void handleResponse(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> request = this.getResponseBody(exchange);
            String botName = request.get("botName");
            String command = request.get("command");
            RobotPlayer bot = BotManager.getBotByProfileName(botName);
            if (bot != null && bot.getMessageManager() != null) {
                bot.getMessageManager().putMessage(command);
                this.sendResponse(exchange, 200, Map.of("success", true, "message", "Command sent successfully"));
            } else {
                this.sendResponse(exchange, 404, Map.of("success", false, "message", "Bot not found or not connected"));
            }
        } catch (Exception ignore) {

        }
    }
}
