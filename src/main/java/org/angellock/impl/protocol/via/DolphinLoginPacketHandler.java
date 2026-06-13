/*
 * DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 * Copyright (C) 2025 NeonAngelThreads (https://github.com/NeonAngelThreads)
 *
 *    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 *    License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any
 *    later version.
 *
 *    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 *    implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this
 *    program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.angellock.impl.protocol.via;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.ChannelFutureListener;
import net.raphimc.netminecraft.constants.ConnectionState;
import net.raphimc.netminecraft.constants.MCPipeline;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.netty.crypto.AESEncryption;
import net.raphimc.netminecraft.netty.crypto.CryptUtil;
import net.raphimc.netminecraft.packet.impl.login.C2SLoginHelloPacket;
import net.raphimc.netminecraft.packet.impl.login.C2SLoginKeyPacket;
import net.raphimc.netminecraft.packet.impl.login.S2CLoginGameProfilePacket;
import net.raphimc.netminecraft.packet.impl.login.S2CLoginHelloPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.UUID;

/**
 * Comprehensive login packet handler that manages the complete authentication flow.
 *
 * <p>This handler handles both client-to-proxy (C2P) and proxy-to-server (P2S) directions:</p>
 * <ul>
 *   <li><b>C2P:</b> Intercepts Login Hello, fixes null UUIDs</li>
 *   <li><b>P2S:</b> Handles encryption requests (S2CLoginHello), login success (S2CLoginGameProfile)</li>
 * </ul>
 *
 * <p>Mirrors ViaProxy's {@code LoginPacketHandler} for complete login flow support,
 * including offline-mode UUID generation and encryption negotiation.</p>
 */
public class DolphinLoginPacketHandler {

    private static final Logger log = LoggerFactory.getLogger("DolphinProxy");

    /** Key pair for encryption handshake */
    private static final KeyPair KEY_PAIR = CryptUtil.generateKeyPair();

    private LoginState loginState = LoginState.FIRST_PACKET;

    /**
     * Process a packet from client-to-proxy direction before forwarding to server.
     * Returns true if packet should be forwarded, false if consumed.
     */
    public boolean handleC2P(Packet packet, DolphinProxySession session) {
        if (packet instanceof C2SLoginHelloPacket loginHello) {
            return handleClientLoginHello(loginHello, session);
        }

        return true; // Forward all other packets
    }

    /**
     * Process a packet from proxy-to-server direction before forwarding to client.
     * Returns true if packet should be forwarded, false if consumed.
     */
    public boolean handleP2S(Packet packet, DolphinProxySession session) {
        if (packet instanceof S2CLoginHelloPacket loginHelloPacket) {
            return handleServerEncryptionRequest(loginHelloPacket, session);
        } else if (packet instanceof S2CLoginGameProfilePacket gameProfilePacket) {
            return handleLoginSuccess(gameProfilePacket, session);
        }

        return true; // Forward all other packets
    }

    // ── Client-to-Proxy handlers ──

    private boolean handleClientLoginHello(C2SLoginHelloPacket loginHello, DolphinProxySession session) {
        log.info("[LOGIN] Processing Login Hello for bot '{}': name={}, uuid={}",
                session.getBotName(), loginHello.name, loginHello.uuid);

        if (this.loginState != LoginState.FIRST_PACKET) {
            log.warn("[LOGIN] Unexpected Login Hello packet (state={})", this.loginState);
            return false; // Reject duplicate login attempts
        }
        this.loginState = LoginState.SENT_HELLO;

        // Store the login hello for later use (e.g., encryption)
        session.setLoginHelloPacket(loginHello);

        // Fix null UUID by generating offline-mode UUID (same algorithm as Minecraft server)
        if (loginHello.uuid == null) {
            UUID offlineUuid = generateOfflineUuid(loginHello.name);
            log.info("[LOGIN] Generated offline UUID {} for bot '{}'",
                    offlineUuid, session.getBotName());
            loginHello.uuid = offlineUuid;
        }

        return true; // Forward the modified packet to server
    }

    // ── Proxy-to-Server handlers ──

