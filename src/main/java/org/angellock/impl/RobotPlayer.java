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
import lombok.Setter;
import org.angellock.impl.api.state.LoginState;
import org.angellock.impl.api.state.LoginStateMachine;
import org.angellock.impl.api.state.StateAction;
import org.angellock.impl.extensions.actions.JoinAction;
import org.angellock.impl.extensions.actions.LoginAction;
import org.angellock.impl.extensions.actions.RegisterAction;
import org.angellock.impl.extensions.actions.VerifyAction;
import org.angellock.impl.ingame.IPlayer;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.plugin.PluginManager;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TranslatableUtil;
import org.angellock.impl.util.math.Position;
import org.angellock.impl.util.reason.KickReason;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.data.game.entity.object.Direction;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundUseItemOnPacket;

import java.util.Optional;

public class RobotPlayer extends AbstractRobot implements IPlayer {
    private long connectTime;
    private long lastMsgTime = 0L;
    private final long msgDelay;
    @Setter
    private volatile boolean shouldReconnect = true;
    @Getter
    private final ChatMessageManager messageManager;
    @Getter
    private final LoginStateMachine loginStateMachine = new LoginStateMachine(LoginState.DISCONNECTED);
    @Setter
    protected Position loginPos = new Position();

    public RobotPlayer(ConfigManager configManager, PluginManager pluginManager) {
        super(configManager, pluginManager);

        LoginStateMachine stateMachine = this.loginStateMachine;
        StateAction registerAction = new RegisterAction(this);
        StateAction joinAction = new JoinAction(this);
        StateAction loginAction = new LoginAction(stateMachine, this);

        stateMachine
                .source(LoginState.DISCONNECTED).whenReceive("离线玩家请注册").goal(LoginState.REGISTER, registerAction)
                .and()
                .whenReceive("离线玩家请登陆").goal(LoginState.LOGIN, loginAction)
                .and()
                .whenReceive("登陆成功").goal(LoginState.JOIN, joinAction)
                .source(LoginState.VERIFY).whenReceive("机器人验证已完毕").goal(LoginState.REGISTER, registerAction)
                .source(LoginState.REGISTER).whenReceive("已成功注册").goal(LoginState.JOIN, joinAction)
                .source(LoginState.LOGIN).whenReceive("登陆成功").goal(LoginState.JOIN, joinAction)
                .source(LoginState.JOIN).whenReceive("Position in queue").goal(LoginState.IDLE, null)
                .resetOnlyWhen(KickReason.HUMAN_VERIFICATION)
                .build();
        this.messageManager = new ChatMessageManager(this);
        this.msgDelay = Long.parseLong(Optional
                .ofNullable(
                        this.globalConfig.getConfigValue("msg-send-delay"))
                .orElse("3000")
        );
    }

    @Override
    public void mainTickingEventLoop() {
        try {
            boolean connect = true;
            boolean shouldWait = false;

            while (true) {
                try {
                    Thread.sleep(20L);
                    if (!this.session.isConnected()){
                        this.connectDuration = System.currentTimeMillis();
                        break;
                    } else if (connect) {
                        if (System.currentTimeMillis() - this.connectDuration > 100L){
                            this.pluginManager.loadAllPlugins(this);
                            connect = false;
                        }
                    } else if (!shouldWait) {
                        if (this.getMessageManager().pollMessage()) {
                            this.lastMsgTime = System.currentTimeMillis();
                            shouldWait = true;
                        }
                    } else if (canSendMessages()) {
                        shouldWait = false;
                    }
                }
                catch (InterruptedException e){
                    continue;
                } catch (Throwable e) {
                    TranslatableUtil.warnTranslatableOf(EnumSystemEvents.PACKET_ERROR, (Object) e.getStackTrace());
                }
            }
        } finally {
            this.session.disconnect("");
            if (BotManager.getBotByProfileName(getProfileName()) != null){
                scheduleReconnect();
            }
        }
    }

    @Override
    public boolean canSendMessages() {
        long t = System.currentTimeMillis();
        return t - lastMsgTime > msgDelay;
    }

    @Override
    public void onJoin() {
        log.info(this.getBotLabel(), TranslatableUtil.getFormattedMessage(EnumSystemEvents.SERVER_CONNECTION_ESTABLISHED, this.getProfileName()));
    }

    @Override
    public void onQuit(String reason) {
        long millis = System.currentTimeMillis() - this.connectTime;
        log.info(this.getBotLabel(), ConsoleTokens.colorizeText("[{}] &7Session Duration: &f{}ms"), this.getProfileName(), millis);
        log.info(this.getBotLabel(), TranslatableUtil.getFormattedMessage(EnumSystemEvents.DISCONNECT, reason));
        this.getPluginManager().disableAllPlugins(this);
        this.getSession().getChannel().close();
        this.getSession().getChannel().deregister();
        this.getSession().getChannel().closeFuture();
        TranslatableUtil.infoTranslatableOf(EnumSystemEvents.DOLPHIN_TIMING_RESET);
        BotManager.bots().put(getProfileName(), this);
    }

    @Override
    public void onKicked(KickReason reason) {

    }

    @Override
    public void onPreLogin() {
        while (!this.shouldReconnect) {
            try {Thread.sleep(100L);
            } catch (InterruptedException ignored) {}
        }
        this.connectTime = System.currentTimeMillis();
        log.info(this.getBotLabel(), TranslatableUtil.getFormattedMessage(EnumSystemEvents.CONNECT, this.getInfoHelper().getServer(), String.valueOf(this.getInfoHelper().getPort())));
    }

    @Override
    public double getDistanceFromOthers(IPlayer player) {
        return this.getPosition().getDistance(player.getPosition());
    }

    @Override
    public Position getPosition() {
        return this.loginPos;
    }

    @Override
    public void interactBlock(double x, double y, double z) {
        this.interactBlock((int) x, (int) y, (int) z, (int) System.currentTimeMillis());
    }

    public void interactBlock(int x, int y, int z, int i3) {
        this.sendPacket(new ServerboundUseItemOnPacket(Vector3i.from(x, y, z), Direction.NORTH, Hand.MAIN_HAND, 0f, 0f, 0f, false, false, i3));
    }
}
