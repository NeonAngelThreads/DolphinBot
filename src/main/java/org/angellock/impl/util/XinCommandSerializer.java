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

package org.angellock.impl.util;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.commands.AbstractCommandSerializer;
import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.api.events.game.PlayerChatEvent;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XinCommandSerializer extends AbstractCommandSerializer {
    private final Pattern senderPattern = ChatPatternsRegistry.SERVER_2B2T_XIN;

    public XinCommandSerializer(AbstractRobot robotPlayer) {
        super(robotPlayer);
    }

    @Override
    protected @Nullable CommandResponse extractCommandMeta(PlayerChatEvent event, String[] commands) {
        if (event != null){
            if (commands != null){
                return new CommandResponse(commands, event.getPlayer());
            }
        }
        return null;
    }
    @Override
    public @Nullable PlayerChatEvent getEvent(String raw){
        Matcher matcher = this.senderPattern.matcher(raw);
        String commandSender;

        if (matcher.find()) {
            commandSender = matcher.group(1);

            raw = matcher.replaceFirst("").trim().strip();
            raw = ConsoleTokens.fadeText(raw);
            return new PlayerChatEvent(commandSender, raw);
        }
        return null;
    }

}
