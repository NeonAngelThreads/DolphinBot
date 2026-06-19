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
 *    program. If not, see <https://www.gnu.org/licenses/>.
 *
 * https://space.bilibili.com/386644641
 */

package org.angellock.impl.commands;

import lombok.extern.slf4j.Slf4j;
import org.angellock.impl.AbstractRobot;
import org.angellock.impl.api.events.game.PlayerChatEvent;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCommandSerializer {
    private final char chineseExclamation = '！';
    private final char exclamation = '!';
    private final AbstractRobot robotPlayer;
    public AbstractCommandSerializer(AbstractRobot robotPlayer) {
        this.robotPlayer = robotPlayer;
    }
    public @Nullable CommandResponse serialize(String stringCommand){

        PlayerChatEvent msgPart = getEvent(stringCommand);
        if (msgPart != null) {
            String[] commands = this.splitCommands(msgPart.getMessage(), exclamation, chineseExclamation);
            if (commands != null){
                return extractCommandMeta(msgPart, commands);
            }
            robotPlayer.callHandleableEvent(msgPart);
        }
        return null;
    }

    public @Nullable CommandResponse serialize(String command, String commandSender) {
        int indexSum = command.indexOf(exclamation) + command.indexOf(chineseExclamation);
        if (indexSum == -1) {
            String[] commands = command.substring(1).strip().split(" ");
            for (int o = 0; o < commands.length; o++) {
                commands[o] = commands[o].trim();
            }
            return new CommandResponse(commands, commandSender);
        }
        return null;
    }

    public String[] splitCommands(String msg, char...chars){
        for (char ch: chars) {
            if (msg.indexOf(ch) != -1) {
                msg = msg.substring(msg.indexOf(ch) + 1);
                String[] commands = msg.split("\\s+");
                for (int o = 0; o < commands.length; o++) {
                    commands[o] = commands[o].trim();
                }
                return commands;
            }
        }
        return null;
    }

    protected abstract @Nullable CommandResponse extractCommandMeta(PlayerChatEvent event, String[] commands);

    public abstract @Nullable PlayerChatEvent getEvent(String raw);
}
