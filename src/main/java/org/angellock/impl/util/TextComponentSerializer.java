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
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.angellock.impl.util.colorutil.SimpleColor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TextComponentSerializer implements ComponentSerializer<Component, Component, String> {

    private final StringBuilder result = new StringBuilder();
    @NotNull
    @Override
    public Component deserialize(@NotNull String input) {
        return Component.newline();
    }

    private void serializeColorAndStyle(Component component){
        Style style = component.style();
        TextColor color = style.color();
        ConsoleTokens colorToken = ConsoleTokens.NONE; // fallback color target
        if (color != null) {
            SimpleColor textColour = SimpleColor.parseColorFromHex(color.asHexString().substring(1));
            colorToken = ConsoleTokens.findMostSimilarANSIColor(textColour);
        }
        this.result.append(colorToken);
        Set<TextDecoration> decorations = style.decorations().keySet();

        for (TextDecoration decoration: decorations){
            if(style.decoration(decoration) == TextDecoration.State.TRUE){
                this.result.append(ConsoleDecorations.valueOf(decoration.name()));
            }
        }

        if (component instanceof TextComponent textComponent){
            this.result.append(textComponent.content());
        }

        if (component instanceof TranslatableComponent translatableComponent){
            this.result.append(translatableComponent.key());
        }

        for (Component child : component.children()){
            this.serializeColorAndStyle(child);
        }
    }

    @NotNull
    @Override
    public String serialize(@NotNull Component component) {
        this.serializeColorAndStyle(component);
        return result.toString() + ConsoleTokens.RESET_ALL;
    }
}
