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

import org.angellock.impl.commands.AbstractCommandSerializer;
import org.angellock.impl.commands.CommandResponse;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XinCommandSerializer extends AbstractCommandSerializer {
    private final Pattern senderPattern = Pattern.compile("<([^>]+)>");

    @Override
    protected @Nullable CommandResponse extractCommandMeta(String msg, char target) {
        Matcher matcher = this.senderPattern.matcher(msg);
        String commandSender;
        if (matcher.find()) {
            commandSender = matcher.group(1);

            msg = matcher.replaceAll("").trim().strip();
            msg = ConsoleTokens.fadeText(msg);
            if (msg.indexOf(target) < 1) {
                msg = msg.substring(msg.indexOf(target) + 1);
                String[] commands = msg.split(" ");
                for (int o = 0; o < commands.length; o++) {
                    commands[o] = commands[o].trim();
                }
                return new CommandResponse(commands, commandSender);
            }
            return null;
        }
        return null;
    }
}
