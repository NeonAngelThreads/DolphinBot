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

package org.angellock.impl.events.handlers;

import net.kyori.adventure.text.TranslatableComponent;
import org.angellock.impl.AbstractRobot;
import org.angellock.impl.events.IDisconnectListener;
import org.angellock.impl.util.PlainTextSerializer;
import org.geysermc.mcprotocollib.network.event.session.DisconnectedEvent;

public class DisconnectReasonHandler implements IDisconnectListener {
    private final AbstractRobot bot;

    public DisconnectReasonHandler(AbstractRobot abstractRobot) {
        this.bot = abstractRobot;
    }

    @Override
    public void onDisconnect(DisconnectedEvent event) {
        PlainTextSerializer serializer = new PlainTextSerializer();
        String text = serializer.serialize(event.getReason());
        if (text.isBlank()) {
            TranslatableComponent component = ((TranslatableComponent) event.getReason());
            text = String.format("%s: %s", component.key(), serializer.serialize(component.arguments().get(0).asComponent()));
        }
        this.bot.onQuit(text);
    }
}
