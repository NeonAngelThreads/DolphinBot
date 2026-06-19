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

import org.angellock.impl.plugin.AbstractPlugin;

import java.util.ArrayList;
import java.util.List;
@Deprecated(since = "1.6.0")
public class CommandBuilder extends AbstractBuilder<Command> {
    private List<String> users = new ArrayList<>();

    public CommandBuilder withName(String name) {
        this.commandName = name;
        return this;
    }
    public CommandBuilder withUsage(String name){
        this.usage = name;
        return this;
    }
    public CommandBuilder withProvider(AbstractPlugin plugin){
        this.provider = plugin;
        return this;
    }
    public CommandBuilder withDescription(String description){
        this.description = description;
        return this;
    }

    public CommandBuilder withAliases(String... aliases){
        this.aliases = aliases;
        return this;
    }

    public CommandBuilder allowedUsers(List<String> users) {
        this.users = users;
        return this;
    }

    @Override
    public Command build(ICommandExecutor action) {
        Command command = new Command(this.commandName, action, this.users);
        command.setDescription(this.description);
        command.setAliases(this.aliases);
        return command;
    }
}
