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
import org.angellock.impl.api.websocket.APIResponseHandler;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.util.reason.KickReason;

import java.io.IOException;
import java.util.Map;

public class DeleteBotHandler extends APIResponseHandler {
    @Override
    public void handleResponse(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            String botName = getResponseBody(exchange).get("botName");
            //BotManager.getBotByProfileName(botName).getSession().disconnect(KickReason.CLOSED_BY_API.name());

            success = BotManager.removeBot(botName);
        } catch (Exception e) {
            this.sendResponse(exchange, 500, Map.of("success", false, "message", e.getMessage()));
        }
        if (success){
            this.sendResponse(exchange, 200, Map.of("success", true, "message", "Bot deleted successfully"));
        }else {
            this.sendResponse(exchange, 500, Map.of("success", false, "message", "Could not deleted the bot"));
        }
    }

    @Override
    public boolean isMethodAllowed(HttpExchange exchange) {
        return "POST".equals(exchange.getRequestMethod());
    }
}
