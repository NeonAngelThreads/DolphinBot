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

package org.angellock.impl.commands.executors;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.commands.ICommandAction;
import org.angellock.impl.util.ConsoleTokens;

public class LicenseExecutor implements ICommandAction {
    @Override
    public void onCommand(CommandResponse responseEntity, AbstractRobot bot) {
        AbstractRobot.getLog().info(bot.getBotLabel(),ConsoleTokens.colorizeText(
                "&7# &9DolphinBot - https://github.com/NeonAngelThreads/DolphinBot\n" +
                        "&7# Copyright (C) 2025 &3NeonAngelThreads (&bhttps://github.com/NeonAngelThreads&7)\n" +
                        "&7#\n" +
                        "&7#    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public\n" +
                        "&7#    License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any\n" +
                        "&7#    later version.\n" +
                        "&7#\n" +
                        "&7#    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the\n" +
                        "&7#    implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public\n" +
                        "&7#    License for more details. You should have received a copy of the GNU General Public License along with this\n" +
                        "&7#    program.  If not, see <&6https://www.gnu.org/licenses/&7>.\n" +
                        "&7#\n" +
                        "&7# &bhttps://space.bilibili.com/386644641\n" +
                        "&7#"
        ));
        AbstractRobot.getLog().info("");
    }
}
