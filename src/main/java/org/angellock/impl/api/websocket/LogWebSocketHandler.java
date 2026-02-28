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

package org.angellock.impl.api.websocket;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.unimi.dsi.fastutil.Pair;
import org.angellock.impl.api.WebLogAppender;
import org.angellock.impl.util.strings.BaseLine;
import org.angellock.impl.win32terminal.SystemTabCompleter;
import org.jline.reader.Candidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class LogWebSocketHandler extends WebSocketServer {
    private static final Logger log = LoggerFactory.getLogger("LogWebSocket");
    private static final Gson gson = new Gson();
    private static final Map<WebSocket, BiConsumer<String, String>> clientListeners = new ConcurrentHashMap<>();

    public LogWebSocketHandler(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        log.debug("New WebSocket client connected: {}", conn.getRemoteSocketAddress());
        
        // create a log listener for dolphin client
        BiConsumer<String, String> logListener = (message, botName) -> {
            try {
                if (conn.isOpen()) {
                    conn.send(gson.toJson(Map.of(
                            "type", "log",
                            "target", botName,
                            "content", message
                    )));
                }
            } catch (Exception ignore) {
            }
        };
        
        clientListeners.put(conn, logListener);
        WebLogAppender.addLogListener(logListener);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        log.debug("WebSocket client disconnected: {}", conn.getRemoteSocketAddress());

        // remove listener
        BiConsumer<String, String> listener = clientListeners.remove(conn);
        if (listener != null) {
            WebLogAppender.removeLogListener(listener);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            Map<String, Object> request = gson.fromJson(message, Map.class);
            String type = (String) request.get("type");
            
            if ("complete".equals(type)) {
                // Tab complete request
                String input = (String) request.get("input");
                int cursor = ((Double) request.get("cursor")).intValue();

                List<Candidate> list = new ArrayList<>();
                List<CharSequence> webSideCandidate = new ArrayList<>();
                SystemTabCompleter.getInstance().complete(null, new BaseLine(input, cursor), list);

                list.forEach((candidate)-> webSideCandidate.add(candidate.value()));
                
                conn.send(gson.toJson(Map.of(
                        "type", "completion",
                        "input", input,
                        "candidates", webSideCandidate
                )));
            }
        } catch (Exception e) {
            log.error("Failed to process WebSocket message", e);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        log.error("WebSocket error", ex);
    }

    @Override
    public void onStart() {
        log.info("Log WebSocket Server started on port {}", getPort());
    }
}
