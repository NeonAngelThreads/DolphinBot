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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import net.raphimc.netminecraft.constants.ConnectionState;
import net.raphimc.netminecraft.constants.IntendedState;
import net.raphimc.netminecraft.constants.MCPipeline;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.impl.configuration.C2SConfigFinishConfigurationPacket;
import net.raphimc.netminecraft.packet.impl.handshaking.C2SHandshakingClientIntentionPacket;
import net.raphimc.netminecraft.packet.impl.login.C2SLoginAcknowledgedPacket;
import net.raphimc.netminecraft.packet.impl.play.C2SPlayConfigurationAcknowledgedPacket;
import net.raphimc.netminecraft.packet.registry.DefaultPacketRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles decoded Packet objects on the client-to-proxy channel.
 *
 * <p>This handler receives fully decoded Minecraft {@link Packet} objects from mcprotocollib's
 * packet codec. It works with ViaVersion's translation pipeline to ensure proper protocol
 * conversion between client and server versions.</p>
 *
 * <p>Lifecycle:</p>
 * <ol>
 *   <li>Bot connects → {@code channelActive} stores the session</li>
 *   <li>Bot sends handshake ({@link C2SHandshakingClientIntentionPacket}) → parse target info, connect to real server</li>
 *   <li>Subsequent packets (decoded {@link Packet} objects) → forward to server channel for ViaVersion translation</li>
 * </ol>
 *
 * <p>This implementation mirrors ViaProxy's {@code Client2ProxyHandler}, using the same
 * packet-based architecture for full protocol compatibility.</p>
 */
@Sharable
public class DolphinClientHandler extends SimpleChannelInboundHandler<Packet> {

    private static final Logger log = LoggerFactory.getLogger("DolphinProxy");

    private final DolphinProxyServer proxyServer;

