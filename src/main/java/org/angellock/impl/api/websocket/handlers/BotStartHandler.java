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
import java.util.Map;

public class BotStartHandler extends APIResponseHandler {

    private final String method;

    public BotStartHandler(String method) {
        this.method = method;
    }

    @Override
    public void handleResponse(HttpExchange exchange) throws IOException {
        try {
            String profileName = (getResponseBody(exchange)).get("profileName");
            RobotPlayer robotPlayer = BotManager.getBotByName(profileName);
            if (robotPlayer != null){
                robotPlayer.scheduleConnect();
                robotPlayer.setShouldReconnect(true);
            } else {
                this.sendResponse(exchange, 400, Map.of("success", false, "message", "could not find bot: "+profileName));
            }
        } catch (Exception e) {
            this.sendResponse(exchange, 400, Map.of("success", false, "message", "Invalid request: " + e.getMessage()));
        }finally {
            this.sendResponse(exchange, 200, Map.of("success", true, "message", "Bot started successfully"));
        }
    }

    @Override
    public boolean isMethodAllowed(HttpExchange exchange) {
        return exchange.getRequestMethod().equalsIgnoreCase(this.method);
    }
}
