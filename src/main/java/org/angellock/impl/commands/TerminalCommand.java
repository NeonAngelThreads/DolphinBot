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

import lombok.Getter;
import lombok.Setter;
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.plugin.AbstractPlugin;

@Setter
@Getter
public class TerminalCommand extends AbstractCommand {
    protected ICommandCompleter completer;
    protected AbstractPlugin provider;
    protected String usage;

    public TerminalCommand(String name, ICommandExecutor executor) {
        super(name, executor);
    }

    public TerminalCommand(String name, ICommandExecutor action, ICommandCompleter completer) {
        super(name, action);
        this.completer = completer;
    }

    public static class Builder extends AbstractCommandBuilder<TerminalCommand> {

        public Builder withName(String name) {
            this.commandName = name;
            return this;
        }
        public Builder withUsage(String name){
            this.usage = name;
            return this;
        }
        public Builder withProvider(AbstractPlugin plugin){
            this.provider = plugin;
            return this;
        }
        public Builder withDescription(String description){
            this.description = description;
            return this;
        }

        public Builder withAliases(String... aliases){
            this.aliases = aliases;
            return this;
        }

        @Override
        public TerminalCommand build(ICommandExecutor action) {
            TerminalCommand command = new TerminalCommand(this.commandName, action);
            command.setAliases(this.aliases);
            command.setDescription(this.description);
            command.setProvider(this.provider);
            command.setUsage(this.usage);
            return command;
        }
    }

    @Override
    public boolean activate(CommandResponse entity, RobotPlayer bot) throws Exception {
        if (entity.isFromTerminal()){
            action.onCommand(entity, bot);
            return true;
        }
        return false;
    }
}
