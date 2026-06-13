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
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import lombok.extern.slf4j.Slf4j;
import net.raphimc.netminecraft.constants.ConnectionState;
import net.raphimc.netminecraft.constants.MCPipeline;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.impl.login.C2SLoginHelloPacket;
import net.raphimc.netminecraft.packet.registry.PacketRegistry;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Holds per-connection state for one bot session going through the translation proxy.
 * Each instance binds together the client-facing channel (from mcprotocollib),
 * the server-facing channel (to the target Minecraft server), and the ViaVersion
 * {@link UserConnection} that drives protocol translation.
 */
@Slf4j
public final class DolphinProxySession {

    public static final AttributeKey<DolphinProxySession> SESSION_KEY =
            AttributeKey.valueOf("dolphin_proxy_session");

    private Channel clientChannel;
    private Channel serverChannel;

    private ProtocolVersion clientProtocolVersion = ProtocolVersion.unknown;
    private ProtocolVersion serverProtocolVersion = ProtocolVersion.unknown;

    private ConnectionState clientConnectionState = ConnectionState.HANDSHAKING;
    private ConnectionState serverConnectionState = ConnectionState.HANDSHAKING;

    private String targetHost;
    private int targetPort;
    private String botName;

    /** Whether we have successfully connected to the backend server */
    private volatile boolean serverConnected;

    private UserConnection clientUserConnection;
    private UserConnection serverUserConnection;

    /** Login Hello packet sent by the client (used for encryption handshake) */
    private C2SLoginHelloPacket loginHelloPacket;

    /** Buffer for packets that arrive before the handshake is complete. */
    private final Queue<Packet> pendingPackets = new ConcurrentLinkedQueue<>();

    /**
     * Custom PacketRegistry for the target server's protocol version.
     * When set, this registry will be used instead of the default one
     * during channel initialization to enable multi-version support.
     *
     * @see MultiVersionPacketCodecFactory
     */
    private PacketRegistry serverPacketRegistry;

    /**
     * Latch to ensure the server channel's pipeline (including ViaCodec) is fully
     * initialized before we send the handshake packet. This prevents race conditions
     * where the handshake is sent before ViaVersion's protocol pipeline is ready.
     *
     * <p>Countdown happens in {@link DolphinServerChannelInitializer#initChannel(Channel)}
     * after all handlers (including DolphinViaCodec) are added to the pipeline.</p>
     *
     * <p>Wait happens in {@link DolphinClientHandler#handleHandshake} before writing
     * the handshake packet to the server channel.</p>
     */
    private final java.util.concurrent.CountDownLatch serverChannelReadyLatch =
            new java.util.concurrent.CountDownLatch(1);

    /**
     * Signal that the server channel's pipeline initialization is complete.
     * Called by {@link DolphinServerChannelInitializer} at the end of initChannel().
     */
    public void signalServerChannelReady() {
        this.serverChannelReadyLatch.countDown();
        log.info("[Session] Server channel pipeline ready for bot '{}'", this.botName);
    }

