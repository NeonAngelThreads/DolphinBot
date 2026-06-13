/*
 * DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 */
package org.angellock.impl.protocol.via;

import com.viaversion.viabackwards.protocol.v1_20_5to1_20_3.provider.TransferProvider;
import com.viaversion.viaversion.api.connection.UserConnection;

/**
 * Minimal TransferProvider for ViaBackwards. Our proxy doesn't support
 * server transfers (the bot stays connected to one server).
 */
public class DolphinTransferProvider implements TransferProvider {

    @Override
    public void connectToServer(UserConnection user, String host, int port) {
        // Not supported in embedded proxy mode
    }
}
