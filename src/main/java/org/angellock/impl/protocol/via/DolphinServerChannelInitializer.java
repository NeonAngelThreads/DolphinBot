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
import com.viaversion.viaversion.platform.ViaChannelInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import net.raphimc.netminecraft.constants.MCPipeline;
import net.raphimc.netminecraft.netty.codec.NoReadFlowControlHandler;
import net.raphimc.netminecraft.netty.connection.MinecraftChannelInitializer;
import net.raphimc.netminecraft.packet.registry.DefaultPacketRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes the proxy-to-server channel (the side that connects to the target Minecraft server).
 *
 * <p>Mirrors ViaProxy's {@code Proxy2ServerChannelInitializer} exactly:</p>
 * <ul>
 *   <li>Sets PacketRegistry to CLIENT version (so PacketCodec encodes in client format)</li>
 *   <li>Creates UserConnection via ViaChannelInitializer (no manual protocol version/pipeline setup)</li>
 *   <li>Inserts ViaCodec before PacketCodec for protocol translation</li>
 *   <li>ViaVersion handles pipeline building automatically when the handshake packet is processed</li>
 * </ul>
 *
 * <p><b>CRITICAL:</b> Do NOT manually set protocol versions or build the protocol pipeline here.
 * ViaVersion determines the client version from the handshake packet and the server version
 * from our {@link DolphinVersionProvider}. Setting them manually causes the pipeline to be
 * built twice and packet translation to fail with IndexOutOfBoundsException.</p>
 */
public class DolphinServerChannelInitializer extends MinecraftChannelInitializer {

    private static final Logger log = LoggerFactory.getLogger("DolphinProxy");

    private final DolphinProxySession session;

    public DolphinServerChannelInitializer(ChannelHandler handler, DolphinProxySession session) {
        super(() -> handler);
        this.session = session;
    }

    @Override
    protected void initChannel(Channel channel) {
        super.initChannel(channel);

        // Use the CLIENT protocol version for the PacketRegistry — same as ViaProxy's
        // Proxy2ServerChannelInitializer.java:79 which uses getClientVersion().getVersion().
        // PacketCodec encodes Packet objects in client format, then ViaCodec translates
        // the raw bytes from client format to server format.
        int clientVersion = session.getClientProtocolVersion().getVersion();
        int serverVersion = session.getServerProtocolVersion().getVersion();
        channel.attr(MCPipeline.PACKET_REGISTRY_ATTRIBUTE_KEY).set(new DefaultPacketRegistry(true, clientVersion));
        log.info("[P2S] Set server channel PacketRegistry to client version {} ({})",
                clientVersion, session.getClientProtocolVersion().getName());

        // Create ViaVersion UserConnection — exactly like ViaProxy does.
        // Do NOT set protocol versions or build the pipeline manually!
        // ViaVersion will:
        //   1. Read the client version from the handshake packet
        //   2. Call DolphinVersionProvider.getClosestServerProtocol() to get the server version
        //   3. Build the protocol pipeline automatically
        final UserConnection user = ViaChannelInitializer.createUserConnection(channel, true);
        session.setServerUserConnection(user);
        channel.attr(DolphinProxySession.SESSION_KEY).set(session);

        // Insert ViaVersion codec for server↔client translation
        channel.pipeline().addBefore(MCPipeline.PACKET_CODEC_HANDLER_NAME,
                DolphinViaInjector.CODEC_NAME, new DolphinViaCodec(user));
        channel.pipeline().addAfter(DolphinViaInjector.CODEC_NAME,
                "via-" + MCPipeline.FLOW_CONTROL_HANDLER_NAME, new NoReadFlowControlHandler());

        // Dump pipeline for debugging
        log.info("[P2S] Pipeline for bot '{}': {}", session.getBotName(), channel.pipeline().names());

        // Signal that the server channel pipeline is fully initialized
        session.signalServerChannelReady();
        log.info("[P2S] Server channel fully initialized and ready for bot '{}'", session.getBotName());
    }
}
