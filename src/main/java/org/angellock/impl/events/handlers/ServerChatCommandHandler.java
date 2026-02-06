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

package org.angellock.impl.events.handlers;

import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.commands.CommandSerializer;
import org.angellock.impl.commands.CommandSpec;
import org.angellock.impl.events.AbstractEventProcessor;
import org.angellock.impl.util.PlainTextSerializer;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;

public class ServerChatCommandHandler extends AbstractEventProcessor<ClientboundSystemChatPacket> {

    public ServerChatCommandHandler(CommandSpec commands) {
        this.addExtraAction((chatPacket -> {
            PlainTextSerializer componentSerializer = new PlainTextSerializer();
            String commandMsg = componentSerializer.serialize(chatPacket.getContent());
            CommandSerializer serializer = new CommandSerializer();
            CommandResponse meta = serializer.serialize(commandMsg);
            commands.executeCommand(meta);
        }));
    }

    @Override
    protected boolean isTargetPacket(Packet packet) {
        return (packet instanceof ClientboundSystemChatPacket);
    }
}
