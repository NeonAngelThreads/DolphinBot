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
import org.angellock.impl.api.state.LoginState;
import org.angellock.impl.api.state.LoginStateMachine;
import org.angellock.impl.api.state.StateAction;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;

public class LoginAction extends StateAction {
    LoginStateMachine stateMachine;
    public LoginAction(LoginStateMachine stateMachine, AbstractRobot botInstance) {
        super(botInstance);
        this.stateMachine = stateMachine;
    }

    @Override
    public void execute() {

        if (!entityBot.getSession().isConnected()){
            return;
        }
        entityBot.getMessageManager().sendCommand("login " + entityBot.getPassword());
//        if (entityBot.getServerGamemode() != GameMode.SURVIVAL){
//            stateMachine.setState(LoginState.JOIN);
//        }
    }
}
