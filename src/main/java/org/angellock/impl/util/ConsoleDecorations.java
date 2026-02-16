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

public enum ConsoleDecorations {
    BOLD("\u001B[1m", 'L'),
    UNDERLINED("\u001B[4m", 'n'),
    REVERSE("\u001B[7m", 'R' ),
    STRIKETHROUGH("",'m'),
    ITALIC("",'o'),
    RESET_REVERSE("\u001B[27m", 'U'),
    RESET_ALL("\u001B[0m", '~'),
    NONE("", Character.MIN_VALUE);

    private final String colorToken;
    private final char colorCode;
    ConsoleDecorations(String ansiCode, char styleCode) {
        this.colorToken = ansiCode;
        this.colorCode = styleCode;
    }

    @Override
    public String toString() {
        return this.colorToken;
    }

    public char getColorCode() {
        return colorCode;
    }

    public static ConsoleDecorations parseColorFormCode(String name){
        for (ConsoleDecorations instance: values()){
            if (instance.name().equalsIgnoreCase(name)){
                return instance;
            }
        }
        return ConsoleDecorations.NONE; // return the default style
    }
}
