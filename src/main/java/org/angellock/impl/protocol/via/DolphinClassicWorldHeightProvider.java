/*
 * DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 */
package org.angellock.impl.protocol.via;

import com.viaversion.viaversion.api.connection.UserConnection;
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.provider.ClassicWorldHeightProvider;

/**
 * Stub classic world height provider.
 */
public class DolphinClassicWorldHeightProvider extends ClassicWorldHeightProvider {

    @Override
    public short getMaxChunkSectionCount(UserConnection user) {
        return 16; // Default for pre-1.17
    }
}
