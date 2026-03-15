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

public abstract class AbstractBuilder<T> {
    protected String commandName = "";
    protected String description;
    protected AbstractPlugin provider;
    protected String usage;

    protected String[] aliases = new String[0];

    public AbstractBuilder<T> withName(String name) {
        this.commandName = name;
        return this;
    }

    public AbstractBuilder<T> withUsage(String name) {
        this.usage = name;
        return this;
    }

    public AbstractBuilder<T> withProvider(AbstractPlugin plugin) {
        this.provider = plugin;
        return this;
    }

    public AbstractBuilder<T> withDescription(String description){
        this.description = description;
        return this;
    }

    public AbstractBuilder<T> withAliases(String... aliases) {
        this.aliases = aliases;
        return this;
    }

    public abstract T build(ICommandAction action);
}
