/*
 *  DolphinBot - https://github.com/NeonAngelThreads/DolphinBot
 *  Copyright (C) 2025 NeonAngelThreads (https://github.com/NeonAngelThreads)
 *
 *     This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 *     License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any
 *     later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 *     implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 *     License for more details. You should have received a copy of the GNU General Public License along with this
 *     program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  https://space.bilibili.com/386644641
 */

package org.angellock.impl.commands;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommand {
    protected final String name;
    protected ICommandAction action;
    protected String description;
    protected List<String> aliases = new ArrayList<>();

    public AbstractCommand(String name, ICommandAction action) {
        this.name = name;
        this.action = action;
        this.aliases.add(name);
    }

    public AbstractCommand(String name, ICommandAction action, String[] aliases) {
        this(name, action);
        this.aliases.addAll(List.of(aliases));
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAliases(String[] aliases) {
        this.aliases = List.of(aliases);
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public boolean isAnAliasOf(String alias) {
        return this.aliases.contains(alias);
    }

    public abstract boolean activate(CommandResponse entity);
}
