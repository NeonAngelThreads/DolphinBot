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
 *
 * https://space.bilibili.com/386644641
 */

package org.angellock.impl.api.action;

import org.angellock.impl.RobotPlayer;
import org.geysermc.mcprotocollib.protocol.data.game.entity.object.Direction;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.PlayerAction;

/**
 * Client-simulation layer SPI. The framework core only speaks the wire protocol
 * (sequences, teleports, ticks); anything that mimics vanilla client behaviour
 * (aiming, block interaction, item use, and later entity interaction / physics /
 * pathfinding) is implemented by a registered simulator, normally provided by
 * the Actions-Core plugin. Register via {@link RobotPlayer#registerActionSimulator}.
 */
public interface PlayerActionSimulator {

    /** Rotates the bot view towards the target point, like a vanilla player moving the mouse. */
    void aimAt(RobotPlayer bot, double x, double y, double z);

    /**
     * Right-clicks the block at the given block coordinates the way the vanilla
     * client does: aim, UseItemOn with a consistent hit face/cursor, then Swing.
     */
    void interactBlock(RobotPlayer bot, int x, int y, int z);

    /** Right-clicks the air with the held item (ServerboundUseItemPacket + Swing). */
    void useItem(RobotPlayer bot, Hand hand);

    /** Low-level UseItemOn with an explicit target face and cursor position (placement path). */
    void useItemOn(RobotPlayer bot, int x, int y, int z, Direction face,
                   double cursorX, double cursorY, double cursorZ);

    /** Swings the main hand on its own (paired with useItemOn/dig by callers). */
    void swing(RobotPlayer bot);

    /** Digging lifecycle packet: START_DIGGING, then FINISH/CANCEL_DIGGING. */
    void dig(RobotPlayer bot, int x, int y, int z, Direction face, PlayerAction action);
}
