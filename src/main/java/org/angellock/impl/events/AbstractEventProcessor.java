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

package org.angellock.impl.events;

import org.angellock.impl.events.bukkit.ActiveListener;
import org.angellock.impl.events.bukkit.Event;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.util.ConsoleTokens;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.PacketErrorEvent;
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractEventProcessor<T extends MinecraftPacket> extends SessionAdapter {
    private static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&l&9PacketHandlers"));
    protected long time_elapse = System.currentTimeMillis();
    private final long DELAY;
    protected List<IActions<T>> actionList = new ArrayList<>();

    private final boolean showWarn = ConfigManager.getCoreSettings().getDebugSettings().isShowWarns();
    protected IActions<T> preAction = (T) -> {
    };

    public AbstractEventProcessor(long filterDelay){
        this.DELAY = filterDelay;
    }
    public AbstractEventProcessor(){
        this(0);
    }
    public AbstractEventProcessor(IActions<T> action, long filterDelay){
        this(filterDelay);
        this.actionList.add(action);
    }
    public AbstractEventProcessor(IActions<T> action){
        this(action, 0);
    }

    @Override
    public void packetReceived(Session session, Packet packet){
        if (System.currentTimeMillis() - this.time_elapse < this.DELAY){
            return;
        }
        try {
            if(packet != null && this.isTargetPacket(packet)){
                T packet1 = (T) packet;
                this.preAction.onAction(packet1);
                for (IActions<T> reacts : this.actionList) {
                    reacts.onAction(packet1);
                }
            }
        } catch (ClassCastException omit) {
            return;
        } catch (IllegalArgumentException e) {
            log.warn(ConsoleTokens.colorizeText("&6 {}"), e.getLocalizedMessage());
        } catch (Throwable throwable) {
            log.warn(ConsoleTokens.colorizeText("&8 {}"), throwable.toString());
        }
    }

    @Override
    public void packetError(PacketErrorEvent event) {
        if (this.showWarn) {
            log.warn(ConsoleTokens.colorizeText("&eA packet error was detected: &7At event &6" + event));
            log.error(ConsoleTokens.colorizeText("&7" + event.getCause().toString()));
        }
        event.setSuppress(true);
    }

    protected void dispatch(Event event) {
        HandlerMapper mapper = event.getMapper();
        for (ActiveListener listener : mapper.getRegisteredListenersInOrder()) {
            try {
                listener.call(event);
            } catch (Throwable throwable) {
                log.error(ConsoleTokens.colorizeText("&6Could not pass event &7{}"), throwable.getClass(), throwable);
            }
        }
    }

    protected abstract boolean isTargetPacket(Packet packet);
    public SessionAdapter addExtraAction(IActions<T> action){
        this.actionList.add(action);
        return this;
    }
}
