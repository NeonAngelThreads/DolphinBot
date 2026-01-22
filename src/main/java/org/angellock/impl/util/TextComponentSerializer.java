package org.angellock.impl.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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

        if (component instanceof TextComponent){
            this.result.append(((TextComponent) component).content());
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
