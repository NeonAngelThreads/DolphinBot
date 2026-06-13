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
 *    program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * https://space.bilibili.com/3865746441
 */

package org.angellock.impl.protocol;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lightweight, single-process, byte-level proxy for Minecraft connections.
 *
 * <p>This is a deliberately minimal proxy. It accepts an mcprotocollib client connection on a
 * local port, opens a TCP socket to the target Minecraft server, and bidirectionally pipes
 * bytes without inspecting or modifying any packet. There is no protocol translation, no
 * handshake rewriting, no compression/encryption handling. Whatever the client sends is
 * forwarded verbatim to the server, and whatever the server sends is forwarded verbatim to
 * the client.
 *
 * <h2>Why byte-only?</h2>
 * <p>mcprotocollib 1.21.11 only ships one protocol codec. It cannot impersonate 1.20/1.19/etc.
 * clients, so true cross-version translation would require a full ViaProxy-style proxy which
 * is out of scope for this bot. Instead, when the bot's mcprotocollib client connects to a
 * server that runs a different protocol version, we open a local byte bridge so the bot
 * (and any future DolphinBot UI tools that read the local port) can still go through a
 * single, observable hop.
 *
 * <h2>Multiple bots</h2>
 * <p>Each bot that needs bridging spawns its own {@link DolphinProxyInstance} on a different
 * local port. All instances live inside the same JVM, so this still satisfies the
 * "single process" constraint. The {@link #getInstance()} singleton keeps a registry of
 * running instances and stops them on JVM shutdown.
 *
 * <h2>Limitations</h2>
 * <ul>
 *   <li>The handshake's server address field will be the proxy's own {@code 127.0.0.1:port}
 *       rather than the real backend host. Servers that use IP whitelists or BungeeCord
 *       forwarding will not work.</li>
 *   <li>No protocol translation. Packets that the target server does not understand will
 *       be rejected.</li>
 *   <li>No compression / encryption handling. The bot must speak plaintext with the proxy.</li>
 * </ul>
 *
 * <h2>UI hooks</h2>
 * <p>{@link DolphinProxyInstance} exposes getters for every relevant value, so a future
 * DolphinBot UI can show running instances, stop them, or reconfigure targets.
 */
public class DolphinProxyServer {

    private static final Logger log = LoggerFactory.getLogger("DolphinProxyServer");

    @Getter
    private static final DolphinProxyServer instance = new DolphinProxyServer();

    private final AtomicInteger nextPortHint = new AtomicInteger(25568);
    private final Map<Integer, DolphinProxyInstance> instances = new ConcurrentHashMap<>();
    private final ExecutorService sharedPool = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "DolphinProxy-Shared");
        t.setDaemon(true);
        return t;
    });

    private DolphinProxyServer() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopAll, "DolphinProxy-Shutdown"));
    }

    /**
     * Start a new local proxy instance pointing at {@code targetHost:targetPort}.
     *
     * @param targetHost       the real backend Minecraft server host
     * @param targetPort       the real backend Minecraft server port
     * @param targetVersion    human-readable version label, only used for logging
     * @param targetProtocolId numeric Minecraft protocol id the backend speaks. The
     *                         proxy will rewrite the handshake packet's protocolVersion
     *                         VarInt to this id so the handshake phase is accepted.
     * @return a started {@link DolphinProxyInstance}, never {@code null}
     */
    public synchronized DolphinProxyInstance start(String targetHost, int targetPort, String targetVersion, int targetProtocolId) {
        int localPort = allocateFreePort();
        DolphinProxyInstance inst = new DolphinProxyInstance(targetHost, targetPort, targetVersion, targetProtocolId, localPort, sharedPool);
        inst.start();
        instances.put(localPort, inst);
        return inst;
    }

    /**
     * Convenience overload that uses the modern mcprotocollib protocol id (769) as the
     * handshake-rewrite value. Prefer {@link #start(String, int, String, int)} so the
     * rewrite actually matches the real backend version.
     */
    public synchronized DolphinProxyInstance start(String targetHost, int targetPort, String targetVersion) {
        return start(targetHost, targetPort, targetVersion, 769);
    }

    /**
     * Stop the proxy bound to {@code localPort}. No-op if no such instance exists.
     */
    public void stop(int localPort) {
        DolphinProxyInstance inst = instances.remove(localPort);
        if (inst != null) {
            inst.stop();
        }
    }

    /**
     * Stop every running proxy instance. Safe to call multiple times.
     */
    public void stopAll() {
        for (DolphinProxyInstance inst : instances.values()) {
            try {
                inst.stop();
            } catch (Throwable t) {
                log.warn("Error stopping proxy on port {}", inst.getLocalPort(), t);
            }
        }
        instances.clear();
    }

    /** @return unmodifiable snapshot of all running instances, keyed by local port. */
    public Map<Integer, DolphinProxyInstance> getInstances() {
        return Map.copyOf(instances);
    }

    private int allocateFreePort() {
        // Try 100 candidate ports starting at nextPortHint.
        for (int i = 0; i < 100; i++) {
            int candidate = nextPortHint.getAndIncrement();
            if (candidate > 30000) {
                nextPortHint.set(25568);
                candidate = nextPortHint.getAndIncrement();
            }
            if (instances.containsKey(candidate)) {
                continue;
            }
            try (ServerSocket probe = new ServerSocket()) {
                probe.setReuseAddress(true);
                probe.bind(new InetSocketAddress("127.0.0.1", candidate));
                return candidate;
            } catch (IOException ignored) {
                // try next
            }
        }
        throw new IllegalStateException("Could not allocate a free local port for DolphinProxyServer");
    }
}
