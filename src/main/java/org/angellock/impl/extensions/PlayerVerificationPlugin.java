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
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.events.IDisconnectListener;
import org.angellock.impl.events.handlers.ContainerPacketHandler;
import org.angellock.impl.events.handlers.LoginHandler;
import org.angellock.impl.events.handlers.SystemChatHandler;
import org.angellock.impl.events.handlers.TitlePacketHandler;
import org.angellock.impl.extensions.actions.JoinAction;
import org.angellock.impl.extensions.actions.LoginAction;
import org.angellock.impl.extensions.actions.RegisterAction;
import org.angellock.impl.extensions.actions.VerifyAction;
import org.angellock.impl.plugin.AbstractPlugin;
import org.angellock.impl.api.state.StateAction;
import org.angellock.impl.api.state.LoginState;
import org.angellock.impl.api.state.LoginStateMachine;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TextComponentSerializer;
import org.angellock.impl.util.TranslatableUtil;
import org.angellock.impl.util.reason.KickReason;
import org.geysermc.mcprotocollib.network.event.session.SessionListener;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ContainerAction;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ContainerActionType;
import org.geysermc.mcprotocollib.protocol.data.game.item.HashedStack;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.inventory.ServerboundContainerButtonClickPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.inventory.ServerboundContainerClickPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class PlayerVerificationPlugin extends AbstractPlugin {

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
        this.schedulerThread = null;
        this.getListeners().clear();
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable(RobotPlayer entityBot) {

        LoginStateMachine stateMachine = entityBot.getLoginStateMachine();

        StateAction verifyAction = new VerifyAction(stateMachine, entityBot);
        StateAction registerAction = new RegisterAction(entityBot);
        StateAction joinAction = new JoinAction(entityBot);
        StateAction loginAction = new LoginAction(stateMachine, entityBot);

        stateMachine
                .source(LoginState.DISCONNECTED).whenReceive("离线玩家请注册").goal(LoginState.REGISTER, registerAction)
                    .and()
                    .whenReceive("离线玩家请登陆").goal(LoginState.LOGIN, loginAction)
                .source(LoginState.VERIFY).whenReceive("机器人验证已完毕").goal(LoginState.REGISTER, registerAction)
                .source(LoginState.REGISTER).whenReceive("已成功注册").goal(LoginState.JOIN, joinAction)
                .source(LoginState.LOGIN).whenReceive("登陆成功").goal(LoginState.JOIN, joinAction)
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
            log.info(entityBot.getBotLabel(), ConsoleTokens.colorizeText("&7[Inventory] &7Container opened, with containerId: &9{}, &6Title: \"&l{}\""), packet.getContainerId(), title);
            entityBot.sendPacket(new ServerboundContainerButtonClickPacket(packet.getContainerId(), 4));
            entityBot.sendPacket(new ServerboundContainerClickPacket(packet.getContainerId(),
                    0,
                    4,
                    ContainerActionType.CLICK_ITEM,
                    (ContainerAction) () -> 0,
                    null,
                    new HashMap<>())
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
}
