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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class PlainTextSerializer implements ComponentSerializer<Component, Component, String> {
    private final StringBuilder result = new StringBuilder();
    @Override
    public @NotNull Component deserialize(@NotNull String input) {
        return Component.newline();
    }

    public void serializePlain(@NotNull Component component) {
        if (component instanceof TextComponent){
            this.result.append(((TextComponent) component).content());
        }

        for (Component child : component.children()){
            this.serializePlain(child);
        }
    }

    @NotNull
    @Override
    public String serialize(@NotNull Component component) {
        this.serializePlain(component);
        return ConsoleTokens.fadeText(result.toString());
    }
}
