package org.angellock.impl.events.handlers;

import org.angellock.impl.events.AbstractEventProcessor;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;

public class SystemChatHandler extends AbstractEventProcessor<ClientboundSystemChatPacket> {
    @Override
    protected boolean isTargetPacket(Packet packet) {
        return (packet instanceof ClientboundSystemChatPacket);
    }
}
