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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * One instance of a local byte-proxy. Owns its own accept loop and a thread pool
 * of two threads per connected client (one per direction). Lifetime is tied to
 * {@link #start()} and {@link #stop()}.
 *
 * <p>Instances are created exclusively by {@link DolphinProxyServer#start(String, int, String)}.
 */
public class DolphinProxyInstance {

    private static final Logger log = LoggerFactory.getLogger("DolphinProxyInstance");

    @Getter
    private final String targetHost;
    @Getter
    private final int targetPort;
    @Getter
    private final String targetVersion;
    /** Numeric protocol id the target server speaks (used to rewrite the handshake VarInt). */
    @Getter
    private final int targetProtocolId;
    @Getter
    private final int localPort;
    @Getter
    private final String localAddress = "127.0.0.1";

    private final ExecutorService sharedPool;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicInteger activeSessions = new AtomicInteger(0);
    private ServerSocket serverSocket;
    private Thread acceptThread;

    DolphinProxyInstance(String targetHost, int targetPort, String targetVersion, int targetProtocolId, int localPort, ExecutorService sharedPool) {
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.targetVersion = targetVersion;
        this.targetProtocolId = targetProtocolId;
        this.localPort = localPort;
        this.sharedPool = sharedPool;
    }

    /**
     * Start the accept loop on {@link #localPort}. Idempotent.
     */
    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        try {
            this.serverSocket = new ServerSocket();
            this.serverSocket.setReuseAddress(true);
            this.serverSocket.bind(new InetSocketAddress(localAddress, localPort));
        } catch (IOException e) {
            running.set(false);
            throw new IllegalStateException("Could not bind DolphinProxy on " + localAddress + ":" + localPort, e);
        }
        this.acceptThread = new Thread(this::acceptLoop, "DolphinProxy-Accept-" + localPort);
        this.acceptThread.setDaemon(true);
        this.acceptThread.start();
        log.info("DolphinProxy listening on {}:{} -> {}:{} (target version {})",
                localAddress, localPort, targetHost, targetPort, targetVersion);
    }

    /**
     * Stop the accept loop and close any open session sockets. Idempotent.
     */
    public void stop() {
        if (!running.compareAndSet(true, false)) {
            return;
        }
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            log.warn("Error closing DolphinProxy accept socket on port {}", localPort, e);
        }
        if (acceptThread != null) {
            acceptThread.interrupt();
        }
        log.info("DolphinProxy on port {} stopped ({} active sessions were closed)",
                localPort, activeSessions.get());
    }

    public boolean isRunning() {
        return running.get();
    }

    public int getActiveSessions() {
        return activeSessions.get();
    }

    private void acceptLoop() {
        while (running.get()) {
            Socket client;
            try {
                client = serverSocket.accept();
            } catch (IOException e) {
                if (running.get()) {
                    log.warn("DolphinProxy accept failed on port {}", localPort, e);
                }
                return;
            }
            activeSessions.incrementAndGet();
            sharedPool.submit(() -> handleClient(client));
        }
    }

    private void handleClient(Socket clientSocket) {
        String clientDesc = clientSocket.getRemoteSocketAddress().toString();
        log.info("[port {}] client connected: {}", localPort, clientDesc);
        Socket serverSocket = null;
        Thread pipeDownstream = null;
        Thread pipeUpstream = null;
        try {
            serverSocket = new Socket();
            serverSocket.connect(new InetSocketAddress(targetHost, targetPort), 8000);
            log.info("[port {}] connected to target {}:{}", localPort, targetHost, targetPort);

            pipeDownstream = pipe(clientSocket, serverSocket, "C->S", true);
            pipeUpstream = pipe(serverSocket, clientSocket, "S->C", false);
            // Wait for either direction to close; the other pipe will then exit on EOF too.
            pipeDownstream.join();
            pipeUpstream.join();
        } catch (Throwable t) {
            log.warn("[port {}] session ended for {}: {}", localPort, clientDesc, t.getMessage());
        } finally {
            closeQuietly(clientSocket);
            closeQuietly(serverSocket);
            if (pipeDownstream != null) {
                pipeDownstream.interrupt();
            }
            if (pipeUpstream != null) {
                pipeUpstream.interrupt();
            }
            activeSessions.decrementAndGet();
            log.info("[port {}] session closed for {}", localPort, clientDesc);
        }
    }

    private Thread pipe(Socket from, Socket to, String direction, boolean rewriteHandshake) {
        Thread t = new Thread(() -> {
            try (InputStream in = from.getInputStream(); OutputStream out = to.getOutputStream()) {
                if (rewriteHandshake) {
                    if (!rewriteAndPipeHandshake(in, out)) {
                        return;
                    }
                }
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) >= 0) {
                    if (n == 0) {
                        continue;
                    }
                    out.write(buf, 0, n);
                    out.flush();
                }
            } catch (IOException ignored) {
                // Peer closed; expected when the other direction tears down the session.
            }
        }, "DolphinProxy-" + direction + "-" + localPort);
        t.setDaemon(true);
        t.start();
        return t;
    }

    /**
     * Read the first framed packet from {@code in}, expecting the Minecraft
     * "ClientIntention" / handshake packet (handshaking state, packet id 0x00), rewrite the
     * VarInt protocol-version field to {@link #targetVersion} (interpreted as a numeric
     * protocol id) and forward everything to {@code out}. Returns {@code false} if the
     * first packet is malformed or not a handshake.
     *
     * <p>This allows the bot's mcprotocollib client (which always speaks the native
     * protocol version hardcoded in its codec) to at least complete the handshake phase
     * against a server running a different version. Subsequent packets are still
     * serialized with the native codec, so the server may reject them — that is the
     * fundamental limitation of a byte-only bridge.
     */
    private boolean rewriteAndPipeHandshake(InputStream in, OutputStream out) throws IOException {
        int packetLength = readVarInt(in);
        if (packetLength <= 0) {
            return false;
        }
        int packetId = readVarInt(in);
        if (packetId != 0x00) {
            // Not a handshake. We cannot safely rewrite; pipe the rest of the frame raw.
            byte[] tail = readNBytes(in, packetLength - sizeOfVarInt(packetId));
            out.write(encodeVarInt(packetLength));
            out.write(encodeVarInt(packetId));
            out.write(tail);
            out.flush();
            return true;
        }
        int originalProtocol = readVarInt(in);
        if (originalProtocol <= 0) {
            return false;
        }
        int originalProtocolBytes = sizeOfVarInt(originalProtocol);
        // Read the rest of the packet (server address, server port, next state) verbatim.
        int consumed = sizeOfVarInt(packetId) + originalProtocolBytes;
        int remaining = packetLength - consumed;
        byte[] tail = readNBytes(in, remaining);
        int targetProtocolId = this.targetProtocolId;
        byte[] newProtoBytes = encodeVarInt(targetProtocolId);
        // Recompute the framed length.
        int newPacketBodyLength = sizeOfVarInt(packetId) + newProtoBytes.length + tail.length;
        out.write(encodeVarInt(newPacketBodyLength));
        out.write(encodeVarInt(packetId));
        out.write(newProtoBytes);
        out.write(tail);
        out.flush();
        if (targetProtocolId != originalProtocol) {
            log.info("[port {}] rewrote handshake protocolVersion {} -> {} ({})", localPort,
                    originalProtocol, targetProtocolId, targetVersion);
        } else {
            log.debug("[port {}] handshake protocolVersion already {} ({})", localPort, originalProtocol, targetVersion);
        }
        return true;
    }

    private static int readVarInt(InputStream in) throws IOException {
        int value = 0;
        int position = 0;
        int b;
        while ((b = in.read()) != -1) {
            value |= (b & 0x7F) << position;
            if ((b & 0x80) == 0) {
                return value;
            }
            position += 7;
            if (position >= 35) {
                throw new IOException("VarInt too big");
            }
        }
        return -1;
    }

    private static byte[] encodeVarInt(int value) {
        byte[] out = new byte[sizeOfVarInt(value)];
        int i = 0;
        while ((value & ~0x7F) != 0) {
            out[i++] = (byte) ((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out[i] = (byte) value;
        return out;
    }

    private static int sizeOfVarInt(int value) {
        int size = 1;
        while ((value & ~0x7F) != 0) {
            size++;
            value >>>= 7;
        }
        return size;
    }

    private static byte[] readNBytes(InputStream in, int n) throws IOException {
        byte[] buf = new byte[n];
        int off = 0;
        while (off < n) {
            int read = in.read(buf, off, n - off);
            if (read < 0) {
                throw new IOException("Unexpected EOF, wanted " + n + " bytes, got " + off);
            }
            off += read;
        }
        return buf;
    }

    private static void closeQuietly(Socket s) {
        if (s == null) {
            return;
        }
        try {
            s.close();
        } catch (IOException ignored) {
        }
    }
}
