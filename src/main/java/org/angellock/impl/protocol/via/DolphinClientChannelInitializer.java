/*
 * DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 * Copyright (C) 2025 NeonAngelThreads (https://github.com/NeonAngelThreads)
 *
 *    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 *    License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any
 *    later version.
 *
 *    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 *    implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this
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
 * Initializes the client-to-proxy channel (the side that our bot's mcprotocollib connects to).
 *
 * <p>This initializer uses the full mcprotocollib pipeline (via MinecraftChannelInitializer)
 * so that packets are properly decoded/encoded as Packet objects. ViaVersion's DolphinViaCodec
 * is inserted into the pipeline to handle protocol translation.</p>
 *
 * <p>Architecture (mirrors ViaProxy's Client2ProxyChannelInitializer):</p>
 * <ul>
 *   <li>Inherits from {@code MinecraftChannelInitializer} for complete packet codec support</li>
 *   <li>Configures {@link DefaultPacketRegistry} for proper packet ID mapping</li>
 *   <li>Creates a {@link UserConnection} for ViaVersion state tracking</li>
 *   <li>Inserts {@link DolphinViaCodec} before the packet codec for translation</li>
 *   <li>Adds flow control handler for backpressure management</li>
 * </ul>
 *
 * Pipeline (inbound order):
 *   1. Length codec      – VarInt length prefix framing (from MinecraftChannelInitializer)
 *   2. Compression codec – Packet compression/decompression
 *   3. Encryption codec  – Packet encryption/decryption
 *   4. DolphinViaCodec   – Protocol translation (client ↔ server)
 *   5. Packet codec       – Minecraft packet serialization/deserialization
 *   6. Flow control      – Backpressure management
 *   7. Proxy handler     – Bridges to server channel (DolphinClientHandler)
 */
public class DolphinClientChannelInitializer extends MinecraftChannelInitializer {

    private static final Logger log = LoggerFactory.getLogger("DolphinProxy");

    private final ChannelHandler proxyHandler;

    public DolphinClientChannelInitializer(ChannelHandler handler) {
        super(() -> handler);
        this.proxyHandler = handler;
    }

    @Override
    protected void initChannel(Channel channel) {
        log.info("[C2P] Incoming client connection: {}", channel.remoteAddress());

        // Call parent to set up the base Minecraft pipeline (length, compression, encryption, packet codec)
        super.initChannel(channel);

        // Dequeue the session that was pre-configured by createSession() for the bot
        DolphinProxySession session = DolphinProxyServer.getInstance().pollPendingSession();
        if (session == null) {
            log.warn("[C2P] No pending session for incoming connection, closing channel");
            channel.close();
            return;
        }

        log.info("[C2P] Assigned session '{}' to incoming connection", session.getBotName());

        // Store session on channel for later retrieval
        channel.attr(DolphinProxySession.SESSION_KEY).set(session);
        session.setClientChannel(channel);

        // Configure packet registry for client-side decoding
        // Match ViaProxy's Client2ProxyChannelInitializer which uses DefaultPacketRegistry(false, -1).
        //
        // Why -1 (unknown version)? The client channel talks to mcprotocollib which uses
        // its own internal codec (MinecraftCodec.CODEC) for packet encoding/decoding.
        // The PacketRegistry on this channel is only used for the initial HANDSHAKING state
        // where packet IDs are the same across versions. After the handshake, the connection
        // state is updated and the registry is reconfigured.
        //
        // IMPORTANT: Do NOT use the server version here! Using serverVersion (e.g., 758)
        // would cause mcprotocollib's 774-format packets to be decoded with wrong packet IDs,
        // leading to ReadTimeoutException because the server can't understand the garbled packets.
        channel.attr(MCPipeline.PACKET_REGISTRY_ATTRIBUTE_KEY).set(new DefaultPacketRegistry(false, -1));

        // Create ViaVersion UserConnection for client-side protocol state tracking.
        // IMPORTANT: The client-side ViaCodec does NOT translate packets — it only
        // passes them through. All protocol translation happens on the server-side
        // channel. This matches ViaProxy's Client2ProxyChannelInitializer which
        // creates the UserConnection but never sets protocol versions on it.
        final UserConnection user = ViaChannelInitializer.createUserConnection(channel, false);
        session.setClientUserConnection(user);

        // Do NOT set serverProtocolVersion on the client-side UserConnection!
        // ViaProxy's Client2ProxyChannelInitializer does not set any protocol versions.
        // Setting them would cause the client-side ViaCodec to attempt translation,
        // which corrupts packets. All translation is done on the server-side channel.

        // Mark the client-side UserConnection as INACTIVE so that ViaCodec's
        // shouldTransformPacket() returns false and packets pass through unmodified.
        user.setActive(false);

        log.info("[C2P] Client UserConnection created (pass-through mode, no translation)");

        // Insert ViaVersion codec BEFORE the packet codec so it can translate packets
        // as they flow through the pipeline
        channel.pipeline().addBefore(MCPipeline.PACKET_CODEC_HANDLER_NAME,
                DolphinViaInjector.CODEC_NAME, new DolphinViaCodec(user));

        // Add flow control handler after ViaCodec for backpressure management
        channel.pipeline().addAfter(DolphinViaInjector.CODEC_NAME,
                "via-" + MCPipeline.FLOW_CONTROL_HANDLER_NAME, new NoReadFlowControlHandler());

        log.info("[C2P] Channel initialized with full mcprotocollib + ViaVersion pipeline for bot '{}'",
                session.getBotName());
    }
}
