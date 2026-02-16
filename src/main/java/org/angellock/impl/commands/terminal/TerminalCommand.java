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

package org.angellock.impl.commands.terminal;

import lombok.Getter;
import lombok.Setter;
import org.angellock.impl.AbstractRobot;
import org.angellock.impl.commands.AbstractCommand;
import org.angellock.impl.commands.CommandResponse;
import org.angellock.impl.commands.ICommandAction;
import org.angellock.impl.commands.ICommandCompleter;
import org.angellock.impl.plugin.AbstractPlugin;

@Setter
@Getter
public class TerminalCommand extends AbstractCommand {
    protected ICommandCompleter completer;
    protected AbstractPlugin provider;
    protected String usage;

    public TerminalCommand(String name, ICommandAction executor) {
        super(name, executor);
    }

    public TerminalCommand(String name, ICommandAction action, ICommandCompleter completer) {
        super(name, action);
        this.completer = completer;
    }

    @Override
    public boolean activate(CommandResponse entity, AbstractRobot bot) {
        if (entity.isFromTerminal()){
            action.onCommand(entity, bot);
            return true;
        }
        return false;
    }
}
