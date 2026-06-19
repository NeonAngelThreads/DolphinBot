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

package org.angellock.impl.api.handlers;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.commands.AbstractCommandSerializer;
import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.commands.CommandSpec;
import org.angellock.impl.events.AbstractEventProcessor;
import org.angellock.impl.api.events.game.PlayerChatEvent;
import org.angellock.impl.util.PlainTextSerializer;
import org.angellock.impl.util.XinCommandSerializer;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerChatPacket;

public class ChatCommandHandler extends AbstractEventProcessor<ClientboundPlayerChatPacket> {
    private final AbstractCommandSerializer serializer;
    public ChatCommandHandler(CommandSpec commands, AbstractRobot robotPlayer) {
        this.serializer = new XinCommandSerializer(robotPlayer);
        this.addExtraAction((chat) -> {
            PlainTextSerializer nameSerializer = new PlainTextSerializer();
            String sender = nameSerializer.serialize(chat.getName());
            String commandMsg = chat.getContent();
            CommandResponse meta = serializer.serialize(commandMsg, sender);
            if (meta == null){
                robotPlayer.callHandleableEvent(new PlayerChatEvent(sender, commandMsg));
            } else {
                commands.executeCommand(meta);
            }
        });
    }

    @Override
    protected boolean isTargetPacket(Packet packet) {
        return (packet instanceof ClientboundPlayerChatPacket);
    }
}