    public DolphinClientHandler(DolphinProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.debug("[C2P] Channel active: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        DolphinProxySession session = DolphinProxySession.fromChannel(ctx.channel());
        if (session != null) {
            log.info("[C2P] Client disconnected for bot '{}'. Closing server side.", session.getBotName());
            session.disconnect("client disconnected");
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        DolphinProxySession session = DolphinProxySession.fromChannel(ctx.channel());
        if (session == null || session.isClosed()) {
            return; // Don't release - mcprotocollib handles reference counting
        }

        // ── Handshake phase: intercept Client Intention packet ──
        if (session.getClientConnectionState() == ConnectionState.HANDSHAKING) {
            if (packet instanceof C2SHandshakingClientIntentionPacket) {
                handleHandshake(ctx, session, (C2SHandshakingClientIntentionPacket) packet);
            } else {
                log.warn("[C2P] Unexpected packet {} in HANDSHAKING state", packet.getClass().getSimpleName());
            }
            return;
        }

        // ── Normal forwarding: pass decoded Packet to server channel ──
        Channel serverCh = session.getServerChannel();
        if (serverCh != null && serverCh.isActive()) {
            // Apply packet handlers (e.g. LoginPacketHandler for UUID fix)
            DolphinLoginPacketHandler loginHandler = new DolphinLoginPacketHandler();
            if (!loginHandler.handleC2P(packet, session)) {
                return; // Packet was consumed by handler
            }

            // Handle Configuration state transitions (1.20.2+ clients)
            // Mirrors ViaProxy's ConfigurationPacketHandler exactly:
            //   1. Set c2p state BEFORE writing
            //   2. Write packet to server
            //   3. On write success: set p2s state AND restore auto-read on SERVER channel
            // The server channel's auto-read was disabled in LoginPacketHandler
            // when Login Success was received (for 1.20.2+ clients).
            if (packet instanceof C2SLoginAcknowledgedPacket) {
                // Client acknowledged login → switch to CONFIGURATION state
                session.setClientConnectionState(ConnectionState.CONFIGURATION);
                serverCh.writeAndFlush(packet)
                        .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                        .addListener((ChannelFutureListener) f -> {
                            if (f.isSuccess()) {
                                session.setServerConnectionState(ConnectionState.CONFIGURATION);
                                DolphinChannelUtil.restoreAutoRead(serverCh);
                                log.info("[C2P] Switched to CONFIGURATION state for bot '{}'",
                                        session.getBotName());
                            }
                        });
                return;
            } else if (packet instanceof C2SConfigFinishConfigurationPacket) {
                // Client finished configuration → switch to PLAY state
                session.setClientConnectionState(ConnectionState.PLAY);
                serverCh.writeAndFlush(packet)
                        .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                        .addListener((ChannelFutureListener) f -> {
                            if (f.isSuccess()) {
                                session.setServerConnectionState(ConnectionState.PLAY);
                                DolphinChannelUtil.restoreAutoRead(serverCh);
                                log.info("[C2P] Configuration finished, switched to PLAY state for bot '{}'",
                                        session.getBotName());
                            }
                        });
                return;
            } else if (packet instanceof C2SPlayConfigurationAcknowledgedPacket) {
                // Client acknowledged re-configuration → switch back to CONFIGURATION
                session.setClientConnectionState(ConnectionState.CONFIGURATION);
                serverCh.writeAndFlush(packet)
                        .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                        .addListener((ChannelFutureListener) f -> {
                            if (f.isSuccess()) {
                                session.setServerConnectionState(ConnectionState.CONFIGURATION);
                                DolphinChannelUtil.restoreAutoRead(serverCh);
                                log.info("[C2P] Switched back to CONFIGURATION state for bot '{}'",
                                        session.getBotName());
                            }
                        });
                return;
            }

            // Write the decoded Packet directly — ViaCodec on the server channel handles translation
            serverCh.writeAndFlush(packet)
                    .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            log.warn("[C2P] No active server channel to forward packet {} (bot={})",
                    packet.getClass().getSimpleName(), session.getBotName());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        DolphinProxySession session = DolphinProxySession.fromChannel(ctx.channel());
        String botName = session != null ? session.getBotName() : "unknown";
        log.warn("[C2P] Exception for bot '{}': {}", botName, cause.getMessage(), cause);
        ctx.close();
    }

    /**
     * Intercept the handshake packet, determine the target server, and initiate
     * the backend connection. Then forward a **new** handshake packet to the backend
     * (mirroring ViaProxy's approach in Client2ProxyHandler.java:297).
     *
     * <p><b>Why create a new packet instead of forwarding the original?</b></p>
     * <ol>
     *   <li>ViaProxy uses {@code clientVersion.getOriginalVersion()} to reconstruct
     *       the handshake, ensuring the protocol version field is clean.</li>
     *   <li>The original packet object may have internal state from mcprotocollib's
     *       decoding that conflicts with ViaVersion's translation pipeline.</li>
     *   <li>This avoids "Outdated client" errors caused by corrupted or misinterpreted
     *       protocol version fields during translation.</li>
     * </ol>
     */
    private void handleHandshake(ChannelHandlerContext ctx, DolphinProxySession session,
                                 C2SHandshakingClientIntentionPacket handshake) {

        ProtocolVersion clientVersion = ProtocolVersion.getProtocol(handshake.protocolVersion);

        if (handshake.intendedState == null) {
            log.error("[C2P] Bot '{}' sent null intended state in handshake", session.getBotName());
            ctx.close();
            return;
        }

        ConnectionState nextState = handshake.intendedState.getConnectionState();

        // ── CRITICAL FIX: Update client channel PacketRegistry to client version ──
        // ViaProxy does this in ProxyConnection.setClientVersion():
        //   this.c2p.attr(MCPipeline.PACKET_REGISTRY_ATTRIBUTE_KEY)
        //       .set(new DefaultPacketRegistry(false, clientVersion.getVersion()));
        // This ensures that subsequent packets from mcprotocollib are decoded correctly
        // using the client version's packet ID mapping.
        ctx.channel().attr(MCPipeline.PACKET_REGISTRY_ATTRIBUTE_KEY)
                .set(new DefaultPacketRegistry(false, clientVersion.getVersion()));
        log.info("[C2P] Updated client channel PacketRegistry to client version {} ({})",
                clientVersion.getVersion(), clientVersion.getName());

        // Target is pre-configured when the proxy was started for this bot
        String targetHost = session.getTargetHost();
        int targetPort = session.getTargetPort();
        ProtocolVersion serverVersion = session.getServerProtocolVersion();

        log.info("[C2P] Handshake: bot='{}' client={} (proto={}) -> server={}:{} (version {})",
                session.getBotName(), clientVersion.getName(), handshake.protocolVersion,
                targetHost, targetPort, serverVersion.getName());

        // Disable auto-read on client until we're connected to the backend
        // (mirrors ViaProxy's ChannelUtil.disableAutoRead)
        DolphinChannelUtil.disableAutoRead(ctx.channel());

        // Connect to the target server
        proxyServer.connectToBackend(session, targetHost, targetPort,
                () -> {
                    Channel serverCh = session.getServerChannel();

                    // ── CRITICAL FIX (Plan B): Wait for server channel pipeline to be ready ──
                    // The DolphinServerChannelInitializer.initChannel() may not have completed yet
                    // even though the TCP connection is established. We must wait for ViaCodec
                    // and the protocol pipeline to be fully initialized before sending packets.
                    // This prevents race conditions that cause "Outdated client" errors.
                    if (!session.awaitServerChannelReady(5000)) {
                        log.error("[C2P] Server channel NOT ready after 5s timeout for bot '{}'. Aborting handshake.",
                                session.getBotName());
                        ctx.close();
                        return;
                    }
                    log.info("[C2P] Server channel confirmed ready for bot '{}'", session.getBotName());

                    // ── CRITICAL FIX (Plan A): Create a NEW handshake packet like ViaProxy does ──
                    // ViaProxy (Client2ProxyHandler.java:297) reconstructs the handshake using
                    // clientVersion.getOriginalVersion() instead of forwarding the original packet.
                    // This ensures:
                    // 1. The protocol version field is exactly what we want (original client version)
                    // 2. No internal state from mcprotocollib's decoding leaks into ViaVersion
                    // 3. The packet is clean for translation by the server-side ViaCodec
                    int handshakeProtocolVersion = clientVersion.getOriginalVersion();
                    log.info("[C2P] Using protocol version {} (originalVersion={}) for outgoing handshake",
                            clientVersion.getVersion(), handshakeProtocolVersion);

                    // Construct address with BungeeCord format support (like ViaProxy)
                    String[] handshakeParts = handshake.address.split("\0");
                    handshakeParts[0] = targetHost;  // Replace address with actual target

                    C2SHandshakingClientIntentionPacket newHandshake =
                            new C2SHandshakingClientIntentionPacket(
                                    handshakeProtocolVersion,
                                    String.join("\0", handshakeParts),
                                    targetPort,
                                    handshake.intendedState);

                    log.info("[C2P] Forwarding NEW handshake packet to backend for bot '{}': proto={}, addr={}, port={}, state={}",
                            session.getBotName(),
                            newHandshake.protocolVersion,
                            newHandshake.address,
                            newHandshake.port,
                            newHandshake.intendedState);

                    serverCh.writeAndFlush(newHandshake)
                            .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                            .addListener((ChannelFutureListener) f -> {
                                if (f.isSuccess()) {
                                    session.setClientConnectionState(nextState);
                                    session.setServerConnectionState(nextState);
                                    session.setServerConnected(true);
                                    DolphinChannelUtil.restoreAutoRead(ctx.channel());
                                    log.info("[C2P] Backend connected for bot '{}', handshake forwarded",
                                            session.getBotName());

                                    // Replay buffered packets (e.g. Login Start) that arrived
                                    // while the handshake was being processed
                                    java.util.Queue<Packet> buffered = session.drainPendingPackets();
                                    if (!buffered.isEmpty()) {
                                        log.info("[C2P] Replaying {} buffered packets for bot '{}'",
                                                buffered.size(), session.getBotName());
                                        DolphinLoginPacketHandler loginHandler = new DolphinLoginPacketHandler();
                                        for (Packet p : buffered) {
                                            // Apply packet handlers to buffered packets too
                                            if (loginHandler.handleC2P(p, session)) {
                                                serverCh.writeAndFlush(p)
                                                        .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                                            }
                                        }
                                    }
                                }
                            });
                },
                (cause) -> {
                    log.error("[C2P] Failed to connect to backend {}:{} for bot '{}': {}",
                            targetHost, targetPort, session.getBotName(), cause.getMessage());
                    ctx.close();
                });
    }
}
