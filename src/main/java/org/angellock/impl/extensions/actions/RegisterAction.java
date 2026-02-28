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

package org.angellock.impl.extensions.actions;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.api.state.StateAction;
import org.angellock.impl.util.ConsoleTokens;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;

public class RegisterAction extends StateAction {
    private long antiSpam = 0L;
    public RegisterAction(AbstractRobot botInstance) {
        super(botInstance);
    }

    @Override
    public void execute() {
        if (System.currentTimeMillis() - this.antiSpam > 3000L){
            entityBot.setBypassed(true);
            entityBot.getMessageManager().sendCommand(String.format("reg %s %s", entityBot.getPassword(), entityBot.getPassword()));
            VerifyAction.setVerifyTimes(0);
            this.antiSpam = System.currentTimeMillis();
        }
    }
}
