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

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.Stack;

/**
 * Utility for managing auto-read state on channels during state transitions.
 * Mirrors ViaProxy's ChannelUtil — uses a stack so nested disable/restore works.
 */
public final class DolphinChannelUtil {

    private static final AttributeKey<Stack<Boolean>> LAST_AUTO_READ =
            AttributeKey.valueOf("dolphin_last_auto_read");

    private DolphinChannelUtil() {}

    /**
     * Disable auto-read on the channel, pushing the current state onto a stack.
     */
    public static void disableAutoRead(Channel channel) {
        Stack<Boolean> stack = channel.attr(LAST_AUTO_READ).get();
        if (stack == null) {
            stack = new Stack<>();
            channel.attr(LAST_AUTO_READ).set(stack);
        }
        stack.push(channel.config().isAutoRead());
        channel.config().setAutoRead(false);
    }

    /**
     * Restore auto-read to its previous state (popped from the stack).
     */
    public static void restoreAutoRead(Channel channel) {
        Stack<Boolean> stack = channel.attr(LAST_AUTO_READ).get();
        if (stack == null || stack.isEmpty()) {
            throw new IllegalStateException("Tried to restore auto read, but it was never disabled");
        }
        if (channel.config().isAutoRead()) {
            throw new IllegalStateException("Race condition detected: Auto read has been enabled somewhere else");
        }
        channel.config().setAutoRead(stack.pop());
    }
}