    /**
     * Wait for the server channel's pipeline to be fully initialized (up to timeout).
     * @return true if ready within timeout, false if timed out
     */
    public boolean awaitServerChannelReady(long timeoutMs) {
        try {
            boolean ready = this.serverChannelReadyLatch.await(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
            if (!ready) {
                log.warn("[Session] Timed out waiting for server channel ready (bot='{}', timeout={}ms)",
                        this.botName, timeoutMs);
            }
            return ready;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[Session] Interrupted waiting for server channel ready (bot='{}')", this.botName);
            return false;
        }
    }

    public DolphinProxySession() {
    }

    // ──────────────── accessors ────────────────

    public static DolphinProxySession fromUserConnection(UserConnection user) {
        if (user == null || user.getChannel() == null) return null;
        return user.getChannel().attr(SESSION_KEY).get();
    }

    public static DolphinProxySession fromChannel(Channel ch) {
        return ch == null ? null : ch.attr(SESSION_KEY).get();
    }

    public Channel getClientChannel() { return clientChannel; }
    public void setClientChannel(Channel c) { this.clientChannel = c; c.attr(SESSION_KEY).set(this); }

    public Channel getServerChannel() { return serverChannel; }
    public void setServerChannel(Channel c) { this.serverChannel = c; c.attr(SESSION_KEY).set(this); }

    public ProtocolVersion getClientProtocolVersion() { return clientProtocolVersion; }
    public void setClientProtocolVersion(ProtocolVersion v) { this.clientProtocolVersion = v; }

    public ProtocolVersion getServerProtocolVersion() { return serverProtocolVersion; }
    public void setServerProtocolVersion(ProtocolVersion v) { this.serverProtocolVersion = v; }

    public ConnectionState getClientConnectionState() { return clientConnectionState; }
    public void setClientConnectionState(ConnectionState s) {
        this.clientConnectionState = s;
        if (clientChannel != null) {
            PacketRegistry reg = clientChannel.attr(MCPipeline.PACKET_REGISTRY_ATTRIBUTE_KEY).get();
            if (reg != null) reg.setConnectionState(s);
        }
        syncViaState(s);
    }

    public ConnectionState getServerConnectionState() { return serverConnectionState; }
    public void setServerConnectionState(ConnectionState s) {
        this.serverConnectionState = s;
        if (serverChannel != null) {
            PacketRegistry reg = serverChannel.attr(MCPipeline.PACKET_REGISTRY_ATTRIBUTE_KEY).get();
            if (reg != null) reg.setConnectionState(s);
        }
        syncViaState(s);
    }

    /**
     * Sync the ViaVersion protocol state on both client and server UserConnections.
     *
     * <p>Only updates the PacketRegistry's connection state (for mcprotocollib packet ID mapping).
     * ViaVersion's internal state is managed automatically by the ViaCodec when processing
     * the handshake packet — we do NOT manually set ProtocolInfo states here, as that
     * conflicts with ViaVersion's internal pipeline setup.</p>
     */
    public void syncViaState(ConnectionState s) {
        // Only update PacketRegistry connection states for mcprotocollib
        // ViaVersion manages its own ProtocolInfo states internally
    }

    /**
     * Sync the server connection state to the ViaVersion pipeline.
     * Uses the current serverConnectionState.
     */
    public void syncViaState() {
        syncViaState(this.serverConnectionState);
    }

    private static State convertToViaState(ConnectionState s) {
        try {
            return State.valueOf(s.name());
        } catch (IllegalArgumentException e) {
            log.warn("[State] Unknown connection state: {}", s);
            return null;
        }
    }

    public String getTargetHost() { return targetHost; }
    public void setTargetHost(String h) { this.targetHost = h; }

    public int getTargetPort() { return targetPort; }
    public void setTargetPort(int p) { this.targetPort = p; }

    public String getBotName() { return botName; }
    public void setBotName(String n) { this.botName = n; }

    /**
     * Get the custom PacketRegistry for the target server's protocol version.
     * @return The server-version PacketRegistry, or null if not set
     */
    public PacketRegistry getServerPacketRegistry() { return serverPacketRegistry; }

    /**
     * Set the custom PacketRegistry for multi-version support.
     * @param registry The PacketRegistry configured for the target server version
     */
    public void setServerPacketRegistry(PacketRegistry registry) {
        this.serverPacketRegistry = registry;
        log.info("[Session] Set server PacketRegistry for bot '{}' (version={})",
                this.botName, registry != null ? registry.getProtocolVersion() : "null");
    }

    public boolean isServerConnected() { return serverConnected; }
    public void setServerConnected(boolean b) { this.serverConnected = b; }

    public UserConnection getUserConnection() { return clientUserConnection; }
    public void setClientUserConnection(UserConnection u) { this.clientUserConnection = u; }
    public UserConnection getServerUserConnection() { return serverUserConnection; }
    public void setServerUserConnection(UserConnection u) { this.serverUserConnection = u; }

    public C2SLoginHelloPacket getLoginHelloPacket() { return loginHelloPacket; }
    public void setLoginHelloPacket(C2SLoginHelloPacket p) { this.loginHelloPacket = p; }

    /** Queue a packet for later replay (during handshake phase). */
    public void queuePendingPacket(Packet packet) {
        pendingPackets.offer(packet);
    }

    /** Drain all buffered pending packets. */
    public Queue<Packet> drainPendingPackets() {
        Queue<Packet> drained = new ConcurrentLinkedQueue<>(pendingPackets);
        pendingPackets.clear();
        return drained;
    }

    public boolean isClosed() {
        return (clientChannel != null && !clientChannel.isActive())
                || (serverChannel != null && !serverChannel.isActive());
    }

    /**
     * Close both channels cleanly.
     */
    public void disconnect(String reason) {
        try {
            if (serverChannel != null && serverChannel.isActive()) {
                serverChannel.close();
            }
        } catch (Exception ignored) {}
        try {
            if (clientChannel != null && clientChannel.isActive()) {
                clientChannel.close();
            }
        } catch (Exception ignored) {}
    }
}
