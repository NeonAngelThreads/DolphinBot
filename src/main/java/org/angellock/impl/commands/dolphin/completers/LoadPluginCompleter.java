package org.angellock.impl.commands.dolphin.completers;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.commands.ICommandCompleter;
import org.angellock.impl.managers.BotManager;

import java.util.ArrayList;
import java.util.List;

public class LoadPluginCompleter implements ICommandCompleter {

    @Override
    public List<String> complete(String[] cmdList) {

        AbstractRobot bot = BotManager
                .bots()
                .values()
                .iterator()
                .next();
        String[] plugins = bot.getPluginManager().listPlugins();

        List<String> list = new ArrayList<>();
        if (cmdList[0].equalsIgnoreCase("load")) {
            for (String plugin : plugins) {
                if (plugin.contains(cmdList[1])) {
                    list.add(plugin);
                }
            }
        }
        else {
            String expectBot = cmdList[2].toLowerCase();
            bot = BotManager.bots().get(expectBot);
            if (bot != null){
                list.addAll(BotManager.bots().keySet());
            }
        }
        return list;
    }
}
