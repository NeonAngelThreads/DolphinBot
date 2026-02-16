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

package org.angellock.impl.extensions;

import net.kyori.adventure.text.TextComponent;
import org.angellock.impl.AbstractRobot;
import org.angellock.impl.EnumSystemEvents;
import org.angellock.impl.events.IDisconnectListener;
import org.angellock.impl.events.handlers.ContainerPacketHandler;
import org.angellock.impl.events.handlers.LoginHandler;
import org.angellock.impl.events.handlers.SystemChatHandler;
import org.angellock.impl.events.handlers.TitlePacketHandler;
import org.angellock.impl.plugin.AbstractPlugin;
import org.angellock.impl.state.Action;
import org.angellock.impl.state.LoginState;
import org.angellock.impl.state.LoginStateMachine;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TextComponentSerializer;
import org.angellock.impl.util.TimingUtil;
import org.angellock.impl.util.TranslatableUtil;
import org.angellock.impl.util.reason.KickReason;
import org.geysermc.mcprotocollib.network.event.session.SessionListener;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ContainerAction;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ContainerActionType;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.inventory.ServerboundContainerButtonClickPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.inventory.ServerboundContainerClickPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundSetCarriedItemPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundUseItemPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;

public class PlayerVerificationPlugin extends AbstractPlugin {
    protected int verifyTimes = 0;
    private boolean hasLoggedIn = false;
    private boolean inQueue = false;
    private AbstractRobot botInstance;
    protected static final Logger log = LoggerFactory.getLogger("AutoLogin");

    @Override
    public String getPluginName() {
        return "AutomaticVerify";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "AutomaticVerify";
    }

    @Override
    public void onDisable() {
        this.hasLoggedIn = false;
        this.inQueue = false;
        this.schedulerThread = null;
        this.getListeners().clear();
    }

    @Override
    public void onLoad() {

    }

    public void sendRegister(AbstractRobot entityBot){
        entityBot.sendPacket(new ServerboundChatCommandPacket("reg " + entityBot.getPassword() +" "+ entityBot.getPassword()));
    }

    @Override
    public void onEnable(AbstractRobot entityBot) {
        this.botInstance = entityBot;
        this.hasLoggedIn = false;

        LoginStateMachine stateMachine = new LoginStateMachine(LoginState.IDLE);

        Action verifyAction = new Action(entityBot) {
            @Override
            public void execute() {
                int var = 0;
                try {
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
                                stateMachine.currentState = LoginState.REGISTER;
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    entityBot.getSession().disconnect("Interrupted");
                    throw new RuntimeException();
                }
            }
        };
        Action registerAction = new Action(entityBot) {
            @Override
            public void execute() {
                entityBot.getPluginManager().loadAllPlugins(entityBot);
                entityBot.setBypassed(true);

                log.info(ConsoleTokens.colorizeText("&aRobot verification successfully passed, sending reg command!"));
                sendRegister(entityBot);
                verifyTimes = 0;
            }
        };
        Action joinAction = new Action(entityBot) {
            @Override
            public void execute() {
                entityBot.sendPacket(new ServerboundSetCarriedItemPacket(2));
                entityBot.sendPacket(new ServerboundUseItemPacket(
                        Hand.MAIN_HAND,
                        (int) Instant.now().toEpochMilli(),
                        0,
                        0
                ));
                inQueue = true;
            }
        };
        Action loginAction = new Action(entityBot) {
            @Override
            public void execute() {
                    try {
                        Thread.sleep((inQueue) ? 10000L : 2500L);
                        if (!entityBot.getSession().isConnected()){
                            return;
                        }

                        if (!hasLoggedIn) {
                            entityBot.sendPacket(new ServerboundChatCommandPacket("login " + entityBot.getPassword()));
                        }else if (entityBot.getServerGamemode() != GameMode.SURVIVAL){
                            stateMachine.currentState = LoginState.JOIN;
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
            }
        };

        stateMachine
                .source(LoginState.IDLE).whenReceive("§c§l离线玩家请注册").goal(LoginState.REGISTER, registerAction)
                    .and()
                    .whenReceive("§c§l离线玩家请登陆").goal(LoginState.LOGIN, loginAction)
                .source(LoginState.VERIFY).whenReceive("机器人验证已完毕").goal(LoginState.REGISTER, registerAction)
                .source(LoginState.REGISTER).whenReceive("已成功注册").goal(LoginState.JOIN, joinAction)
                .source(LoginState.LOGIN).whenReceive("§a§l登陆成功").goal(LoginState.JOIN, joinAction)
                .source(LoginState.JOIN).whenReceive("Position in queue").goal(LoginState.IDLE, null)
                .resetOnlyWhen(KickReason.HUMAN_VERIFICATION)
                .build();

        TextComponentSerializer serializer = new TextComponentSerializer();

        getListeners().add((IDisconnectListener) event -> {
            String s = serializer.serialize(event.getReason());
            if (s.contains("请关闭所有外挂后重新进入以完成验证！")){
                stateMachine.raise(KickReason.HUMAN_VERIFICATION);
                verifyAction.execute();
            }
        });

        getListeners().add(new ContainerPacketHandler().addExtraAction((packet -> {
            String title = ConsoleTokens.colorizeText(((TextComponent)packet.getTitle()).content().strip());
            log.info(ConsoleTokens.colorizeText("&7[Inventory] &7Container opened, with containerId: &9{}, &6Title: \"&l{}\""), packet.getContainerId(), title);
            entityBot.sendPacket(new ServerboundContainerButtonClickPacket(packet.getContainerId(), 4));
            entityBot.sendPacket(new ServerboundContainerClickPacket(packet.getContainerId(),
                    0,
                    4,
                    ContainerActionType.CLICK_ITEM,
                    (ContainerAction) () -> 0,
                    new ItemStack(0), new HashMap<>())
            );
        })));

        SessionListener chatListener = new SystemChatHandler().addExtraAction((packet) -> {
                    TextComponentSerializer componentSerializer = new TextComponentSerializer();
                    String msg = componentSerializer.serialize(packet.getContent());
                    stateMachine.check(msg);
        });
        SessionListener titleListener = new TitlePacketHandler().addExtraAction((titleTextPacket)->{
            String titleMsg = ((TextComponent) titleTextPacket.getText()).content();
            stateMachine.check(titleMsg);
        });
        getListeners().add(chatListener);

        getListeners().add(
                new LoginHandler().addExtraAction((loginPacket) -> {
                    entityBot.setServerGamemode(loginPacket.getCommonPlayerSpawnInfo().getGameMode());
                    getLogger().info(TranslatableUtil.getFormattedMessage(
                            EnumSystemEvents.SERVER_PLAYER_GAMEMODE,
                            entityBot.getProfileName(),
                            loginPacket.getCommonPlayerSpawnInfo().getGameMode().name()
                    ));
                })
        );

        getListeners().add(titleListener);
    }

    public void resetVerify(){
        this.botInstance.setBypassed(false);
    }

    private boolean isBypassed(){
        return this.botInstance.isByPassedVerification();
    }

}
