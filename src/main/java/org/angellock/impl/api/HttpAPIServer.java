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

package org.angellock.impl.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.api.websocket.LogWebSocketHandler;
import org.angellock.impl.api.websocket.handlers.*;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.reason.KickReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@Slf4j
public class HttpAPIServer {
    @Getter
    private static HttpAPIServer instance;
    private final HttpServer server;
    private final LogWebSocketHandler logWebSocketServer;
    private final int wsPort;

    public HttpAPIServer(int port) throws IOException {
        instance = this;
        this.wsPort = port + 1;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.setExecutor(Executors.newCachedThreadPool());

        registerRoutes();

        // 启动日志WebSocket服务
        this.logWebSocketServer = new LogWebSocketHandler(wsPort);
        this.logWebSocketServer.start();

        this.server.start();
        log.info(ConsoleTokens.colorizeText("&aHTTP API Server started on port &b{}&a, ready to accept requests"), port);
        log.info(ConsoleTokens.colorizeText("&aLog WebSocket Server started on port &b{}&a"), wsPort);
    }

    private void registerRoutes() {
        server.createContext("/api/health", new HeartBeatHandler());
        server.createContext("/api/bots", new BotsHandler("GET"));
        server.createContext("/api/bots/start", new BotStartHandler("POST"));
        server.createContext("/api/bots/stop", new BotStopHandler("POST"));
        server.createContext("/api/bots/send-command", new CommandHandler("POST"));
        server.createContext("/api/config", new ConfigHandler());
        server.createContext("/api/bot/create", new AddedBotHandler("POST"));
        server.createContext("/api/bot/delete", new DeleteBotHandler());
    }

    public void stop() {
        server.stop(0);
        try {
            logWebSocketServer.stop();
        } catch (InterruptedException e) {
            log.error("Failed to stop WebSocket server", e);
        }
        log.info(ConsoleTokens.colorizeText("&eHTTP API Server stopped"));
        log.info(ConsoleTokens.colorizeText("&eLog WebSocket Server stopped"));
    }
}
