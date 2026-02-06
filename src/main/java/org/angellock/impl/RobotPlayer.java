/*
 *  DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 *  Copyright (C) 2025 NeonAngelThreads (https://github.com/NeonAngelThreads)
 *
 *     This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 *     License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any
 *     later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 *     implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 *     License for more details. You should have received a copy of the GNU General Public License along with this
 *     program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  https://space.bilibili.com/386644641
 */

package org.angellock.impl;

import org.angellock.impl.commands.CommandBuilder;
import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.ingame.IPlayer;
import org.angellock.impl.managers.ConfigManager;
import org.angellock.impl.plugin.PluginManager;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.math.Position;

import java.util.Optional;

public class RobotPlayer extends AbstractRobot {
    private long connectTime;
    private long lastMsgTime = 0L;
    private final long msgDelay;

    public RobotPlayer(ConfigManager configManager, PluginManager pluginManager) {
        super(configManager, pluginManager);

        this.msgDelay = Long.parseLong(Optional.ofNullable(this.config.getConfigValue("msg-send-delay")).orElse("3000"));
    }

    @Override
    public boolean canSendMessages() {
        long t = System.currentTimeMillis();
        if (t - lastMsgTime > msgDelay) {
            this.lastMsgTime = t;
            return true;
        }
        return false;
    }

    @Override
    public void onJoin() {
        log.info(ConsoleTokens.colorizeText("&7[{}] &2Connection was established!"), this.getProfileName());
    }

    @Override
    public void onQuit(String reason) {
        long millis = System.currentTimeMillis() - this.connectTime;
        log.info(ConsoleTokens.colorizeText("[{}] &7Session Duration: &f{}ms"), this.getProfileName(), millis);
        log.info(ConsoleTokens.colorizeText("&l&4Disconnected from the server! &6Reason: &d&n{}"), reason);
        if (this.config.getConfigValue("auto-reconnecting").equals("true")){
            log.info(ConsoleTokens.colorizeText("&9Trying to reconnect to the server..."));

            this.getPluginManager().disableAllPlugins(this);
            log.info(ConsoleTokens.colorizeText("&aTiming completed."));
            this.getSession().getChannel().close();
            this.getSession().getChannel().deregister();
            this.getSession().getChannel().closeFuture();
        }
    }

    @Override
    public void onKicked() {
        return;
    }

    @Override
    public void onPreLogin() {
        this.connectTime = System.currentTimeMillis();
        log.info(ConsoleTokens.colorizeText("&l&bAttempt to join to the server &3"+ this.infoHelper.getServer()+':'+this.infoHelper.getPort() +". &bWaiting for server establishing the connection..."));
    }

    @Override
    public double getDistanceFromOthers(IPlayer player) {
        return this.getPosition().getDistance(player.getPosition());
    }

    @Override
    public Position getPosition() {
        return this.loginPos;
    }
}
