package org.angellock.impl;

import org.angellock.impl.commands.CommandBuilder;
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

        this.commands.register(new CommandBuilder().withName("reload").allowedUsers(this.owners).build((act) -> {
            long timeElapse = System.currentTimeMillis();
            this.pluginManager.reloadPlugin(this, act.getCommandList()[1].toLowerCase());
            long time = (System.currentTimeMillis() - timeElapse);
            this.getMessageManager().putMessage("[INFO]操作已成功完成。耗时" + time + "ms");

        }));

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
        this.connectTime = System.currentTimeMillis();
        log.info(ConsoleTokens.colorizeText("&7[{}] &2Connection was established!"), this.getProfileName());
    }

    @Override
    public void onQuit(String reason) {
        long millis = System.currentTimeMillis() - this.connectTime;
        log.info(ConsoleTokens.colorizeText("[{}] &7Session Duration: &f{}ms"), this.getProfileName(), millis);
        log.info(ConsoleTokens.colorizeText("&4Disconnected from the server!"));
        log.info(ConsoleTokens.colorizeText("&6Reason: &d" + reason));
        if (this.config.getConfigValue("auto-reconnecting").equals("true")){
            log.info(ConsoleTokens.colorizeText("&9Trying to reconnect to the server..."));

            if (reason.contains("验证")){
                this.isByPassedVerification = false;
            }
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
        log.info(ConsoleTokens.colorizeText("&l&bAttempt to join to the server &3"+ this.server+':'+this.port +". &bWaiting for server establishing the connection..."));
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