    private boolean handleServerEncryptionRequest(S2CLoginHelloPacket encryptionRequest, DolphinProxySession session) {
        log.info("[LOGIN] Received encryption request from server for bot '{}'", session.getBotName());

        if (this.loginState != LoginState.SENT_HELLO) {
            log.warn("[LOGIN] Unexpected encryption request (state={})", this.loginState);
            return false;
        }
        this.loginState = LoginState.RECEIVED_ENCRYPTION;

        try {
            // Decode server's public key
            PublicKey serverPublicKey = CryptUtil.decodeRsaPublicKey(encryptionRequest.publicKey);

            // Generate shared secret for encryption
            SecretKey secretKey = CryptUtil.generateSecretKey();

            // Encrypt the secret with server's public key
            byte[] encryptedSecretKey = CryptUtil.encryptData(serverPublicKey, secretKey.getEncoded());

            // Encrypt verify token
            byte[] encryptedToken = CryptUtil.encryptData(serverPublicKey, encryptionRequest.nonce);

            // Create login key packet with encrypted credentials
            C2SLoginKeyPacket loginKey = new C2SLoginKeyPacket(encryptedSecretKey, encryptedToken);

            // Send the login key to server
            session.getServerChannel().writeAndFlush(loginKey)
                    .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);

            // Enable encryption on server channel (for 1.7.2+)
            if (session.getServerProtocolVersion().newerThanOrEqualTo(ProtocolVersion.v1_7_2)) {
                session.getServerChannel().attr(MCPipeline.ENCRYPTION_ATTRIBUTE_KEY)
                        .set(new AESEncryption(secretKey));
                log.info("[LOGIN] Encryption enabled on server channel for bot '{}'",
                        session.getBotName());
            }

            // For offline mode servers, we also need to forward the encryption request to client
            // so they can encrypt their side too (if the client expects encryption)
            if (shouldForwardEncryptionToClient(session)) {
                session.getClientChannel().writeAndFlush(encryptionRequest)
                        .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                log.info("[LOGIN] Forwarded encryption request to client for bot '{}'",
                        session.getBotName());
            }

        } catch (Exception e) {
            log.error("[LOGIN] Failed to handle encryption request for bot '{}': {}",
                    session.getBotName(), e.getMessage(), e);
            session.disconnect("Encryption failed: " + e.getMessage());
        }

        return false; // Don't forward original packet - we handled it
    }

    private boolean handleLoginSuccess(S2CLoginGameProfilePacket gameProfile, DolphinProxySession session) {
        log.info("[LOGIN] Login success for bot '{}': uuid={}, name={}",
                session.getBotName(), gameProfile.uuid, gameProfile.name);

        this.loginState = LoginState.COMPLETED;

        // Determine next state based on client version
        // 1.20.2+ clients go through CONFIGURATION state before PLAY
        ConnectionState nextState = session.getClientProtocolVersion()
                .newerThanOrEqualTo(ProtocolVersion.v1_20_2)
                ? ConnectionState.CONFIGURATION : ConnectionState.PLAY;

        log.info("[LOGIN] Login success for bot '{}', next state: {}", session.getBotName(), nextState);

        // Mirrors ViaProxy's LoginPacketHandler.handleP2S for S2CLoginGameProfilePacket:
        // Always disable auto-read on the server channel to prevent PLAY-state packets
        // from arriving before the state transition is complete.
        // For pre-1.20.2 clients: restore auto-read immediately after the packet
        // is written to the client (since there's no CONFIGURATION state).
        // For 1.20.2+ clients: auto-read stays disabled until the client sends
        // C2SLoginAcknowledgedPacket (handled in DolphinClientHandler).
        DolphinChannelUtil.disableAutoRead(session.getServerChannel());

        if (nextState != ConnectionState.CONFIGURATION) {
            // For pre-1.20.2 clients, switch directly to PLAY
            // Auto-read will be restored after the packet is forwarded to the client
            // (we can't add a listener here since we return true to forward the packet)
            session.setClientConnectionState(nextState);
            session.setServerConnectionState(nextState);
            session.syncViaState();
            // Restore auto-read since there's no CONFIGURATION phase
            DolphinChannelUtil.restoreAutoRead(session.getServerChannel());
        }

        return true; // Forward login success to client
    }

    // ── Helper methods ──

    /**
     * Generate an offline-mode UUID from a player name (Minecraft's standard algorithm).
     */
    private static UUID generateOfflineUuid(String name) {
        return UUID.nameUUIDFromBytes(
                ("OfflinePlayer:" + name).getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    /**
     * Determine if we should forward encryption request to the client.
     *
     * <p>In most cases for a bot, we handle encryption ourselves and don't need to
     * forward to the client unless the client explicitly expects it.</p>
     */
    private boolean shouldForwardEncryptionToClient(DolphinProxySession session) {
        // For now, don't forward to simplify the flow
        // Bots typically don't need client-side encryption handling
        return false;
    }

    /**
     * Login state machine for tracking authentication progress.
     */
    private enum LoginState {
        FIRST_PACKET,      // Waiting for initial Login Hello from client
        SENT_HELLO,       // Client sent Login Hello, waiting for server response
        RECEIVED_ENCRYPTION, // Server sent encryption request
        COMPLETED         // Login successful, switched to PLAY/CONFIGURATION state
    }
}
