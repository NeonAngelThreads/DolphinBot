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
 */
package org.angellock.impl.protocol.via;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.platform.UserConnectionViaVersionPlatform;
import io.netty.channel.ChannelFutureListener;
import net.raphimc.netminecraft.packet.impl.configuration.C2SConfigCustomPayloadPacket;
import net.raphimc.netminecraft.packet.impl.configuration.S2CConfigCustomPayloadPacket;
import net.raphimc.netminecraft.packet.impl.play.C2SPlayCustomPayloadPacket;
import net.raphimc.netminecraft.packet.impl.play.S2CPlayCustomPayloadPacket;
import net.raphimc.netminecraft.constants.ConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;

/**
 * ViaVersion platform implementation for DolphinBot's embedded proxy.
 * Provides the minimal hooks that ViaVersion needs: logger creation,
 * player kick, custom payload sending, and platform identification.
 */
public class DolphinViaPlatform extends UserConnectionViaVersionPlatform {

    private static final Logger log = LoggerFactory.getLogger("DolphinVia");

    public DolphinViaPlatform() {
        super(new java.io.File(System.getProperty("user.dir")));
    }

    @Override
    public java.util.logging.Logger createLogger(String name) {
        return new JavaUtilToSlf4jAdapter(LoggerFactory.getLogger(name));
    }

    @Override
    public String getPlatformName() {
        return "DolphinBot";
    }

    @Override
    public String getPlatformVersion() {
        return "1.5.0";
    }

    @Override
    public boolean kickPlayer(UserConnection connection, String message) {
        try {
            io.netty.channel.Channel ch = connection.getChannel();
            if (ch != null && ch.isActive()) {
                ch.writeAndFlush(null).addListener(ChannelFutureListener.CLOSE);
            }
        } catch (Exception ignored) {
        }
        return true;
    }

    @Override
    public void sendCustomPayload(UserConnection connection, String channel, byte[] message) {
        try {
            DolphinProxySession session = DolphinProxySession.fromUserConnection(connection);
            if (session == null || !session.getServerChannel().isActive()) return;
            ConnectionState state = session.getServerConnectionState();
            Object packet = switch (state) {
                case CONFIGURATION -> new C2SConfigCustomPayloadPacket(channel, message);
                case PLAY -> new C2SPlayCustomPayloadPacket(channel, message);
                default -> null;
            };
            if (packet != null) {
                session.getServerChannel().writeAndFlush(packet)
                        .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
        } catch (Exception e) {
            log.warn("Failed to send custom payload to server", e);
        }
    }

    @Override
    public void sendCustomPayloadToClient(UserConnection connection, String channel, byte[] message) {
        try {
            DolphinProxySession session = DolphinProxySession.fromUserConnection(connection);
            if (session == null || !session.getClientChannel().isActive()) return;
            ConnectionState state = session.getClientConnectionState();
            Object packet = switch (state) {
                case CONFIGURATION -> new S2CConfigCustomPayloadPacket(channel, message);
                case PLAY -> new S2CPlayCustomPayloadPacket(channel, message);
                default -> null;
            };
            if (packet != null) {
                session.getClientChannel().writeAndFlush(packet)
                        .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
        } catch (Exception e) {
            log.warn("Failed to send custom payload to client", e);
        }
    }

    @Override
    public JsonObject getDump() {
        JsonObject root = new JsonObject();
        root.addProperty("platform", "DolphinBot");
        root.addProperty("version", "1.5.0");
        return root;
    }

    /**
     * Minimal java.util.Logger -> SLF4J bridge so ViaVersion's internal logging
     * routes through our SLF4J/logback stack.
     */
    private static class JavaUtilToSlf4jAdapter extends java.util.logging.Logger {
        private final Logger slf4j;

        protected JavaUtilToSlf4jAdapter(Logger slf4j) {
            super("", null);
            this.slf4j = slf4j;
        }

        @Override
        public void log(Level level, String msg) {
            if (level.intValue() >= Level.SEVERE.intValue()) slf4j.error(msg);
            else if (level.intValue() >= Level.WARNING.intValue()) slf4j.warn(msg);
            else if (level.intValue() >= Level.INFO.intValue()) slf4j.info(msg);
            else slf4j.debug(msg);
        }

        @Override
        public void log(Level level, String msg, Throwable thrown) {
            if (level.intValue() >= Level.SEVERE.intValue()) slf4j.error(msg, thrown);
            else if (level.intValue() >= Level.WARNING.intValue()) slf4j.warn(msg, thrown);
            else if (level.intValue() >= Level.INFO.intValue()) slf4j.info(msg, thrown);
            else slf4j.debug(msg, thrown);
        }
    }
}
