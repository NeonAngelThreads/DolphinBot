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
 *    License for more details.
 *
 *    You should have received a copy of the GNU General Public License along with this program.  If not, see
 *    <https://www.gnu.org/licenses/>.
 *
 * https://space.bilibili.com/386644641
 */

package org.angellock.impl.api.handlers;

import net.kyori.adventure.key.Key;
import org.angellock.impl.AbstractRobot;
import org.angellock.impl.events.AbstractEventProcessor;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.HandPreference;
import org.geysermc.mcprotocollib.protocol.data.game.setting.ChatVisibility;
import org.geysermc.mcprotocollib.protocol.data.game.setting.ParticleStatus;
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundClientInformationPacket;
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundCustomPayloadPacket;
import org.geysermc.mcprotocollib.protocol.packet.login.clientbound.ClientboundLoginFinishedPacket;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Mirrors ClientHandshakePacketListenerImpl.handleLoginSuccess: right after the login
 * phase finishes, a vanilla client enters the configuration phase and sends
 *   ServerboundLoginAcknowledgedPacket   (handled automatically by MCProtocolLib's ClientListener)
 *   ServerboundCustomPayloadPacket       ("minecraft:brand" = "vanilla")
 *   ServerboundClientInformationPacket
 * in this exact order. Since MCProtocolLib's built-in ClientListener is registered before
 * this listener, our packets go out right after LoginAcknowledged, matching vanilla.
 * A client that never announces its brand / client information during configuration is
 * trivially distinguishable from a vanilla client.
 */
public class ClientLoginFinishedHandler extends AbstractEventProcessor<ClientboundLoginFinishedPacket> {
    private static final byte[] VANILLA_BRAND_PAYLOAD = buildBrandPayload("vanilla");

    public ClientLoginFinishedHandler(AbstractRobot bot) {
        this.addExtraAction(packet -> {
            bot.sendPacket(new ServerboundCustomPayloadPacket(Key.key("minecraft", "brand"), VANILLA_BRAND_PAYLOAD));
            bot.sendPacket(new ServerboundClientInformationPacket(
                    "en-us",
                    bot.config().getMaxChunkView(),
                    ChatVisibility.FULL,
                    true,
                    new ArrayList<>(),
                    HandPreference.RIGHT_HAND,
                    true,
                    true,
                    ParticleStatus.ALL
            ));
        });
    }

    /** BrandPayload is a single UTF string on the wire: VarInt length prefix + bytes. */
    private static byte[] buildBrandPayload(String brand) {
        byte[] utf = brand.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream out = new ByteArrayOutputStream(5 + utf.length);
        int length = utf.length;
        do {
            int b = length & 0x7F;
            length >>>= 7;
            out.write(b | (length != 0 ? 0x80 : 0));
        } while (length != 0);
        out.write(utf, 0, utf.length);
        return out.toByteArray();
    }

    @Override
    protected boolean isTargetPacket(Packet packet) {
        return (packet instanceof ClientboundLoginFinishedPacket);
    }
}
