package org.angellock.impl.events.handlers;

import org.angellock.impl.events.AbstractEventProcessor;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerInfoRemovePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerInfoUpdatePacket;

public class PlayerLogInfo {
    public static class RemoveHandler extends AbstractEventProcessor<ClientboundPlayerInfoRemovePacket> {
        @Override
        protected boolean isTargetPacket(Packet packet) {
            return (packet instanceof ClientboundPlayerInfoRemovePacket);
        }
    }

    public static class UpdateHandler extends AbstractEventProcessor<ClientboundPlayerInfoUpdatePacket> {
        @Override
        protected boolean isTargetPacket(Packet packet) {
            return (packet instanceof ClientboundPlayerInfoUpdatePacket);
        }
    }
}
