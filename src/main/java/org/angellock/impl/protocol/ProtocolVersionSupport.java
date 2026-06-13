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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for Minecraft protocol version management.
 * This class provides information about supported protocol versions and their mappings.
 */
public class ProtocolVersionSupport {
    private static final Logger log = LoggerFactory.getLogger(ProtocolVersionSupport.class);
    
    // Native protocol version supported by the bot
    public static final ProtocolVersion NATIVE_PROTOCOL = ProtocolVersion.v1_21_11;
    
    // Map of protocol versions to friendly names
    private static final Map<Integer, String> VERSION_NAMES = new HashMap<>();
    
    static {
        // Modern versions
//        VERSION_NAMES.put(ProtocolVersion.v1_21_3.getVersion(), "1.21.3");
        VERSION_NAMES.put(ProtocolVersion.v1_21_2.getVersion(), "1.21.2");
//        VERSION_NAMES.put(ProtocolVersion.v1_21_1.getVersion(), "1.21.1");
        VERSION_NAMES.put(ProtocolVersion.v1_21.getVersion(), "1.21");
//        VERSION_NAMES.put(ProtocolVersion.v1_20_6.getVersion(), "1.20.6");
        VERSION_NAMES.put(ProtocolVersion.v1_20_5.getVersion(), "1.20.5");
//        VERSION_NAMES.put(ProtocolVersion.v1_20_4.getVersion(), "1.20.4");
        VERSION_NAMES.put(ProtocolVersion.v1_20_3.getVersion(), "1.20.3");
        VERSION_NAMES.put(ProtocolVersion.v1_20_2.getVersion(), "1.20.2");
//        VERSION_NAMES.put(ProtocolVersion.v1_20_1.getVersion(), "1.20.1");
        VERSION_NAMES.put(ProtocolVersion.v1_20.getVersion(), "1.20");
        VERSION_NAMES.put(ProtocolVersion.v1_19_4.getVersion(), "1.19.4");
        VERSION_NAMES.put(ProtocolVersion.v1_19_3.getVersion(), "1.19.3");
        VERSION_NAMES.put(ProtocolVersion.v1_19_1.getVersion(), "1.19.1");
        VERSION_NAMES.put(ProtocolVersion.v1_19.getVersion(), "1.19");
        VERSION_NAMES.put(ProtocolVersion.v1_18_2.getVersion(), "1.18.2");
//        VERSION_NAMES.put(ProtocolVersion.v1_18_1.getVersion(), "1.18.1");
        VERSION_NAMES.put(ProtocolVersion.v1_18.getVersion(), "1.18");
        VERSION_NAMES.put(ProtocolVersion.v1_17_1.getVersion(), "1.17.1");
        VERSION_NAMES.put(ProtocolVersion.v1_17.getVersion(), "1.17");
        
        // Older versions
        VERSION_NAMES.put(ProtocolVersion.v1_16_4.getVersion(), "1.16.4-1.16.5");
        VERSION_NAMES.put(ProtocolVersion.v1_16_3.getVersion(), "1.16.3");
        VERSION_NAMES.put(ProtocolVersion.v1_16_2.getVersion(), "1.16.2");
        VERSION_NAMES.put(ProtocolVersion.v1_16_1.getVersion(), "1.16.1");
        VERSION_NAMES.put(ProtocolVersion.v1_16.getVersion(), "1.16");
        VERSION_NAMES.put(ProtocolVersion.v1_15_2.getVersion(), "1.15.2");
        VERSION_NAMES.put(ProtocolVersion.v1_15_1.getVersion(), "1.15.1");
        VERSION_NAMES.put(ProtocolVersion.v1_15.getVersion(), "1.15");
        VERSION_NAMES.put(ProtocolVersion.v1_14_4.getVersion(), "1.14.4");
        VERSION_NAMES.put(ProtocolVersion.v1_14_3.getVersion(), "1.14.3");
        VERSION_NAMES.put(ProtocolVersion.v1_14_2.getVersion(), "1.14.2");
        VERSION_NAMES.put(ProtocolVersion.v1_14_1.getVersion(), "1.14.1");
        VERSION_NAMES.put(ProtocolVersion.v1_14.getVersion(), "1.14");
        
        // Classic versions
        VERSION_NAMES.put(ProtocolVersion.v1_13_2.getVersion(), "1.13.2");
        VERSION_NAMES.put(ProtocolVersion.v1_13_1.getVersion(), "1.13.1");
        VERSION_NAMES.put(ProtocolVersion.v1_13.getVersion(), "1.13");
        VERSION_NAMES.put(ProtocolVersion.v1_12_2.getVersion(), "1.12.2");
        VERSION_NAMES.put(ProtocolVersion.v1_12_1.getVersion(), "1.12.1");
        VERSION_NAMES.put(ProtocolVersion.v1_12.getVersion(), "1.12");
        VERSION_NAMES.put(ProtocolVersion.v1_11_1.getVersion(), "1.11.1-1.11.2");
        VERSION_NAMES.put(ProtocolVersion.v1_11.getVersion(), "1.11");
        VERSION_NAMES.put(ProtocolVersion.v1_10.getVersion(), "1.10.x");
//        VERSION_NAMES.put(ProtocolVersion.v1_9_4.getVersion(), "1.9.3-1.9.4");
        VERSION_NAMES.put(ProtocolVersion.v1_9_2.getVersion(), "1.9.2");
        VERSION_NAMES.put(ProtocolVersion.v1_9_1.getVersion(), "1.9.1");
        VERSION_NAMES.put(ProtocolVersion.v1_9.getVersion(), "1.9");
        VERSION_NAMES.put(ProtocolVersion.v1_8.getVersion(), "1.8.x");
        VERSION_NAMES.put(ProtocolVersion.v1_7_6.getVersion(), "1.7.6-1.7.10");
        VERSION_NAMES.put(ProtocolVersion.v1_7_2.getVersion(), "1.7.2-1.7.5");
    }
    
