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

package org.angellock.impl.extensions.tasks;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TimingUtil;
import org.angellock.impl.util.math.Position;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class RunnableAFKAction implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RunnableAFKAction.class);

    private final AbstractRobot bot;

    private final Random random = TimingUtil.getRandomizer();
    private final Integer targetX;
    private final Integer targetY;
    private final Integer targetZ;

    // --- note block echo detector state (wired lazily onto the session) ---
    private Session wiredSession = null;
    private final java.util.List<int[]> noteBlocks = java.util.Collections.synchronizedList(new java.util.ArrayList<>());

    // --- sweep state (no fixed target): offsets around the bot's feet block ---
    private static final int[][] OFFSETS = buildOffsets();
    private int sweepIndex = 0;
    private boolean reportedNotReady = false;

    public RunnableAFKAction(AbstractRobot bot) {
        this(bot, null, null, null);
    }

    /**
     * @param targetX block X to right-click every cycle (e.g. a note block), or null for sweep mode
     * @param targetY block Y
     * @param targetZ block Z
     * Sweep mode right-clicks every block in a small radius around the bot
     * (one per tick) until a block.note_block.* sound comes back from the
     * server, then locks onto that block and keeps it ringing.
     */
    public RunnableAFKAction(AbstractRobot bot, Integer targetX, Integer targetY, Integer targetZ) {
        this.bot = bot;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    private static int[][] buildOffsets() {
        java.util.List<int[]> list = new java.util.ArrayList<>();

            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    if (dx == 0 && dz == 0) continue; // skip the bot's own body
                    list.add(new int[]{dx, -1, dz});
                }
            }

        return list.toArray(new int[0][]);
    }

    private void wireSoundDetector(RobotPlayer robot) {
        Session session = robot.getSession();
        if (session == null || session == this.wiredSession) {
            return;
        }
        this.wiredSession = session;
//        session.addListener(new SessionAdapter() {
//            @Override
//            public void packetReceived(Session session, Packet packet) {
//                // TEMP DEBUG: prove the server still talks to us and whether any sound arrives.
//                if (packet instanceof org.geysermc.mcprotocollib.protocol.packet.common.clientbound.ClientboundKeepAlivePacket ka) {
//                    log.info("[Dbg] <- KeepAlive pingId={}", ka.getPingId());
//                    return;
//                }
//                if (packet instanceof org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.border.ClientboundInitializeBorderPacket ib) {
//                    log.info("[Dbg] <- BorderInit center=({},{}) size={} warnBlocks={}", ib.getNewCenterX(), ib.getNewCenterZ(), ib.getNewSize(), ib.getWarningBlocks());
//                }
//                if (packet instanceof org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.border.ClientboundSetBorderSizePacket bs) {
//                    log.info("[Dbg] <- BorderSize {}", bs.getSize());
//                }
//                if (!(packet instanceof ClientboundSoundPacket soundPacket)) {
//                    return;
//                }
//                Sound sound = soundPacket.getSound();
//                String name = sound.getName();
//                log.info("[Dbg] <- Sound {} at ({},{},{}) pitch={}", name, soundPacket.getX(), soundPacket.getY(), soundPacket.getZ(), soundPacket.getPitch());
//                if (name != null && name.contains("note_block")) {
//                    int bx = (int) Math.floor(soundPacket.getX());
//                    int by = (int) Math.floor(soundPacket.getY());
//                    int bz = (int) Math.floor(soundPacket.getZ());
//                    synchronized (noteBlocks) {
//                        boolean known = noteBlocks.stream().anyMatch(p -> p[0] == bx && p[1] == by && p[2] == bz);
//                        if (!known) {
//                            noteBlocks.add(new int[]{bx, by, bz});
//                            log.info("[AFK] SUCCESS! note block located at {},{},{} (total {})", bx, by, bz, noteBlocks.size());
//                        }
//                    }
//                    log.info("[AFK] heard note block sound {} at ({},{},{}) pitch={}",
//                            name, soundPacket.getX(), soundPacket.getY(), soundPacket.getZ(),
//                            soundPacket.getPitch());
//                }
//            }
//        });
    }

    @Override
    public void run() {
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        int lockIndex = 0;
        while (true) {
            RobotPlayer robot = (RobotPlayer) this.bot;
            try {
                wireSoundDetector(robot);

                int[] lockedTarget = null;
                synchronized (noteBlocks) {
                    if (!noteBlocks.isEmpty()) {
                        lockedTarget = noteBlocks.get(lockIndex % noteBlocks.size());
                        lockIndex++;
                    }
                }

                if (lockedTarget != null) {
                    // Cycle through every known note block so consecutive interact
                    // events differ (some servers/plugins dedupe identical repeats).
                    robot.interactBlock(lockedTarget[0], lockedTarget[1], lockedTarget[2]);
                } else if (this.targetX != null) {
                    robot.interactBlock(this.targetX, this.targetY, this.targetZ);
                } else {
                    sweepOnce(robot);
                }
            } catch (Throwable t) {
                log.warn(ConsoleTokens.colorizeText("&e[AFK] tick error: &7{}"), t.toString());
            }
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void sweepOnce(RobotPlayer robot) {
        Position pos = robot.getPosition();
        if (pos.getX() == 0.0 && pos.getY() == 0.0 && pos.getZ() == 0.0) {
            if (!this.reportedNotReady) {
                this.reportedNotReady = true;
                log.info("[AFK] waiting for first server teleport (position still 0,0,0)...");
            }
            return; // not joined yet
        }
        if (this.reportedNotReady) {
            this.reportedNotReady = false;
            log.info("[AFK] position acquired, sweep starting at base ({},{},{})", pos.getX(), pos.getY(), pos.getZ());
        }
        int bx = (int) Math.floor(pos.getX());
        int by = (int) Math.floor(pos.getY());
        int bz = (int) Math.floor(pos.getZ());
        int[] off = OFFSETS[this.sweepIndex];
        this.sweepIndex = (this.sweepIndex + 1) % OFFSETS.length;
        robot.interactBlock(bx + off[0], by + off[1], bz + off[2]);
    }

    // Kept so older call-sites that used the random look-around variant still compile;
    // sweep mode replaces it.
    @SuppressWarnings("unused")
    private void randomLook(RobotPlayer robot) {
        Position position = robot.getPosition();
        double x = position.getX() - random.nextInt(-2, 2);
        double y = position.getY() - random.nextInt(0, 2);
        double z = position.getZ() - random.nextInt(-2, 2);
        robot.aimAt(x, y, z);
        robot.useItem(Hand.MAIN_HAND);
    }
}
