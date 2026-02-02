package org.angellock.impl.commands.executors;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.commands.ICommandAction;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ReloadCommandExecutor implements ICommandAction {
    private static final Logger log = LoggerFactory.getLogger(ReloadCommandExecutor.class);

    @Override
    public void onCommand(CommandResponse responseEntity) {
        int botAmount = BotManager.bots().size();
        if (botAmount == 1){
            String pluginName = responseEntity.getCommandList()[1];
            log.info("Reloading plugin {}", pluginName);

            AbstractRobot bot = BotManager
                    .bots()
                    .values()
                    .iterator()
                    .next();

            PluginManager pm = bot.getPluginManager();
            pm.reloadPlugin(bot, pluginName);

        }
    }

}
