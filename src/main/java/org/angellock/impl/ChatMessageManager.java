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

package org.angellock.impl;

import lombok.Getter;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TranslatableUtil;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Queue;

public class ChatMessageManager{
    protected static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&7ChatMessageManager"));
    @Getter
    private final Queue<String> chatMessageQueue = new ArrayDeque<>();
    private final RobotPlayer instance;

    public ChatMessageManager(RobotPlayer bot) {
        this.instance = bot;
    }

    public void putMessage(String msg){
        if (!msg.isEmpty()) {
            this.chatMessageQueue.offer(msg);
        }
    }

    public boolean pollMessage() throws Exception{
        String removal = this.chatMessageQueue.poll();
        if(removal != null) {
            if (instance.getSession().isConnected()) {
                this.sendMessagePacket(removal);
            }
            return true;
        }
        return false;
    }

    private boolean isCommand(String msg){
        return msg.startsWith("/");
    }

    private void sendMessagePacket(String message){
        if (!this.isCommand(message)) {
            MinecraftPacket msgPacket = new ServerboundChatPacket(message, Instant.now().toEpochMilli(), System.currentTimeMillis(), null, 0, new BitSet(), 0);
            log.info(instance.getBotLabel(), TranslatableUtil.getFormattedMessage(EnumSystemEvents.CHAT_MESSAGE_SEND, message));
            this.instance.sendPacket(msgPacket);
        } else {
            try {
                boolean valid = this.instance.commandManager.callCommand(message, instance);
                if (!valid) {
                    MinecraftPacket cmd = new ServerboundChatCommandPacket(message.replaceFirst("/", ""));
                    log.info(instance.getBotLabel(), TranslatableUtil.getFormattedMessage(EnumSystemEvents.CHAT_COMMAND_SEND, message));
                    this.instance.sendPacket(cmd);
                }
            } catch (Exception e) {
                log.warn(instance.getBotLabel(), "An exception occurred: &7{}", e.getMessage());
            }
        }
    }

    public void sendCommand(String stringCommand){
        this.instance.sendPacket(new ServerboundChatCommandPacket(stringCommand));
    }
}
