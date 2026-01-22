package org.angellock.impl.extensions;

import net.kyori.adventure.text.TextComponent;
import org.angellock.impl.AbstractRobot;
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.events.handlers.ContainerPacketHandler;
import org.angellock.impl.events.handlers.LoginHandler;
import org.angellock.impl.events.handlers.SystemChatHandler;
import org.angellock.impl.events.handlers.TitlePacketHandler;
import org.angellock.impl.providers.AbstractPlugin;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TextComponentSerializer;
import org.angellock.impl.util.TimingUtil;
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
    private Thread autoLoginThread;
    private boolean hasLoggedIn = false;
    private boolean inQueue = false;
    private AbstractRobot botInstance;
    protected static final Logger log = LoggerFactory.getLogger("AutomaticVerify");
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
        this.autoLoginThread = null;
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

        if (this.schedulerThread == null) {
            this.schedulerThread = new Thread(() -> {
                int var = 0;
                while (true) {
                    try {

                        var = TimingUtil.getRandomDelay(entityBot.getRandomizer(), var);
                        Thread.sleep(500L*(1+var));

                        if (entityBot.getSession().isConnected()){
                            if (!this.isBypassed()) {
                                if(this.verifyTimes < 2){
                                    this.verifyTimes++;
                                    entityBot.getSession().disconnect("Bypassing");
                                }
                                log.info(ConsoleTokens.colorizeText("&7正在进行人机验证..."));
                                if (System.currentTimeMillis() - entityBot.getConnectTime() > 10700L) {
                                    log.info(ConsoleTokens.colorizeText("&a机器人验证已完毕."));
                                    entityBot.getPluginManager().loadAllPlugins(entityBot);
                                    this.botInstance.setBypassed(true);

                                    log.info(ConsoleTokens.colorizeText("&6=&aRobot verification successfully passed, sending reg command!&6="));
                                    this.sendRegister(entityBot);
                                    this.verifyTimes = 0;

                                    Thread.sleep(6000L);
                                    entityBot.getSession().disconnect("Exiting After Registered");

                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        entityBot.getSession().disconnect("Interrupted");
                        throw new RuntimeException();
                    }
                }
            });
        }

        if (this.autoLoginThread == null) {
            this.autoLoginThread = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep((this.inQueue) ? 10000L : 2500L);
                        if (!entityBot.getSession().isConnected()){
                            break;
                        }

//                        log.info(entityBot.getServerGamemode().name());
                        if (!this.hasLoggedIn) {
                            entityBot.sendPacket(new ServerboundChatCommandPacket("login " + entityBot.getPassword()));
                        }else if (this.botInstance.getServerGamemode() != GameMode.SURVIVAL){
                            entityBot.sendPacket(new ServerboundSetCarriedItemPacket(2));
                            entityBot.sendPacket(new ServerboundUseItemPacket(
                                    Hand.MAIN_HAND,
                                    (int) Instant.now().toEpochMilli(),
                                    0,
                                    0
                            ));
                            this.inQueue = true;
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        getListeners().add(new ContainerPacketHandler().addExtraAction((packet -> {
            String title = ConsoleTokens.colorizeText(((TextComponent)packet.getTitle()).content().strip());
            log.info(ConsoleTokens.colorizeText("&7[Inventory] &7Container opened, with containerId: &9{}, &6Title: \"&l{}\""), packet.getContainerId(), title);
            entityBot.sendPacket(new ServerboundContainerButtonClickPacket(packet.getContainerId(), 4));
            entityBot.sendPacket(new ServerboundContainerClickPacket(packet.getContainerId(), 0, 4, ContainerActionType.CLICK_ITEM, (ContainerAction) () -> 0, new ItemStack(0), new HashMap<>()));
        })));

        getListeners().add(new SystemChatHandler().addExtraAction((packet) -> {
            TextComponentSerializer componentSerializer = new TextComponentSerializer();
            String msg = componentSerializer.serialize(packet.getContent());
            if (msg.contains("/reg <密码> <密码>")){
                this.resetVerify();
            }
            else if (msg.contains("请登陆")){
                this.hasLoggedIn = false;
            } else if (msg.contains("请注册")){
                this.sendRegister(entityBot);
            }
        }));

        getListeners().add(
                new LoginHandler().addExtraAction((loginPacket) -> {
                    entityBot.setServerGamemode(loginPacket.getCommonPlayerSpawnInfo().getGameMode());
                    getLogger().info(loginPacket.getCommonPlayerSpawnInfo().getGameMode().name());
                })
        );

        getListeners().add(new TitlePacketHandler().addExtraAction((titleTextPacket)->{
            String titleMsg = ((TextComponent) titleTextPacket.getText()).content();
            getLogger().info("log-in state:"+this.hasLoggedIn);
            if(titleMsg.contains("成功")){
                this.hasLoggedIn = true;
            } else if (titleMsg.contains("请登陆")){
                this.hasLoggedIn = false;
            } else if (titleMsg.contains("请注册")){
                this.sendRegister(entityBot);
            }
        }));

        if (!this.autoLoginThread.isAlive()) {
            this.autoLoginThread.start();
        }
        if (!this.schedulerThread.isAlive()) {
            this.schedulerThread.start();
        }
    }

    public void resetVerify(){
        this.botInstance.setBypassed(false);
    }

    private boolean isBypassed(){
        return this.botInstance.isByPassedVerification();
    }

}
