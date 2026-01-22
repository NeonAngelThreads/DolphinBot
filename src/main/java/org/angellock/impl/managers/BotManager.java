package org.angellock.impl.managers;

import com.google.gson.JsonElement;
import org.angellock.impl.AbstractRobot;
import org.angellock.impl.ChatMessageManager;
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.extensions.Plugins;
import org.angellock.impl.providers.Plugin;
import org.angellock.impl.providers.PluginManager;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.strings.JsonStrings;
import org.angellock.impl.win32terminal.AnsiEscapes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BotManager extends ResourceHelper {
    private static final Logger log = LoggerFactory.getLogger("BotManager");
    private final Map<String, RobotPlayer> bots = new HashMap<>();
    //    private final ScheduledExecutorService terminal = Executors.newScheduledThreadPool(1);
    private Thread terminalInput;
    private final ConfigManager botConfigHelper;
    private PluginManager pluginManager;
    public BotManager(@Nullable String defaultPath, String fileType, ConfigManager botConfigHelper) {
        super(defaultPath, fileType);
        this.botConfigHelper = botConfigHelper;
    }

    public BotManager globalPluginManager(String pluginDir){
        this.pluginManager = new PluginManager(pluginDir);
        return this;
    }

    public String[] escapeArrayCommandLine(String option) {
        if (option != null) {
            return option.replaceAll("\"", "").split(";");
        }
        log.warn(ConsoleTokens.colorizeText("&eBot profiles argument&3('--profiles')&e was &6NOT-SET&e, it will load all of bots inside profile config file."));
        return new String[0];
    }
    public BotManager loadProfiles(String profileString){
        String commandLinePlayerName = (String)this.botConfigHelper.getConfigValue("username");
        String commandLinePWD = (String)this.botConfigHelper.getConfigValue("password");
        String commandLineOwner = (String)this.botConfigHelper.getConfigValue("owner");
        if (commandLinePlayerName != null && commandLinePWD != null) {
            this.registerBot(commandLinePlayerName, commandLinePWD, commandLineOwner);
            return this;
        }

        String[] profileKeys = this.escapeArrayCommandLine(profileString);
        log.info(ConsoleTokens.colorizeText("&bBot profiles was specified: &d{}"), Arrays.toString(profileKeys));

        Map<String, JsonElement> jsonObject = this.readJSONContent();
        Map<String, JsonElement> profiles = jsonObject.get("profiles").getAsJsonObject().asMap();

        if (profileKeys.length == 0) {
            for(String profileName: profiles.keySet()){
                registerBot(profiles, profileName);
            }
        }else {
            for (String name: profileKeys){
                registerBot(profiles, name);
            }
        }
        return this;
    }

    private void registerBot(Map<String, JsonElement> profiles, String name) {
        Map<String, JsonElement> profile = profiles.get(name).getAsJsonObject().asMap();
        String botName = profile.get("name").getAsString();
        String password = profile.get("password").getAsString();
        List<JsonElement> owners = profile.get("owner").getAsJsonArray().asList();
        log.info(ConsoleTokens.colorizeText("&5Registering bot: &o&1[&9name=&b{}&9, password=&b{}&9, owner=&b{}&1]"), botName, password, JsonStrings.toListString(owners));

        List<JsonElement> plugins = profile.get("enabled_plugins").getAsJsonArray().asList();
        List<Plugin> pluginList = new ArrayList<>();
        for(JsonElement element: plugins){
            pluginList.add(Plugins.getPluginFromString(element.getAsString()));
        }

        AbstractRobot botInst = new RobotPlayer(this.botConfigHelper, pluginManager)
                .withName(botName)
                .withPassword(password)
                .withDefaultPlugins(pluginList)
                .withProfileName(name)
                .withBotManager(this)
                .withOwners(owners)
                .buildProtocol();
        this.bots.put(name, (RobotPlayer) botInst);
    }

    private void registerBot(String username, String password, String owner){
        String[] owners = this.escapeArrayCommandLine(owner);
        log.info(ConsoleTokens.colorizeText("&5Registering bot: &o&1[&9name=&b{}&9, password=&b{}&9, owner=&b{}&1]"), username, password, Arrays.toString(owners));

        AbstractRobot botInst = new RobotPlayer(this.botConfigHelper, pluginManager)
                .withName(username)
                .withPassword(password)
                .withOwners(owners)
                .buildProtocol();
        for (Plugins plugins: Plugins.values()){
            botInst.getPluginManager().getDefaultPlugins().add(plugins.getPlugin());
        }
        this.bots.put(username, (RobotPlayer) botInst);
    }

    public void dispatchMessages(List<String> msgQueue){
        List<RobotPlayer> randomBots = new ArrayList<>(this.bots.values());
        Collections.shuffle(randomBots);
        Random random = new Random();
        RobotPlayer last = null;
        for (String string : msgQueue) {
            if (randomBots.isEmpty()) break;
            int i = random.nextInt(randomBots.size());
            RobotPlayer selected = randomBots.remove(i);

            if (last != null) {
                try {
                    Thread.sleep(70L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            selected.getMessageManager().putMessage(string);
            last = selected;
        }
    }

    public Map<String, RobotPlayer> getBots() {
        return bots;
    }

    public void startAll(){
        LineReader reader = AnsiEscapes.getReader();
        this.terminalInput = new Thread(() -> {
            try {
                while (true) {
                    String s = reader.readLine();
                    for (AbstractRobot dolphinBot : this.bots.values()) {
                        ChatMessageManager messageManager = dolphinBot.getMessageManager();
                        if (messageManager != null) {
                            dolphinBot.getMessageManager().putMessage(s);
                            break;
                        }
                    }
                }
            } catch (UserInterruptException w) {
                System.exit(0);
            } catch (Throwable e) {
                log.info(ConsoleTokens.colorizeText("&8Failed to send message: &7{}"), e.getLocalizedMessage());
            }
        });
        this.terminalInput.start();
        for (RobotPlayer bot: this.bots.values()){
            bot.scheduleConnect(0);
            while (bot.getServerGamemode() == GameMode.ADVENTURE){
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public String getFileName() {
        return "bot.profiles";
    }
}
