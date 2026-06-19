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
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.api.handlers.SystemChatHandler;
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
        this.getListeners().clear();
    }

    @Override
    public void onEnable(RobotPlayer entityBot) {

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
