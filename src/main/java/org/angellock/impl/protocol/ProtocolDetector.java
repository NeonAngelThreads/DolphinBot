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
 *    License for more details.
 *
 *    You should have received a copy of the GNU General Public License along with this program.  If not, see
 *    <https://www.gnu.org/licenses/>.
 *
 *  https://space.bilibili.com/386644641
 */

package org.angellock.impl.protocol;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import lombok.Getter;
import net.lenni0451.mcping.MCPing;
import net.lenni0451.mcping.responses.MCPingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Detects Minecraft server protocol versions.
 * Uses MCPing to probe servers and determine their supported protocol versions.
 */
public class ProtocolDetector {
    private static final Logger log = LoggerFactory.getLogger(ProtocolDetector.class);
    private static final int TIMEOUT_MS = 3000;

    /**
     * Detect the protocol version of a Minecraft server
     * @param host Server hostname or IP
     * @param port Server port
     * @return Detected ProtocolVersion
     */
    public static ProtocolVersion detectProtocolVersion(String host, int port) {
        log.info("Probing server {}:{} to detect protocol version...", host, port);
        
        try {
            MCPingResponse response = MCPing
                    .pingModern(ProtocolVersion.v1_21_11.getVersion(), true)
                    .tcpSocketFactory(new net.lenni0451.mcping.pings.sockets.impl.factories.SocketChannelSocketFactory())
                    .address(new InetSocketAddress(host, port))
                    .noResolve()
                    .timeout(TIMEOUT_MS, TIMEOUT_MS)
                    .getSync();

            if (response.version != null) {
                int protocolId = response.version.protocol;
                String versionName = response.version.name;
                
                log.info("Server response - Protocol ID: {}, Version Name: {}", protocolId, versionName);
                
                // Try to find a matching protocol version
                ProtocolVersion version = findBestMatch(protocolId, versionName);
                log.info("Detected protocol version: {}", version.getName());
                return version;
            }
            
            log.warn("Server did not provide version information, falling back to native protocol");
            return ProtocolVersion.v1_21_11;
            
        } catch (Exception e) {
            log.error("Failed to detect protocol version for {}:{} - {}", host, port, e.getMessage());
            log.debug("Full stack trace:", e);
            
            log.warn("Falling back to native protocol version");
            return ProtocolVersion.v1_21_11;
        }
    }

    /**
     * Find the best matching ProtocolVersion for a given protocol ID and name
     */
    private static ProtocolVersion findBestMatch(int protocolId, String versionName) {
        // First try exact match by protocol ID
        if (ProtocolVersion.isRegistered(protocolId)) {
            ProtocolVersion version = ProtocolVersion.getProtocol(protocolId);
            if (version.isKnown()) {
                return version;
            }
        }
        
        // Fall back to searching by name
        if (versionName != null && !versionName.isEmpty()) {
            for (ProtocolVersion version : ProtocolVersion.getProtocols()) {
                if (version.isKnown()) {
                    for (String includedVersion : version.getIncludedVersions()) {
                        if (versionName.contains(includedVersion)) {
                            log.debug("Matched version by name: {} -> {}", versionName, version.getName());
                            return version;
                        }
                    }
                }
            }
        }
        
        // Last resort: try to find closest version
        return findClosestVersion(protocolId);
    }

    /**
     * Find the closest known protocol version
     */
    private static ProtocolVersion findClosestVersion(int protocolId) {
        ProtocolVersion closest = ProtocolVersion.v1_21_11;
        int minDiff = Integer.MAX_VALUE;
        
        for (ProtocolVersion version : ProtocolVersion.getProtocols()) {
            if (version.isKnown()) {
                int diff = Math.abs(version.getVersion() - protocolId);
                if (diff < minDiff) {
                    minDiff = diff;
                    closest = version;
                }
            }
        }
        
        log.info("Closest matching protocol: {} (difference: {})", closest.getName(), minDiff);
        return closest;
    }

    /**
     * Get detailed protocol information from a server
     */
    public static ProtocolInfo detectProtocolInfo(String host, int port) {
        try {
            MCPingResponse response = MCPing
                    .pingModern(ProtocolVersion.v1_21_11.getVersion(), true)
                    .tcpSocketFactory(new net.lenni0451.mcping.pings.sockets.impl.factories.SocketChannelSocketFactory())
                    .address(new InetSocketAddress(host, port))
                    .noResolve()
                    .timeout(TIMEOUT_MS, TIMEOUT_MS)
                    .getSync();

            return new ProtocolInfo(
                    response.version != null ? response.version.protocol : ProtocolVersion.v1_21_11.getVersion(),
                    response.version != null ? response.version.name : "Unknown",
                    response.players != null ? response.players.online : 0,
                    response.players != null ? response.players.max : 0,
                    response.description != null ? response.description : "No description"
            );
        } catch (Exception e) {
            log.error("Failed to get protocol info for {}:{}", host, port, e);
            return new ProtocolInfo(
                    ProtocolVersion.v1_21_11.getVersion(),
                    "Unknown",
                    0,
                    0,
                    "Failed to retrieve information"
            );
        }
    }

    /**
     * Container class for protocol information
     */
    @Getter
    public static class ProtocolInfo {
        private final int protocolId;
        private final String versionName;
        private final int onlinePlayers;
        private final int maxPlayers;
        private final String description;

        public ProtocolInfo(int protocolId, String versionName, int onlinePlayers, int maxPlayers, String description) {
            this.protocolId = protocolId;
            this.versionName = versionName;
            this.onlinePlayers = onlinePlayers;
            this.maxPlayers = maxPlayers;
            this.description = description;
        }

        @Override
        public String toString() {
            return String.format("ProtocolInfo{protocolId=%d, versionName='%s', online=%d/%d, description='%s'}",
                    protocolId, versionName, onlinePlayers, maxPlayers, description);
        }
    }
}
