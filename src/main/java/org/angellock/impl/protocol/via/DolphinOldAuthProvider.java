/*
 * DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 */
package org.angellock.impl.protocol.via;

import com.viaversion.viaversion.api.connection.UserConnection;
import net.raphimc.vialegacy.protocol.release.r1_2_4_5tor1_3_1_2.provider.OldAuthProvider;

/**
 * Stub for old auth provider (pre-1.3). Not used in modern server connections.
 */
public class DolphinOldAuthProvider extends OldAuthProvider {

    @Override
    public void sendAuthRequest(UserConnection user, String serverId) throws Throwable {
        // No-op
    }
}
