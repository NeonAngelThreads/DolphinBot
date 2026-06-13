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

import net.raphimc.netminecraft.constants.MCPipeline;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.impl.login.S2CLoginCompressionPacket;
import net.raphimc.netminecraft.packet.impl.play.S2CPlaySetCompressionPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles compression threshold negotiation between client and server.
 *
 * <p>This handler intercepts compression-related packets and ensures both
 * sides of the proxy connection use consistent compression settings.</p>
 *
 * <p>Mirrors ViaProxy's {@code CompressionPacketHandler} logic for managing
 * compression thresholds across protocol versions.</p>
 */
public class DolphinCompressionPacketHandler {

    private static final Logger log = LoggerFactory.getLogger("DolphinProxy");

    /**
     * Process a packet from proxy-to-server direction before forwarding to client.
     * Returns true if the packet should be forwarded, false if consumed.
     */
    public boolean handleP2S(Packet packet, DolphinProxySession session) {
        // Handle play state compression set packet
        if (packet instanceof S2CPlaySetCompressionPacket setCompression) {
            log.info("[COMPRESSION] Server set compression threshold: {}", setCompression.compressionThreshold);
            session.getServerChannel().attr(MCPipeline.COMPRESSION_THRESHOLD_ATTRIBUTE_KEY)
                    .set(setCompression.compressionThreshold);
            return false; // Don't forward - handled by ViaVersion translation
        }

        // Handle login compression packet from server
        if (packet instanceof S2CLoginCompressionPacket loginCompression) {
            log.info("[COMPRESSION] Login compression threshold: {}", loginCompression.compressionThreshold);
            session.getServerChannel().attr(MCPipeline.COMPRESSION_THRESHOLD_ATTRIBUTE_KEY)
                    .set(loginCompression.compressionThreshold);
            // Don't forward to client - we'll send our own compression packet
            // after login success (matching ViaProxy's CompressionPacketHandler)
            return false;
        }

        return true; // Forward all other packets
    }
}
