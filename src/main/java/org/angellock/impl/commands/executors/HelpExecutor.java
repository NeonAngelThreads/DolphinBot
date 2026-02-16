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
import org.angellock.impl.EnumSystemEvents;
import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.commands.terminal.TerminalCommand;
import org.angellock.impl.managers.TerminalCommandManager;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TranslatableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelpExecutor implements org.angellock.impl.commands.ICommandAction {
    static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&3Help"));

    @Override
    public void onCommand(CommandResponse responseEntity, AbstractRobot bot) {
        for (TerminalCommand command : TerminalCommandManager.registeredCommand.values()) {
            log.info(TranslatableUtil.getFormattedMessage(EnumSystemEvents.COMMAND_NAME, command.getName()));
            log.info("          {}", TranslatableUtil.getFormattedMessage(EnumSystemEvents.COMMAND_ALIASES, command.getAliases()));
            log.info("          {}", TranslatableUtil.getFormattedMessage(EnumSystemEvents.COMMAND_DESCRIPTION, command.getDescription()));
            log.info("          {}", TranslatableUtil.getFormattedMessage(EnumSystemEvents.COMMAND_PROVIDER, command.getProvider()));
            log.info("          {}", TranslatableUtil.getFormattedMessage(EnumSystemEvents.COMMAND_USAGE, command.getUsage()));
        }
    }
}
