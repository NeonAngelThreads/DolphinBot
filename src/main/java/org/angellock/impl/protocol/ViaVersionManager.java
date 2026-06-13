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
 *    You should have received a copy of the GNU General Public License along with this program. If not, see
 *    <https://www.gnu.org/licenses/>.
 *
 *  https://space.bilibili.com/386644641
 */

package org.angellock.impl.protocol;

import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.commands.ViaCommandHandler;
import com.viaversion.viabackwards.ViaBackwardsPlatformImpl;
import com.viaversion.viarewind.ViaRewindPlatformImpl;
import com.viaversion.viaversion.platform.NoopInjector;
import com.viaversion.viaversion.platform.UserConnectionViaVersionPlatform;
import lombok.extern.slf4j.Slf4j;
import net.raphimc.vialegacy.ViaLegacyPlatformImpl;
import org.angellock.impl.protocol.via.DolphinViaInjector;
import org.angellock.impl.protocol.via.DolphinViaPlatformLoader;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.function.Supplier;
import java.util.logging.Level;

@Slf4j
public class ViaVersionManager {
    private static ViaVersionManager instance;
    private final File dataFolder;
    private boolean initialized = false;

    private ViaVersionManager() {
        this.dataFolder = new File("viaversion");
        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdirs();
        }
    }

    public static ViaVersionManager getInstance() {
        if (instance == null) {
            instance = new ViaVersionManager();
        }
        return instance;
    }

    public void initialize() {
        if (initialized) {
            log.warn("ViaVersion is already initialized");
            return;
        }

        log.info("Initializing ViaVersion protocol support...");
        
        try {
            // 创建平台实现
            ViaVersionPlatform platform = new ViaVersionPlatform();

            // 初始化 ViaManager - 平台在 initAndLoad 之后才创建
            Supplier<?>[] platforms = new Supplier[]{
                ViaBackwardsPlatformImpl::new,
                ViaRewindPlatformImpl::new,
                DolphinBotViaLegacyPlatform::new
            };

            ViaManagerImpl.initAndLoad(
                platform,
                new DolphinViaInjector(),
                new ViaCommandHandler(false),
                new DolphinViaPlatformLoader(),
                () -> {
                    for (Supplier<?> p : platforms) {
                        p.get();
                    }
                }
            );
            
            initialized = true;
            log.info("ViaVersion protocol support initialized successfully");
            log.info("Native protocol version: {}", ProtocolVersion.v1_21_11.getName());
            
            int supportedCount = 0;
            for (ProtocolVersion version : ProtocolVersion.getProtocols()) {
                if (version.isKnown()) {
                    supportedCount++;
                }
            }
            log.info("Total known protocol versions: {}", supportedCount);
            
        } catch (Exception e) {
            log.error("Failed to initialize ViaVersion", e);
        }
    }

    public ProtocolVersion getNativeClientProtocol() {
        return ProtocolVersion.v1_21_11;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public void shutdown() {
        if (initialized) {
            try {
                log.info("Shutting down ViaVersion protocol support...");
                initialized = false;
            } catch (Exception e) {
                log.error("Error during ViaVersion shutdown", e);
            }
        }
    }

    // 简单的平台实现
    private static class ViaVersionPlatform extends UserConnectionViaVersionPlatform {
        public ViaVersionPlatform() {
            super(ViaVersionManager.getInstance().getDataFolder());
        }
        
        @Override
        public java.util.logging.Logger createLogger(String name) {
            java.util.logging.Logger logger = java.util.logging.Logger.getLogger(name);
            logger.setLevel(Level.INFO);
            return logger;
        }
        
        @Override
        public String getPlatformName() {
            return "DolphinBot";
        }
        
        @Override
        public String getPlatformVersion() {
            return "1.0.0";
        }
        
        @Override
        public boolean kickPlayer(com.viaversion.viaversion.api.connection.UserConnection connection, String message) {
            log.warn("Kicked player: {}", message);
            return true;
        }
    }
    
    private static class DolphinBotViaLegacyPlatform extends ViaLegacyPlatformImpl {
        @Override
        public String getCpeAppName() {
            return Via.getPlatform().getPlatformName() + " " + Via.getPlatform().getPlatformVersion();
        }
    }
}
