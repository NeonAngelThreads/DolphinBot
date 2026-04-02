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

import java.io.IOException;
import java.util.Map;

public class HeartBeatHandler extends APIResponseHandler {

    @Override
    public void handleResponse(HttpExchange exchange) throws IOException {
        this.sendResponse(exchange, 200, Map.of("status", "ok", "message", "DolphinBot API is running"));
    }

    @Override
    public boolean isMethodAllowed(HttpExchange exchange) {
        return true;
    }
}
