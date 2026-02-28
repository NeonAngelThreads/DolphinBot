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
import joptsimple.OptionSet;
import net.lenni0451.commons.httpclient.proxy.ProxyType;
import org.angellock.impl.DolphinConfig;
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.Start;
import org.angellock.impl.api.websocket.APIResponseHandler;
import org.angellock.impl.extensions.Plugins;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.plugin.PluginManager;
import org.angellock.impl.util.ProxyObject;
import org.geysermc.mcprotocollib.network.ProxyInfo;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AddedBotHandler extends APIResponseHandler {
    private final String method;
    public AddedBotHandler(String method) {
        this.method = method;
    }
    @Override
    public void handleResponse(HttpExchange exchange) throws IOException {
        Map<String, String> map = this.getResponseBody(exchange);
        String name = map.get("name");
        String server = map.get("server");
        String password = map.get("password");
        int port;
        try {
            port = Integer.parseInt(map.getOrDefault("port","25565"));
        } catch (NumberFormatException e) {
            this.sendResponse(exchange, 400, Map.of("success", false, "message", e.getMessage()));
            return;
        }

        String proxy = map.get("enable");
        String proxyAddress = map.get("proxyIP");
        String proxyPort = map.get("proxyPort");
        String proxyMode = map.get("proxyMode");
        ProxyInfo info = null;

        if(Boolean.parseBoolean(proxy)){
            info = new ProxyInfo(ProxyInfo.Type.valueOf(proxyMode),proxyAddress,Integer.parseInt(proxyPort));
        }
        RobotPlayer bot = (RobotPlayer) new RobotPlayer(new ConfigManager(Start.getGLOBAL_CONFIG()), new PluginManager())
                .withName(name)
                .withPassword(password)
                .withProfileName(name)
                .withDefaultPlugins(List.of(Plugins.BASE_PLUGIN.getPlugin(), Plugins.VERIFY_PLUGIN.getPlugin(), Plugins.QUEUE_PLUGIN.getPlugin()))
                .enableProxy(info)
                .buildProtocol();

        bot.config().setServer(server);
        bot.config().setPort(port);

        BotManager.registerNew(bot.getProfileName(), bot);
        this.sendResponse(exchange, 200, Map.of("success", true));
    }

    @Override
    public boolean isMethodAllowed(HttpExchange exchange) {
        return this.method.equalsIgnoreCase(exchange.getRequestMethod());
    }
}
