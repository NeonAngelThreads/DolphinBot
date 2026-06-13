/*
 * DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 */
package org.angellock.impl.protocol.via;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionType;
import com.viaversion.viaversion.protocol.version.BaseVersionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tells ViaVersion which protocol version the client is using and which server
 * version to target. For our embedded proxy, the client version comes from the
 * bot's mcprotocollib, and the server version comes from
 * {@link DolphinProxySession#getServerProtocolVersion()}.
 */
public class DolphinVersionProvider extends BaseVersionProvider {

    private static final Logger log = LoggerFactory.getLogger("DolphinVia");

    @Override
    public ProtocolVersion getClientProtocol(UserConnection connection) {
        ProtocolVersion clientProtocol = connection.getProtocolInfo().protocolVersion();
        log.info("[VersionProvider] getClientProtocol: raw={}, isKnown={}, originalVersion={}",
                clientProtocol, clientProtocol.isKnown(), clientProtocol.getOriginalVersion());
        if (!clientProtocol.isKnown() && ProtocolVersion.isRegistered(VersionType.SPECIAL, clientProtocol.getOriginalVersion())) {
            ProtocolVersion special = ProtocolVersion.getProtocol(VersionType.SPECIAL, clientProtocol.getOriginalVersion());
            log.info("[VersionProvider] getClientProtocol: resolved to special={}", special);
            return special;
        }
        ProtocolVersion result = super.getClientProtocol(connection);
        log.info("[VersionProvider] getClientProtocol: returning={}", result);
        return result;
    }

    @Override
    public ProtocolVersion getClosestServerProtocol(UserConnection connection) throws Exception {
        log.info("[VersionProvider] getClosestServerProtocol: isClientSide={}, channel={}",
                connection.isClientSide(),
                connection.getChannel() != null ? connection.getChannel().id().asShortText() : "null");

        if (connection.isClientSide()) {
            // Server-side channel: return the actual target server version from session
            DolphinProxySession session = DolphinProxySession.fromUserConnection(connection);
            if (session != null) {
                ProtocolVersion serverVersion = session.getServerProtocolVersion();
                log.info("[VersionProvider] getClosestServerProtocol: session found, bot='{}', serverVersion={}, originalVersion={}",
                        session.getBotName(), serverVersion, serverVersion.getOriginalVersion());
                return serverVersion;
            } else {
                log.warn("[VersionProvider] getClosestServerProtocol: session is NULL for clientSide connection!");
            }
        }

        ProtocolVersion fallback = super.getClosestServerProtocol(connection);
        log.info("[VersionProvider] getClosestServerProtocol: fallback={}", fallback);
        return fallback;
    }
}
