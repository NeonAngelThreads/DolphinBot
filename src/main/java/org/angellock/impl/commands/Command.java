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

package org.angellock.impl.commands;

import lombok.Getter;
import lombok.Setter;
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.api.events.ChatCommandErrorEvent;
import org.angellock.impl.plugin.AbstractPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Command extends AbstractCommand {
    @Setter
    private List<String> users = new ArrayList<>();
    private final Map<String, Command> subCommands = new HashMap<>();
    private Command() {}
    public Command(String name, ICommandExecutor action, List<String> users) {
        super(name, action);
        this.users.addAll(users);
    }

    public void setAction(ICommandExecutor action){
        this.action = action;
    }
    @SuppressWarnings("unused")
    public static class Builder extends AbstractCommandBuilder<Command> {
        private final List<String> users = new ArrayList<>();
        private final Map<String, Command> subCommands = new HashMap<>();
        private final Command candidate = new Command();
        public Builder subCommand(String subCommandName, Command subCommand){
            this.subCommands.put(subCommandName, subCommand);
            return this;
        }
        public Builder withName(String name) {
            this.candidate.setName(name);
            return this;
        }
        public Builder withUsage(String usage){
            this.usage = usage;
            return this;
        }
        public Builder withProvider(AbstractPlugin plugin){
            this.provider = plugin;
            return this;
        }
        public Builder withDescription(String description){
            this.candidate.setDescription(description);
            return this;
        }
        public Builder withAliases(String... aliases){
            this.candidate.setAliases(aliases);
            return this;
        }
        public Builder allowedUsers(List<String> users) {
            this.candidate.setUsers(users);
            return this;
        }
        @Override
        public Command build(ICommandExecutor action) {
            this.candidate.setAction(action);
            this.candidate.subCommands.putAll(this.subCommands);
            return candidate;
        }
    }

    @Override
    public boolean activate(CommandResponse entity, RobotPlayer bot) {
        try {
            if (users.contains(entity.getSender()) || users.isEmpty()) {
                String[] commandList = entity.getCommandList();
                if (commandList.length > 0 && subCommands.containsKey(commandList[0])) {
                    Command subCommand = subCommands.get(commandList[0]);
                    CommandResponse subEntity = entity.subResponse(1);
                    return subCommand.activate(subEntity, bot);
                } else {
                    this.action.onCommand(entity, bot);
                    return true;
                }
            }
        } catch (Exception e) {
            bot.callHandleableEvent(new ChatCommandErrorEvent(e.getMessage()));
        }
        return false;
    }
}
