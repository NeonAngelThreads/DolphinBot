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

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.raphimc.netminecraft.constants.ConnectionState;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.impl.configuration.S2CConfigFinishConfigurationPacket;
import net.raphimc.netminecraft.packet.impl.play.S2CPlayStartConfigurationPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles packets on the proxy-to-server channel with full PacketHandler chain support.
 *
 * <p>This handler processes packets coming from the server and forwards them to the client.
 * It integrates multiple packet handlers to ensure proper protocol translation:</p>
 * <ul>
 *   <li>{@link DolphinLoginPacketHandler} - Manages encryption handshake and login flow</li>
 *   <li>{@link DolphinCompressionPacketHandler} - Negotiates compression thresholds</li>
 *   <li>{@link DolphinDisconnectPacketHandler} - Logs disconnect reasons</li>
 * </ul>
 *
 * <p>Mirrors ViaProxy's {@code Server2ProxyHandler} architecture for comprehensive packet handling.</p>
 */
public class DolphinServerHandler extends SimpleChannelInboundHandler<Packet> {

    private static final Logger log = LoggerFactory.getLogger("DolphinProxy");

    /** Packet handler chain for processing server-to-client packets */
    private final DolphinLoginPacketHandler loginHandler = new DolphinLoginPacketHandler();
    private final DolphinCompressionPacketHandler compressionHandler = new DolphinCompressionPacketHandler();
    private final DolphinDisconnectPacketHandler disconnectHandler = new DolphinDisconnectPacketHandler();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.debug("[P2S] Server channel active: {}", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        DolphinProxySession session = DolphinProxySession.fromChannel(ctx.channel());
        if (session != null) {
            log.info("[P2S] Server disconnected for bot '{}'. Closing client side.", session.getBotName());
            Channel clientCh = session.getClientChannel();
            if (clientCh != null && clientCh.isActive()) {
                clientCh.close();
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        DolphinProxySession session = DolphinProxySession.fromChannel(ctx.channel());
        if (session == null || session.isClosed()) return;

        // Apply packet handler chain in order:
        // 1. Login handler (encryption, login success)
        // 2. Compression handler (threshold negotiation)
        // 3. Disconnect handler (logging)

        if (!loginHandler.handleP2S(packet, session)) {
            return; // Packet consumed by login handler
        }

        if (!compressionHandler.handleP2S(packet, session)) {
            return; // Packet consumed by compression handler
        }

        disconnectHandler.handleP2S(packet, session); // Always logs, returns true

        // Handle Configuration state transitions (1.20.2+ clients) from server side
        // Mirrors ViaProxy's ConfigurationPacketHandler.handleP2S:
        //   When server sends S2CConfigFinishConfiguration or S2CPlayStartConfiguration,
        //   disable auto-read on the SERVER channel to prevent more server packets from
        //   being read before the client acknowledges the state transition.
        //   Auto-read will be restored in DolphinClientHandler when the client sends
        //   C2SConfigFinishConfigurationPacket or C2SPlayConfigurationAcknowledgedPacket.
        Channel serverCh = session.getServerChannel();
        if (packet instanceof S2CConfigFinishConfigurationPacket) {
            if (serverCh != null) {
                DolphinChannelUtil.disableAutoRead(serverCh);
            }
        } else if (packet instanceof S2CPlayStartConfigurationPacket) {
            if (serverCh != null) {
                DolphinChannelUtil.disableAutoRead(serverCh);
            }
        }

        // Forward translated packet from server → client
        Channel clientCh = session.getClientChannel();
        if (clientCh != null && clientCh.isActive()) {
            clientCh.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            log.warn("[P2S] No active client channel to forward packet (bot={})", session.getBotName());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        DolphinProxySession session = DolphinProxySession.fromChannel(ctx.channel());
        String botName = session != null ? session.getBotName() : "unknown";
        log.warn("[P2S] Exception for bot '{}': {}", botName, cause.getMessage(), cause);
        ctx.close();
    }
}