    /**
     * Check if a protocol version is supported
     */
    public static boolean isSupported(int protocolVersion) {
        return VERSION_NAMES.containsKey(protocolVersion) || 
               ProtocolVersion.isRegistered(protocolVersion);
    }
    
    /**
     * Get a friendly name for a protocol version
     */
    public static String getVersionName(int protocolVersion) {
        if (VERSION_NAMES.containsKey(protocolVersion)) {
            return VERSION_NAMES.get(protocolVersion);
        }
        
        try {
            ProtocolVersion version = ProtocolVersion.getProtocol(protocolVersion);
            if (version.isKnown()) {
                return version.getName();
            }
        } catch (Exception e) {
            log.debug("Could not get protocol name for version {}", protocolVersion);
        }
        
        return "Unknown (protocol " + protocolVersion + ")";
    }
    
    /**
     * Check if a protocol version is newer than the native version
     */
    public static boolean isNewerThanNative(int protocolVersion) {
        return protocolVersion > NATIVE_PROTOCOL.getVersion();
    }
    
    /**
     * Check if a protocol version is older than the native version
     */
    public static boolean isOlderThanNative(int protocolVersion) {
        return protocolVersion < NATIVE_PROTOCOL.getVersion();
    }
    
    /**
     * Get the best protocol version to use for a server
     */
    public static ProtocolVersion getBestProtocol(ProtocolVersion serverVersion) {
        if (serverVersion.getVersion() == NATIVE_PROTOCOL.getVersion()) {
            return NATIVE_PROTOCOL;
        }
        
        // If ViaVersion is available, it can handle the translation
        if (ViaVersionManager.getInstance().isInitialized()) {
            return serverVersion;
        }
        
        // Fall back to native protocol
        log.warn("ViaVersion not available, falling back to native protocol: {}", 
                NATIVE_PROTOCOL.getName());
        return NATIVE_PROTOCOL;
    }
}
