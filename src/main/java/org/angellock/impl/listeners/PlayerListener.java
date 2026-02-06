/*
 * This file is a part of DolphinBot, see <https://github.com/NeonAngelThreads/DolphinBot>
 *
 *     Copyright (C) 2025-2026 NeonAngelThreads
 *
 *     This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 *     published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should
 *     have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc.,
 *      51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact with me> Bilibili space: https://space.bilibili.com/386644641
 */

package org.angellock.impl.listeners;

import org.angellock.impl.events.IListener;
import org.angellock.impl.events.annotations.EventHandler;
import org.angellock.impl.events.types.EntityMovedEvent;
import org.angellock.impl.events.types.JoinedGameEvent;
import org.angellock.impl.events.types.KeepAliveEvent;
import org.angellock.impl.util.ConsoleTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class PlayerListener implements IListener {

    Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&aPlayerListener"));
    @EventHandler
    public void onEmerge(EntityMovedEvent event) {
        log.info(ConsoleTokens.colorizeText("&4"+event));
    }

    @EventHandler
    public void onKeepAliveSent(JoinedGameEvent event) {
        log.info(ConsoleTokens.colorizeText("&b&lSuccessfully logged-in to server world."));
        log.info(ConsoleTokens.colorizeText("&7Logged-in At Position &b({})"), event.getPosition().toString());
    }
}
