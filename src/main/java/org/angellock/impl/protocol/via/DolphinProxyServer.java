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
 *    program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.angellock.impl.protocol.via;

import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.commands.ViaCommandHandler;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * Embedded Minecraft protocol-translation proxy powered by ViaVersion.
 *
 * <p>Usage:</p>
 * <pre>
 *   // Initialize once (globally, before any bot connects)
 *   DolphinProxyServer.initVia();
 *
 *   // For each bot that needs cross-version support:
 *   int localPort = DolphinProxyServer.getInstance().reservePort(botName);
 *   session = DolphinProxyServer.getInstance().createSession(
 *       botName, localPort,
 *       targetHost, targetPort,
 *       clientProtocol, serverProtocol);
 *
 *   // Bot then connects to localhost:localPort instead of targetHost:targetPort
 *   // The proxy handles all protocol translation transparently.
 * </pre>
 */
public class DolphinProxyServer {

    private static final Logger log = LoggerFactory.getLogger("DolphinProxy");

    /** Singleton instance – one per JVM (all bots share it). */
    private static volatile DolphinProxyServer instance;

    /** Whether ViaVersion has been initialized. */
    private static volatile boolean viaInitialized = false;

    // ── Netty infrastructure ──
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private Class<? extends ServerChannel> serverChannelType;
    private Class<? extends Channel> clientChannelType;

    // ── Session management ──
    private final ConcurrentMap<String, DolphinProxySession> sessions = new ConcurrentHashMap<>();

    /** Pending sessions waiting for their bot to connect. FIFO order. */
    private final ConcurrentLinkedQueue<DolphinProxySession> pendingSessions = new ConcurrentLinkedQueue<>();

    // ── Port allocation ──
    private int basePort = 25566;
    private int nextPort = 25566;

    /**
     * Initialize ViaVersion globally. Must be called once before any proxy sessions are created.
     * Safe to call multiple times – subsequent calls are no-ops.
     */
    public static synchronized void initVia() {
        if (viaInitialized) {
            log.debug("ViaVersion already initialized (by us), skipping");
            return;
        }

        // Check if ViaVersion was already initialized elsewhere (e.g. old ViaVersionManager)
        try {
            Via.getManager();
            log.info("ViaVersion already initialized by another component, reusing existing instance");
            viaInitialized = true;
            return;
        } catch (IllegalStateException ignored) {
            // Not initialized yet – proceed with full init
        }

        log.info("Initializing ViaVersion for embedded proxy...");
        try {
            ViaManagerImpl.initAndLoad(
                    new DolphinViaPlatform(),
                    new DolphinViaInjector(),
                    new ViaCommandHandler(false),
                    new DolphinViaPlatformLoader(),
                    () -> {
                        // Post-init hook (nothing needed for now)
                    }
            );

            // Register auto-detect protocol version (like ViaProxy does)
            ProtocolVersion.register(ProtocolVersion.getProtocol(-1)); // AUTO_DETECT

            viaInitialized = true;
            log.info("ViaVersion initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize ViaVersion", e);
            throw new RuntimeException("ViaVersion initialization failed", e);
        }
    }

    /**
     * Get or create the singleton instance.
     */
    public static synchronized DolphinProxyServer getInstance() {
        if (instance == null) {
            instance = new DolphinProxyServer();
        }
        return instance;
    }

