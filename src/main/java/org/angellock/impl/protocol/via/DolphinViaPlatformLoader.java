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

import com.viaversion.viabackwards.protocol.v1_20_5to1_20_3.provider.TransferProvider;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.protocols.v1_8to1_9.provider.CompressionProvider;
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.provider.ClassicCustomCommandProvider;
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.provider.ClassicMPPassProvider;
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.provider.ClassicWorldHeightProvider;
import net.raphimc.vialegacy.protocol.release.r1_2_4_5tor1_3_1_2.provider.OldAuthProvider;
import net.raphimc.vialegacy.protocol.release.r1_6_4tor1_7_2_5.provider.EncryptionProvider;
import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.provider.GameProfileFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers the providers that ViaVersion (and its add-ons) need at startup.
 * Each provider supplies platform-specific behaviour that the protocol translators
 * call into (e.g. compression, version detection, authentication).
 */
public class DolphinViaPlatformLoader implements ViaPlatformLoader {

    private static final Logger log = LoggerFactory.getLogger("DolphinVia");

    @Override
    public void load() {
        // --- ViaVersion core providers ---
        Via.getManager().getProviders().use(VersionProvider.class, new DolphinVersionProvider());
        Via.getManager().getProviders().use(CompressionProvider.class, new DolphinCompressionProvider());

        // --- ViaBackwards ---
        Via.getManager().getProviders().use(TransferProvider.class, new DolphinTransferProvider());

        // --- ViaLegacy (for pre-1.7 support) ---
        try {
            Via.getManager().getProviders().use(GameProfileFetcher.class, new DolphinGameProfileFetcher());
            Via.getManager().getProviders().use(EncryptionProvider.class, new DolphinEncryptionProvider());
            Via.getManager().getProviders().use(OldAuthProvider.class, new DolphinOldAuthProvider());
            Via.getManager().getProviders().use(ClassicWorldHeightProvider.class, new DolphinClassicWorldHeightProvider());
            Via.getManager().getProviders().use(ClassicCustomCommandProvider.class, new DolphinClassicCustomCommandProvider());
            Via.getManager().getProviders().use(ClassicMPPassProvider.class, new DolphinClassicMPPassProvider());
        } catch (NoClassDefFoundError e) {
            log.info("ViaLegacy not on classpath – pre-1.7 translation unavailable");
        }

        log.info("DolphinViaPlatformLoader: all providers registered");
    }

    @Override
    public void unload() {
        // Nothing to clean up
    }
}
