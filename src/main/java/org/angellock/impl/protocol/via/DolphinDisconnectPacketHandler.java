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

import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.impl.common.S2CDisconnectPacket;
import net.raphimc.netminecraft.packet.impl.login.S2CLoginDisconnectPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles disconnect packets from the server and logs the reason.
 *
 * <p>This handler intercepts both login and play state disconnect packets,
 * extracts the disconnect reason, and logs it for debugging purposes.</p>
 *
 * <p>Mirrors ViaProxy's {@code DisconnectPacketHandler} for consistent error handling.</p>
 */
public class DolphinDisconnectPacketHandler {

    private static final Logger log = LoggerFactory.getLogger("DolphinProxy");

    /**
     * Process a packet from proxy-to-server direction before forwarding to client.
     * Logs disconnect reasons and returns true to allow forwarding.
     */
    public boolean handleP2S(Packet packet, DolphinProxySession session) {
        if (packet instanceof S2CLoginDisconnectPacket loginDisconnect) {
            String reason = extractReason(loginDisconnect.reason);
            log.info("[DISCONNECT] Server disconnected bot '{}' during login: {}",
                    session.getBotName(), reason);
        } else if (packet instanceof S2CDisconnectPacket disconnect) {
            String reason = extractReason(disconnect.reason);
            log.info("[DISCONNECT] Server disconnected bot '{}': {}",
                    session.getBotName(), reason);
        }

        return true; // Always forward disconnect packets
    }

    /**
     * Extract readable reason text from a component object.
     */
    private String extractReason(Object reasonComponent) {
        if (reasonComponent == null) {
            return "Unknown reason";
        }
        try {
            // Try to convert component to string
            return reasonComponent.toString();
        } catch (Exception e) {
            return "Unable to parse reason: " + e.getMessage();
        }
    }
}
