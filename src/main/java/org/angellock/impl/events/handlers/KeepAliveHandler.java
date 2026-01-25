package org.angellock.impl.events.handlers;

import org.angellock.impl.events.AbstractEventProcessor;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.packet.common.clientbound.ClientboundKeepAlivePacket;

public class KeepAliveHandler extends AbstractEventProcessor<ClientboundKeepAlivePacket> {
    @Override
    protected boolean isTargetPacket(Packet packet) {
        return (packet instanceof ClientboundKeepAlivePacket);
    }
}
