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

package org.angellock.impl.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import lombok.Getter;
import org.angellock.impl.AbstractRobot;
import org.angellock.impl.EnumSystemEvents;
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.api.events.MessageBroadcastEvent;
import org.angellock.impl.api.events.NotificationBroadcastEvent;
import org.angellock.impl.extensions.Plugins;
import org.angellock.impl.plugin.AbstractPlugin;
import org.angellock.impl.plugin.PluginManager;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.ProxyObject;
import org.angellock.impl.util.TranslatableUtil;
import org.angellock.impl.util.strings.JsonStrings;
import org.geysermc.mcprotocollib.network.ProxyInfo;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.*;

public class BotManager extends ResourceHelper {
    private static final Logger log = LoggerFactory.getLogger("BotManager");
    private static final Map<String, RobotPlayer> bots = new HashMap<>();
    @Getter
    private static final TranslatableUtil systemEventLogger = new TranslatableUtil();
    private final Gson gson = new GsonBuilder()
                                    .serializeNulls()
                                    .create();
    private final ConfigManager botConfigHelper;
    private static File globalPluginDir;
    public BotManager(@Nullable String defaultPath, String fileType, ConfigManager botConfigHelper) {
        super(defaultPath, fileType);
        this.botConfigHelper = botConfigHelper;
    }

    public BotManager globalPluginManager(@Nullable String pluginDir) {
        return globalPluginManager(pluginDir == null ? null : new File(pluginDir));
    }

    public BotManager globalPluginManager(@Nullable File pluginDir) {
        if (pluginDir == null || !pluginDir.exists() || !pluginDir.isDirectory()) {
            log.warn(ConsoleTokens.colorizeText("&eThe plugin folder was invalid or not existed: &c{}, &6Trying to locate the fallback directory: &d{}plugins."),
                    pluginDir, getBaseConfigRoot());
            globalPluginDir = new File(getBaseConfigRoot(), "plugins");
        } else {
            globalPluginDir = pluginDir;
        }
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
        if (commandLinePlayerName != null) {
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

    public static void registerNew(String key, RobotPlayer botObject){
        log.info(ConsoleTokens.colorizeText("&5Registering bot: &o&1[&9name=&b{}&9, password=&b{}&9, owner=&b{}&1]"),
                botObject.getInfoHelper().getName(),
                botObject.getInfoHelper().getPassword(),
                botObject.getInfoHelper().getOwners());
        bots.put(key, botObject);
    }

    private void registerBot(Map<String, JsonElement> profiles, String name) {
        Map<String, JsonElement> profile = profiles.get(name).getAsJsonObject().asMap();
        String botName = profile.get("name").getAsString();
        String password = profile.get("password").getAsString();
        List<JsonElement> owners = profile.get("owner").getAsJsonArray().asList();
        log.info(ConsoleTokens.colorizeText("&5Registering bot: &o&1[&9name=&b{}&9, password=&b{}&9, owner=&b{}&1]"), botName, password, JsonStrings.toListString(owners));

        List<JsonElement> plugins = profile.get("enabled_plugins").getAsJsonArray().asList();
        List<AbstractPlugin> pluginList = new ArrayList<>();
        for(JsonElement element: plugins){
            pluginList.add(Plugins.getPluginFromString(element.getAsString()));
        }

        ProxyObject proxySetting = this.gson.fromJson(profile.get("proxy"), ProxyObject.class);

        AbstractRobot botInst;
        ProxyInfo proxyInfo = null;
        if (proxySetting != null && proxySetting.isEnabled()) {
            ProxyObject.Info info = proxySetting.getInfo();
            if (info.isValid()) {
                log.info(TranslatableUtil.getFormattedMessage(EnumSystemEvents.PROXY_CONFIG_LOAD, info));

                proxyInfo = new ProxyInfo(info.getType(),
                        new InetSocketAddress(info.getAddress(), info.getPort()),
                        info.getUsername(),
                        info.getPassword()
                );
            } else {
                log.info(TranslatableUtil.getFormattedMessage(EnumSystemEvents.PROXY_CONFIG_INVALID, info));
            }
        }

        botInst = new RobotPlayer(this.botConfigHelper, new PluginManager(globalPluginDir))
                .withName(botName)
                .withPassword(password)
                .withDefaultPlugins(pluginList)
                .withProfileName(name)
                .withBotManager(this)
                .withOwners(owners)
                .enableProxy(proxyInfo)
                .buildProtocol();
        registerNew(name, (RobotPlayer) botInst);
    }

    private void registerBot(String username, String password, String owner){
        String[] owners = this.escapeArrayCommandLine(owner);

        AbstractRobot botInst = new RobotPlayer(this.botConfigHelper, new PluginManager(globalPluginDir))
                .withName(username)
                .withPassword(password)
                .withOwners(owners)
                .buildProtocol();

        for (Plugins plugins: Plugins.values()){
            botInst
                    .getPluginManager()
                    .getDefaultPlugins()
                    .add(plugins
                            .getPlugin()
                    );
        }
        registerNew(username, (RobotPlayer) botInst);
    }

    @SuppressWarnings("unused")
    public static void dispatchMessages(List<String> msgQueue){
        List<RobotPlayer> randomBots = new ArrayList<>(bots.values());
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

    @SuppressWarnings("unused")
    public void distributeNotification(String handleableMessage){
        for (RobotPlayer bot : bots.values()) {
            bot.callHandleableEvent(new NotificationBroadcastEvent(handleableMessage));
        }
    }

    @SuppressWarnings("unused")
    public static @Nullable RobotPlayer getBotByName(String name){
        return bots.get(name);
    }

    public static @Nullable RobotPlayer getBotByProfileName(String name){
        for (RobotPlayer value : bots().values()) {
            if(value.getProfileName().equals(name) || value.getInfoHelper().getName().equals(name)){
                return value;
            }
        }
        return null;
    }

    public static Map<String, RobotPlayer> bots() {
        return bots;
    }

    public void startAll(){
        for (RobotPlayer bot: bots.values()){
            bot.scheduleConnect();
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
