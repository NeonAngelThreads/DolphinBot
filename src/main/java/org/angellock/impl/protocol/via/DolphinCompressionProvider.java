/*
 * DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 */
package org.angellock.impl.protocol.via;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.v1_8to1_9.provider.CompressionProvider;
import net.raphimc.netminecraft.constants.MCPipeline;

public class DolphinCompressionProvider extends CompressionProvider {

    @Override
    public void handlePlayCompression(UserConnection user, int threshold) {
        user.getChannel().attr(MCPipeline.COMPRESSION_THRESHOLD_ATTRIBUTE_KEY).set(threshold);
    }
}