    private DolphinProxyServer() {
        // Use epoll on Linux, NIO elsewhere
        // Use a SINGLE shared EventLoopGroup to avoid blocking on second NioEventLoopGroup creation.
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("linux")) {
            bossGroup = new EpollEventLoopGroup();
            workerGroup = bossGroup; // Share the same group
            serverChannelType = EpollServerSocketChannel.class;
            clientChannelType = EpollSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup();
            workerGroup = bossGroup; // Share the same group
            serverChannelType = NioServerSocketChannel.class;
            clientChannelType = NioSocketChannel.class;
        }
    }

    /**
     * Start the local proxy server listening on {@code bindPort}.
     * Each bot gets its own port to avoid conflicts.
     */
    public void start(int bindPort) throws InterruptedException {
        if (serverChannel != null && serverChannel.isActive()) {
            log.warn("Proxy server already running");
            return;
        }

        this.basePort = bindPort;
        this.nextPort = bindPort;

        DolphinClientHandler clientHandler = new DolphinClientHandler(this);

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new DolphinClientChannelInitializer(clientHandler));

        // Use CountDownLatch to wait for async bind completion
        java.util.concurrent.CountDownLatch bindLatch = new java.util.concurrent.CountDownLatch(1);
        final Throwable[] bindError = {null};

        bootstrap.bind(bindPort).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                serverChannel = future.channel();
                log.info("DolphinProxyServer started on port {}", bindPort);
            } else {
                bindError[0] = future.cause();
                log.error("DolphinProxyServer failed to bind on port {}: {}", bindPort, future.cause().getMessage());
            }
            bindLatch.countDown();
        });

        // Wait up to 10 seconds for bind to complete
        if (!bindLatch.await(10, java.util.concurrent.TimeUnit.SECONDS)) {
            throw new RuntimeException("Bind timeout on port " + bindPort);
        }
        if (bindError[0] != null) {
            throw new RuntimeException("Bind failed", bindError[0]);
        }
    }

    /**
     * Reserve a unique local port for a bot and create a session.
     *
     * @param botName          Bot identifier
     * @param targetHost       Target Minecraft server hostname
     * @param targetPort       Target Minecraft server port
     * @param clientProtocol   Client-side protocol version (from mcprotocollib)
     * @param serverProtocol   Server-side protocol version (detected from server)
     * @return The created session with its assigned local port
     */
    public DolphinProxySession createSession(String botName,
                                            String targetHost, int targetPort,
                                            ProtocolVersion clientProtocol,
                                            ProtocolVersion serverProtocol) {
        log.info("Creating proxy session '{}' → {}:{} (client={} server={})",
                botName, targetHost, targetPort,
                clientProtocol.getName(), serverProtocol.getName());

        DolphinProxySession session = new DolphinProxySession();
        session.setBotName(botName);
        session.setTargetHost(targetHost);
        session.setTargetPort(targetPort);
        session.setClientProtocolVersion(clientProtocol);
        session.setServerProtocolVersion(serverProtocol);

        sessions.put(botName, session);
        // Enqueue for the next incoming client connection to pick up
        pendingSessions.offer(session);
        return session;
    }

    /**
     * Dequeue the next pending session. Called by {@link DolphinClientChannelInitializer}
     * when a new client connects to the proxy.
     */
    public DolphinProxySession pollPendingSession() {
        return pendingSessions.poll();
    }

    /**
     * Connect to the backend server for the given session.
     *
     * @param session      The proxy session
     * @param host         Backend server host
     * @param port         Backend server port
     * @param onSuccess    Called when connected and handshake forwarded
     * @param onFailure    Called on connection failure
     */
    public void connectToBackend(DolphinProxySession session, String host, int port,
                                 Runnable onSuccess, Consumer<Throwable> onFailure) {
        DolphinServerHandler serverHandler = new DolphinServerHandler();
        Bootstrap bootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(clientChannelType)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new DolphinServerChannelInitializer(serverHandler, session));

        bootstrap.connect(host, port).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                Channel serverCh = future.channel();
                session.setServerChannel(serverCh);
                log.info("[P2S] Connected to backend {}:{}", host, port);
                onSuccess.run();
            } else {
                onFailure.accept(future.cause());
            }
        });
    }

    /**
     * Remove and disconnect a session.
     */
    public void removeSession(String botName) {
        DolphinProxySession session = sessions.remove(botName);
        if (session != null) {
            session.disconnect("session removed");
            log.info("Proxy session '{}' removed", botName);
        }
    }

    /**
     * Get an active session by bot name.
     */
    public DolphinProxySession getSession(String botName) {
        return sessions.get(botName);
    }

    /**
     * Shut down the proxy server and release resources.
     */
    public synchronized void shutdown() {
        log.info("Shutting down DolphinProxyServer...");

        // Disconnect all sessions
        sessions.forEach((name, session) -> session.disconnect("proxy shutting down"));
        sessions.clear();

        // Close server channel
        if (serverChannel != null && serverChannel.isActive()) {
            serverChannel.close().syncUninterruptibly();
        }

        // Shutdown event loop groups
        if (bossGroup != null) bossGroup.shutdownGracefully();
        if (workerGroup != null) workerGroup.shutdownGracefully();

        serverChannel = null;
        log.info("DolphinProxyServer stopped");
    }

    public boolean isRunning() {
        return serverChannel != null && serverChannel.isActive();
    }
}
