package org.angellock.impl.commands.executors;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.commands.ICommandAction;
import org.angellock.impl.commands.terminal.TerminalCommand;
import org.angellock.impl.managers.BotManager;
import org.angellock.impl.managers.TerminalCommandManager;
import org.angellock.impl.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class LoadCommandExecutor implements ICommandAction {
    @Override
    public void onCommand(CommandResponse responseEntity) {
        int botAmount = BotManager.bots().size();
        String pluginName = responseEntity.getCommandList()[1];

        if (botAmount == 1){
            AbstractRobot bot = BotManager
                    .bots()
                    .values()
                    .iterator()
                    .next();

            hotLoad(bot, pluginName);
        }
        else {
            String expectBot = responseEntity.getCommandList()[2];
            if (expectBot != null){
                AbstractRobot bot = BotManager
                        .bots()
                        .get(expectBot);

                hotLoad(bot, pluginName);
            }
            else {
                TerminalCommandManager.log().error("You should specify which bot you want to apply to, usage: /load <plugin name> [#bot1 | #bot2 ...]");
            }
        }
    }

    private static void hotLoad(AbstractRobot bot, String pluginName) {
        PluginManager pm = bot.getPluginManager();
        pm.loadPlugin(bot, new File(pm.getPluginFolder(), pluginName + ".jar"));
    }
}
