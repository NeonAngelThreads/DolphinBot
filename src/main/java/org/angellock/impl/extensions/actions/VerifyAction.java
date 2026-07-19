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
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.api.state.LoginState;
import org.angellock.impl.api.state.LoginStateMachine;
import org.angellock.impl.api.state.StateAction;
import org.angellock.impl.events.EventPriority;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TimingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerifyAction extends StateAction {

    protected static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&9Verification"));
    AbstractRobot robot;
    private static int verifyTimes = 0;
    LoginStateMachine stateMachine;
    private boolean isByPassedVerification = true;
    public VerifyAction(LoginStateMachine stateMachine, RobotPlayer botInstance) {
        super(botInstance);
        this.stateMachine = stateMachine;
        this.robot = botInstance;
    }

    @Override
    public void execute() {
        int var = 0;
        try {
            while (true) {
                var = TimingUtil.getRandomDelay(TimingUtil.getRandomizer(), var);
                Thread.sleep(500L*(1+var));

                if (entityBot.getSession().isConnected()){
                    if (!isBypassed()) {
                        if(verifyTimes < 2){
                            verifyTimes++;
                            entityBot.getSession().disconnect("Bypassing");
                        }
                        log.info(ConsoleTokens.colorizeText("&7正在进行人机验证..."));
                        if (System.currentTimeMillis() - entityBot.getConnectTime() > 10700L) {
                            log.info(ConsoleTokens.colorizeText("&a机器人验证已完毕."));
                            stateMachine.setState(LoginState.REGISTER);
                            log.info(ConsoleTokens.colorizeText("&aRobot verification successfully passed, sending reg command!"));
                            return;
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            entityBot.getSession().disconnect("Interrupted");
            throw new RuntimeException();
        }
    }
    public void resetVerify(){
        this.isByPassedVerification = false;
    }

    private boolean isBypassed(){
        return this.isByPassedVerification;
    }

    public static void setVerifyTimes(int verifyTimes) {
        VerifyAction.verifyTimes = verifyTimes;
    }
}
