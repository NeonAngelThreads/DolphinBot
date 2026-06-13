package org.angellock.impl.protocol.via;

import net.raphimc.netminecraft.packet.registry.DefaultPacketRegistry;
import net.raphimc.netminecraft.packet.registry.PacketRegistry;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Multi-version PacketCodec factory based on GitHub issue #686 proposal.
 *
 * <p>This class implements the multi-version support strategy discussed in
 * MCProtocolLib issue #686: https://github.com/GeyserMC/MCProtocolLib/issues/686</p>
 *
 * <h3>Core Concept</h3>
 * <p>Instead of using a single fixed {@link MinecraftProtocol} (which is bound to
 * mcprotocollib's built-in version, e.g., 774 for 1.21.11), we create
 * version-specific {@link PacketRegistry} instances that are injected into the
 * Netty channel pipeline at initialization time.</p>
 *
 * <h3>How It Works</h3>
 * <ol>
 *   <li>When connecting to a server, detect its protocol version (e.g., 758 for 1.18.2)</li>
 *   <li>Create a {@link PacketRegistry} for the target server's version</li>
 *   <li>The registry is set as a channel attribute during channel initialization</li>
 *   <li>mcprotocollib's existing {@code PacketCodec} reads this registry from the channel</li>
 *   <li>Packets are now encoded/decoded using the correct version's packet format</li>
 * </ol>
 *
 * @see <a href="https://github.com/GeyserMC/MCProtocolLib/issues/686">GitHub Issue #686</a>
 */
public class MultiVersionPacketCodecFactory {

    private static final Logger log = LoggerFactory.getLogger(MultiVersionPacketCodecFactory.class);

    /**
     * The default protocol version used by the embedded mcprotocollib build.
     * This is mcprotocollib 1.21.11-SNAPSHOT, which uses protocol version 774.
     */
    public static final int DEFAULT_MCPROTOCOLLIB_VERSION = 774;

    /**
     * Create a PacketRegistry configured for a specific target server protocol version.
     *
     * <p>This method implements the core idea from GitHub issue #686:</p>
     * <pre>{@code
     * // Instead of using the default registry (version 774):
     * PacketRegistry registry = new DefaultPacketRegistry(true, 774);
     *
     * // Use the target server's version:
     * PacketRegistry registry = MultiVersionPacketCodecFactory.createRegistry(758);
     * }</pre>
     *
     * @param targetServerVersion The protocol version of the target server (e.g., 758 for 1.18.2)
     * @param isClientSide Whether this is for a client-side connection (true) or server-side (false)
     * @return A PacketRegistry configured for the specified protocol version
     */
    public static PacketRegistry createRegistry(int targetServerVersion, boolean isClientSide) {
        log.info("[MultiVersion] Creating PacketRegistry for protocol version {} (clientSide={})",
                targetServerVersion, isClientSide);

        // Create a PacketRegistry for the target server version
        // Note: DefaultPacketRegistry currently uses the same packet definitions for all versions,
        // but the protocolVersion parameter allows future version-specific optimizations.
        // For now, we rely on ViaVersion to handle actual format differences.
        PacketRegistry registry = new DefaultPacketRegistry(isClientSide, targetServerVersion);

        log.info("[MultiVersion] Created PacketRegistry with protocol version={}", targetServerVersion);
        return registry;
    }

    /**
     * Create a client-side PacketRegistry for connecting to a specific server version.
     *
     * <p>This is the primary method used when creating bot connections.</p>
     *
     * @param targetServerVersion The target server's protocol version
     * @return A client-side PacketRegistry configured for the target version
     */
    public static PacketRegistry createClientRegistry(int targetServerVersion) {
        return createRegistry(targetServerVersion, true);
    }

    /**
     * Create a server-side PacketRegistry for accepting connections from a specific client version.
     *
     * <p>This is used by the proxy's server-side channel initializer.</p>
     *
     * @param clientVersion The client's protocol version
     * @return A server-side PacketRegistry configured for the client version
     */
    public static PacketRegistry createServerRegistry(int clientVersion) {
        return createRegistry(clientVersion, false);
    }

    /**
     * Check if multi-version support is needed for the given protocol versions.
     *
     * @param clientVersion The client's protocol version
     * @param serverVersion The server's protocol version
     * @return true if versions differ and translation is needed
     */
    public static boolean needsTranslation(int clientVersion, int serverVersion) {
        boolean needsTranslation = (clientVersion != serverVersion);
        if (needsTranslation) {
            log.info("[MultiVersion] Translation needed: client={} -> server={}",
                    clientVersion, serverVersion);
        } else {
            log.info("[MultiVersion] No translation needed: both are version {}", clientVersion);
        }
        return needsTranslation;
    }

    /**
     * Get a human-readable name for a protocol version.
     *
     * @param version The protocol version number
     * @return A human-readable version name (e.g., "1.18.2")
     */
    public static String getVersionName(int version) {
        try {
            com.viaversion.viaversion.api.protocol.version.ProtocolVersion pv =
                    com.viaversion.viaversion.api.protocol.version.ProtocolVersion.getProtocol(version);
            return pv.getName();
        } catch (Exception e) {
            return "Unknown (" + version + ")";
        }
    }
}
