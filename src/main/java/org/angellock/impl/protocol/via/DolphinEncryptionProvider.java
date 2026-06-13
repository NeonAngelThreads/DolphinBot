/*
 * DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 */
package org.angellock.impl.protocol.via;

import com.viaversion.viaversion.api.connection.UserConnection;
import net.raphimc.vialegacy.protocol.release.r1_6_4tor1_7_2_5.provider.EncryptionProvider;

/**
 * Minimal encryption provider for ViaLegacy pre-1.7 support.
 * Our proxy doesn't handle legacy encryption; this is a no-op stub.
 */
public class DolphinEncryptionProvider extends EncryptionProvider {

    @Override
    public void enableDecryption(UserConnection user) {
        // No-op – we don't support pre-1.7 encryption in the embedded proxy
    }
}
