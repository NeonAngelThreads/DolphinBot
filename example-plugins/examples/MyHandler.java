package org.angellock.impl.extensions.examples;

import org.angellock.impl.events.AbstractEventProcessor;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerChatPacket;

public class MyHandler extends AbstractEventProcessor<ClientboundPlayerChatPacket> {
    @Override
    protected boolean isTargetPacket(Packet packet) {
        return (packet instanceof ClientboundPlayerChatPacket);
    }
}
