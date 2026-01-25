package org.angellock.impl.extensions;

import net.kyori.adventure.text.TextComponent;
import org.angellock.impl.AbstractRobot;
import org.angellock.impl.events.handlers.SystemChatHandler;
import org.angellock.impl.managers.QuestionManager;
import org.angellock.impl.plugin.AbstractPlugin;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.QuestionSerializer;
import org.angellock.impl.util.TextComponentSerializer;

public class QuestionAnswererPlugin extends AbstractPlugin {
    private final QuestionManager questionManager = new QuestionManager(".json").load();
    private long lastAnswerTime = System.currentTimeMillis();
    @Override
    public String getPluginName() {
        return "QuestionAnswererPlugin";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "auto answerer";
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable(AbstractRobot entityBot) {

        getListeners().add(new SystemChatHandler().addExtraAction((packet) -> {
            if (!((TextComponent)packet.getContent()).content().isEmpty()) return;
            TextComponentSerializer textSerializer = new TextComponentSerializer();
            String msg = textSerializer.serialize(packet.getContent());

            if(msg.contains("接下来问一个问题")){
                this.lastAnswerTime = System.currentTimeMillis();
            }

            if (System.currentTimeMillis() - this.lastAnswerTime < 300L) {
                QuestionSerializer serializer = new QuestionSerializer(msg, questionManager);
                serializer.build();
                if (serializer.isValid()) {
                    getLogger().info(ConsoleTokens.colorizeText("&b{}"), serializer.getAnswer());
                    entityBot.getMessageManager().putMessage(serializer.getAnswer());
                }
            }
        }));
    }
}
