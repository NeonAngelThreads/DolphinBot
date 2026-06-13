/*
 * DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 */
package org.angellock.impl.protocol.via;

import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.provider.GameProfileFetcher;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Minimal GameProfileFetcher for ViaLegacy (pre-1.8 support).
 * Returns offline-mode UUIDs since we don't integrate auth services.
 */
public class DolphinGameProfileFetcher extends GameProfileFetcher {

    @Override
    public UUID loadMojangUuid(String playerName) throws ExecutionException, InterruptedException {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    @Override
    public com.viaversion.viaversion.api.minecraft.GameProfile loadGameProfile(UUID uuid) {
        // Return a minimal profile
        return new com.viaversion.viaversion.api.minecraft.GameProfile("unknown", uuid);
    }
}
