package org.angellock.impl.events.handlers;

import org.angellock.impl.events.AbstractEventProcessor;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.packet.login.serverbound.ServerboundLoginAcknowledgedPacket;

public class JoinedGameHandler extends AbstractEventProcessor<ServerboundLoginAcknowledgedPacket> {
    @Override
    protected boolean isTargetPacket(Packet packet) {
        return (packet instanceof ServerboundLoginAcknowledgedPacket);
    }
}
