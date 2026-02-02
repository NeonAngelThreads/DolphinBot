package org.angellock.impl;

import org.angellock.impl.util.ConsoleTokens;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Queue;

public class ChatMessageManager{
    protected static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&7ChatMessageManager"));
    private final Queue<String> chatMessageQueue = new ArrayDeque<>();
    private final AbstractRobot instance;

    public ChatMessageManager(AbstractRobot bot) {
        this.instance = bot;
    }

    public void putMessage(String msg){
        this.chatMessageQueue.offer(msg);
    }

    public boolean pollMessage() {
        String removal = this.chatMessageQueue.poll();
        if(removal != null) {
            this.sendMessagePacket(removal);
            return true;
        }
        return false;
    }

    private boolean isCommand(String msg){
        return msg.startsWith("/");
    }

    private void sendMessagePacket(String message){
        if (!this.isCommand(message)) {
            MinecraftPacket msgPacket = new ServerboundChatPacket(message, Instant.now().toEpochMilli(), System.currentTimeMillis(), null, 0, new BitSet());
            log.info(ConsoleTokens.colorizeText("&7Sending in-game chat message: &b&l&o{}"), message);
            this.instance.sendPacket(msgPacket);
        } else {
            this.instance.commandManager.callCommand(message);
        }
    }

    public Queue<String> getChatMessageQueue() {
        return chatMessageQueue;
    }
}
