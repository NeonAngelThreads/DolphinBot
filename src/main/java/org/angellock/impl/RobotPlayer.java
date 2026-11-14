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
 * https://space.bilibili.com/386644641
 */

package org.angellock.impl;

import lombok.Getter;
import lombok.Setter;
import org.angellock.impl.api.action.PlayerActionSimulator;
import org.angellock.impl.api.state.LoginState;
import org.angellock.impl.api.state.LoginStateMachine;
import org.angellock.impl.api.state.StateAction;
import org.angellock.impl.extensions.actions.JoinAction;
import org.angellock.impl.extensions.actions.LoginAction;
import org.angellock.impl.extensions.actions.RegisterAction;
import org.angellock.impl.ingame.abstracts.IPlayer;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.plugin.PluginManager;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TranslatableUtil;
import org.angellock.impl.util.math.Position;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundClientTickEndPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.level.ServerboundAcceptTeleportationPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerPosPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerPosRotPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerRotPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerStatusOnlyPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundSetCarriedItemPacket;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RobotPlayer extends AbstractRobot implements IPlayer {
    private long connectTime;
    private long lastMsgTime = 0L;
    private final long msgDelay;
    @Setter
    private volatile boolean shouldReconnect = true;
    @Getter
    private final ChatMessageManager messageManager;
    @Getter
    private final LoginStateMachine loginStateMachine = new LoginStateMachine(LoginState.DISCONNECTED);
    @Setter
    protected Position loginPos = new Position();

    // ===== Vanilla client emulation state (mirrors net.minecraft.client.player.LocalPlayer) =====
    /** Vanilla LocalPlayer.sendPosition: position delta squared threshold before a Pos packet is sent. */
    private static final double POSITION_MOVE_THRESHOLD_SQ = 2.0E-4 * 2.0E-4;
    /** Vanilla sends a position packet at least every 20 ticks even when idle. */
    private static final int POSITION_REMINDER_LIMIT = 20;

    /** Player look angles, updated on teleport / when aiming (LocalPlayer.yRot/xRot). */
    private volatile float yRot = 0.0F;
    private volatile float xRot = 0.0F;
    /** Assumed ground state; a stationary bot behaves like a vanilla client standing still.
     * -- SETTER --
     * Ground flag as reported in the next movement packet.
     */
    @Setter
    private volatile boolean onGround = true;
    private final boolean horizontalCollision = false;

    /**
     * Prediction sequence used by UseItemOn/UseItem/PlayerAction packets.
     * Vanilla: BlockStatePredictionHandler.startPredicting() pre-increments, so the first
     * sequence ever sent is 1 and it increases by exactly 1 per interaction packet.
     * The server (ServerGamePacketListenerImpl.ackBlockChangesUpTo) throws on negative values
     * and anti-cheats verify the strict +1 monotonicity — never send timestamps here.
     */
    private final AtomicInteger blockChangeSequence = new AtomicInteger(0);

    /** Currently carried hotbar slot as known by the server (Inventory.selected, vanilla default 0). */
    private volatile int carriedSlot = 0;

    /**
     * High-level client behaviours (aim/interact/use). Implemented by the
     * simulation layer (Actions-Core); the core only dispatches to it so that
     * bundled callers keep working when the simulator is present.
     */
    private static volatile PlayerActionSimulator actionSimulator;
    private volatile boolean actionSimulatorWarned = false;

    // Last values actually sent to the server, LocalPlayer.xLast/yLast/... equivalents.
    private double xLast, yLast, zLast;
    private float yRotLast, xRotLast;
    private boolean lastOnGround = false;
    private boolean lastHorizontalCollision = false;
    private int positionReminder = 0;

    private final ScheduledExecutorService vanillaTickScheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "DolphinBot-VanillaTick");
                t.setDaemon(true);
                return t;
            });
    private volatile boolean vanillaTickRunning = false;
    private volatile java.util.concurrent.ScheduledFuture<?> vanillaTickFuture;

    public RobotPlayer(ConfigManager configManager, PluginManager pluginManager) {
        super(configManager, pluginManager);

        LoginStateMachine stateMachine = this.loginStateMachine;
        StateAction registerAction = new RegisterAction(this);
        StateAction joinAction = new JoinAction(this);
        StateAction loginAction = new LoginAction(stateMachine, this);

        stateMachine
                .source(LoginState.DISCONNECTED).whenReceive("离线玩家请注册").goal(LoginState.REGISTER, registerAction)
                .and()
                .whenReceive("离线玩家请登陆").goal(LoginState.LOGIN, loginAction)
                .and()
                .whenReceive("登陆成功").goal(LoginState.JOIN, joinAction)
                .source(LoginState.VERIFY).whenReceive("机器人验证已完毕").goal(LoginState.REGISTER, registerAction)
                .source(LoginState.REGISTER).whenReceive("已成功注册").goal(LoginState.JOIN, joinAction)
                .source(LoginState.LOGIN).whenReceive("登陆成功").goal(LoginState.JOIN, joinAction)
                .source(LoginState.JOIN).whenReceive("Position in queue").goal(LoginState.IDLE, null)
                .build();
        this.messageManager = new ChatMessageManager(this);
        this.msgDelay = Long.parseLong(Optional
                .ofNullable(
                        this.globalConfig.getConfigValue("msg-send-delay"))
                .orElse("3000")
        );
    }

    @Override
    public void mainTickingEventLoop() {
        try {
            boolean connect = true;
            boolean shouldWait = false;

            while (true) {
                try {
                    Thread.sleep(50L);
                    if (!this.session.isConnected()){
                        this.connectDuration = System.currentTimeMillis();
                        break;
                    } else if (connect) {
                        if (System.currentTimeMillis() - this.connectDuration > 100L){
                            this.pluginManager.loadAllPlugins(this);
                            connect = false;
                        }
                    } else if (!shouldWait) {
                        if (this.getMessageManager().pollMessage()) {
                            this.lastMsgTime = System.currentTimeMillis();
                            shouldWait = true;
                        }
                    } else if (canSendMessages()) {
                        shouldWait = false;
                    }
                }
                catch (InterruptedException ignored){
                } catch (Throwable e) {
                    TranslatableUtil.warnTranslatableOf(EnumSystemEvents.PACKET_ERROR, (Object) e.getStackTrace());
                }
            }
        } finally {
            this.session.disconnect("");
            if (BotManager.getBotByProfileName(getProfileName()) != null){
                scheduleReconnect();
            }
        }
    }

    @Override
    public boolean canSendMessages() {
        long t = System.currentTimeMillis();
        return t - lastMsgTime > msgDelay;
    }

    @Override
    public void onJoin() {
        log.info(this.getBotLabel(), TranslatableUtil.getFormattedMessage(EnumSystemEvents.SERVER_CONNECTION_ESTABLISHED, this.getProfileName()));
    }

    @Override
    public void onQuit(String reason) {
        long millis = System.currentTimeMillis() - this.connectTime;
        log.info(this.getBotLabel(), ConsoleTokens.colorizeText("[{}] &7Session Duration: &f{}ms"), this.getProfileName(), millis);
        log.info(this.getBotLabel(), TranslatableUtil.getFormattedMessage(EnumSystemEvents.DISCONNECT, reason));
        this.getPluginManager().disableAllPlugins(this);
        this.getSession().getChannel().close();
        this.getSession().getChannel().deregister();
        this.getSession().getChannel().closeFuture();
        TranslatableUtil.infoTranslatableOf(EnumSystemEvents.DOLPHIN_TIMING_RESET);
        BotManager.bots().put(getProfileName(), this);
    }

    @Override
    public void onPreLogin() {
        while (!this.shouldReconnect) {
            try {Thread.sleep(100L);
            } catch (InterruptedException ignored) {}
        }
        // A fresh login means a fresh vanilla client: prediction sequence and the
        // carried slot the server knows about both restart from zero.
        this.blockChangeSequence.set(0);
        this.carriedSlot = 0;
        this.connectTime = System.currentTimeMillis();
        log.info(this.getBotLabel(), TranslatableUtil.getFormattedMessage(EnumSystemEvents.CONNECT, this.getInfoHelper().getServer(), String.valueOf(this.getInfoHelper().getPort())));
    }

    @Override
    public double getDistanceFromOthers(IPlayer player) {
        return this.getPosition().getDistance(player.getPosition());
    }

    @Override
    public Position getPosition() {
        return this.loginPos;
    }
    @Override
    public float getYRot() {
        return this.yRot;
    }
    @Override
    public float getXRot() {
        return this.xRot;
    }

    /**
     * Sets the look angles (protocol-level rotation state). The periodic tick loop
     * reports the new rotation with a Rot/PosRot packet, exactly like vanilla.
     * High-level "face that point" aiming lives in the simulation layer.
     */
    @Override
    public void setRotation(float yRot, float xRot) {
        this.yRot = yRot;
        this.xRot = xRot;
    }

    /**
     * Protocol-level position write (LocalPlayer position field). The periodic tick
     * loop reports it to the server. Movement/pathing logic belongs to the
     * simulation layer (Actions-Core); the core only carries the state.
     */
    @Override
    public void setFeetPosition(double x, double y, double z) {
        this.loginPos.from(x, y, z);
    }

    /**
     * Registers the client-simulation implementation (normally Actions-Core).
     * Without it, aim/interact/use calls are no-ops with a one-time warning.
     */
    public static void registerActionSimulator(PlayerActionSimulator simulator) {
        actionSimulator = simulator;
    }

    private PlayerActionSimulator requireSimulator() {
        PlayerActionSimulator simulator = actionSimulator;
        if (simulator == null && !this.actionSimulatorWarned) {
            this.actionSimulatorWarned = true;
            log.warn("[{}] No PlayerActionSimulator registered (is Actions-Core loaded?); aim/interact/use calls are ignored.", this.getProfileName());
        }
        return simulator;
    }

    /**
     * Vanilla MultiPlayerGameMode.ensureHasSentCarriedItem: only send the packet when the
     * selected slot actually differs from what the server currently knows.
     */
    public void ensureHasSentCarriedItem(int slot) {
        if (slot != this.carriedSlot) {
            this.carriedSlot = slot;
            this.sendPacket(new ServerboundSetCarriedItemPacket(slot));
        }
    }

    /**
     * Next block-change prediction sequence (BlockStatePredictionHandler semantics:
     * startPredicting pre-increments, so the first value is 1 and each interaction
     * packet consumes exactly one increment).
     */
    @Override
    public int nextUseSequence() {
        return this.blockChangeSequence.incrementAndGet();
    }

    /**
     * Called when the server teleports us (ClientboundPlayerPositionPacket).
     * Mirrors ClientPacketListener.handleMovePlayer: update local position/rotation, then
     * reply ServerboundAcceptTeleportationPacket followed by a PosRot packet with
     * (onGround=false, horizontalCollision=false). Without this the server keeps
     * awaitingPositionFromClient set and silently ignores ALL block interactions.
     */

    public void onServerTeleport(int teleportId, double x, double y, double z, float yRot, float xRot) {
        log.info("[Move] server teleport #{} -> ({}, {}, {}) rot=({}, {})", teleportId, x, y, z, yRot, xRot);
        this.loginPos.from(x, y, z);
        this.yRot = yRot;
        this.xRot = xRot;
        // Reset the "last sent" tracking so the next tick sends exactly what vanilla would.
        this.xLast = x;
        this.yLast = y;
        this.zLast = z;
        this.yRotLast = yRot;
        this.xRotLast = xRot;
        this.positionReminder = 0;
        this.lastOnGround = false;
        this.lastHorizontalCollision = false;

        this.sendPacket(new ServerboundAcceptTeleportationPacket(teleportId));
        this.sendPacket(new ServerboundMovePlayerPosRotPacket(false, false, x, y, z, yRot, xRot));
        startVanillaTickLoop();
    }

    /**
     * Starts the 20 TPS client tick loop (50 ms) that vanilla Minecraft runs in
     * Minecraft.runTick: every tick the client sends its position update
     * (LocalPlayer.sendPosition) and finishes with ServerboundClientTickEndPacket.
     * A client that never sends movement/tick-end packets is trivially flagged by anti-cheats.
     */
    public void startVanillaTickLoop() {
        if (this.vanillaTickRunning) {
            return;
        }
        this.vanillaTickRunning = true;
        this.vanillaTickFuture = this.vanillaTickScheduler.scheduleAtFixedRate(() -> {
            try {
                if (this.session == null || !this.session.isConnected()){
                    this.connectDuration = System.currentTimeMillis();
                    stopVanillaTickLoop();
                    return;
                }
                sendPositionTick();
                this.sendPacket(ServerboundClientTickEndPacket.INSTANCE);
            } catch (Throwable e) {
                TranslatableUtil.warnTranslatableOf(EnumSystemEvents.PACKET_ERROR, (Object) e.getStackTrace());
            }
        }, 50L, 50L, TimeUnit.MILLISECONDS);
    }

    public void stopVanillaTickLoop() {
        this.vanillaTickRunning = false;
        java.util.concurrent.ScheduledFuture<?> future = this.vanillaTickFuture;
        if (future != null) {
            future.cancel(false);
            this.vanillaTickFuture = null;
        }
    }

    /** LocalPlayer.sendPosition(): pick Pos/PosRot/Rot/StatusOnly based on what actually changed. */
    private void sendPositionTick() {
        double x = this.loginPos.getX();
        double y = this.loginPos.getY();
        double z = this.loginPos.getZ();
        double dx = x - this.xLast;
        double dy = y - this.yLast;
        double dz = z - this.zLast;
        double dYaw = this.yRot - this.yRotLast;
        double dPitch = this.xRot - this.xRotLast;
        this.positionReminder++;
        boolean moved = dx * dx + dy * dy + dz * dz > POSITION_MOVE_THRESHOLD_SQ || this.positionReminder >= POSITION_REMINDER_LIMIT;
        boolean rotated = dYaw != 0.0 || dPitch != 0.0;

        if (moved && rotated) {
            this.sendPacket(new ServerboundMovePlayerPosRotPacket(this.onGround, this.horizontalCollision, x, y, z, this.yRot, this.xRot));
        } else if (moved) {
            this.sendPacket(new ServerboundMovePlayerPosPacket(this.onGround, this.horizontalCollision, x, y, z));
        } else if (rotated) {
            this.sendPacket(new ServerboundMovePlayerRotPacket(this.onGround, this.horizontalCollision, this.yRot, this.xRot));
        } else if (this.lastOnGround != this.onGround || this.lastHorizontalCollision != this.horizontalCollision) {
            this.sendPacket(new ServerboundMovePlayerStatusOnlyPacket(this.onGround, this.horizontalCollision));
        }

        if (moved) {
            this.xLast = x;
            this.yLast = y;
            this.zLast = z;
            this.positionReminder = 0;
        }
        if (rotated) {
            this.yRotLast = this.yRot;
            this.xRotLast = this.xRot;
        }
        this.lastOnGround = this.onGround;
        this.lastHorizontalCollision = this.horizontalCollision;
    }

    /**
     * Updates the look angles so the bot faces the target point. Dispatches to the
     * registered {@link PlayerActionSimulator} (Actions-Core); the new rotation is
     * reported to the server by the periodic tick loop with a Rot/PosRot packet.
     */
    @Override
    public void aimAt(double targetX, double targetY, double targetZ) {
        PlayerActionSimulator simulator = requireSimulator();
        if (simulator != null) {
            simulator.aimAt(this, targetX, targetY, targetZ);
        }
    }

    @Override
    public void interactBlock(double x, double y, double z) {
        this.interactBlock((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    }

    /**
     * Right-clicks a block like the vanilla client (aim -> UseItemOn with real hit
     * face/cursor -> Swing). Implemented by the registered {@link PlayerActionSimulator}.
     */
    @Override
    public void interactBlock(int x, int y, int z) {
        PlayerActionSimulator simulator = requireSimulator();
        if (simulator != null) {
            simulator.interactBlock(this, x, y, z);
        }
    }

    /**
     * Uses (right-clicks) the held item in the air. Implemented by the registered
     * {@link PlayerActionSimulator}.
     */
    @Override
    public void useItem(Hand hand) {
        PlayerActionSimulator simulator = requireSimulator();
        if (simulator != null) {
            simulator.useItem(this, hand);
        }
    }
}
