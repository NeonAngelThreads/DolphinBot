/*
 *  DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 *  Copyright (C) 2025 NeonAngelThreads (https://github.com/NeonAngelThreads)
 *
 *     This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 *     License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any
 *     later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 *     implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 *     License for more details. You should have received a copy of the GNU General Public License along with this
 *     program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  https://space.bilibili.com/386644641
 */

package org.angellock.impl.events.handlers;

import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.commands.CommandSerializer;
import org.angellock.impl.commands.CommandSpec;
import org.angellock.impl.events.AbstractEventProcessor;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.PlainTextSerializer;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemChatHandler extends AbstractEventProcessor<ClientboundSystemChatPacket> {
    @Override
    protected boolean isTargetPacket(Packet packet) {
        return (packet instanceof ClientboundSystemChatPacket);
    }
}
