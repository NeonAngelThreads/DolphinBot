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

package org.angellock.impl.win32terminal;

import org.jline.jansi.Ansi;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Win32ColorSerializer implements Serializable {
    private static final Pattern foreground_pattern = Pattern.compile("[&§]([0-9a-flonNRU])");
    private static final Ansi RESET_ALL = Ansi.ansi().reset();

    private static final Ansi.Color[] simpleColors = new Ansi.Color[] {Ansi.Color.BLACK, Ansi.Color.BLUE, Ansi.Color.GREEN, Ansi.Color.CYAN, Ansi.Color.RED, Ansi.Color.MAGENTA, Ansi.Color.YELLOW, Ansi.Color.WHITE, Ansi.Color.BLACK, Ansi.Color.BLUE};
    private static final Ansi[] colorList = new Ansi[]{Ansi.ansi().fgBlack(), Ansi.ansi().fgBlue(), Ansi.ansi().fgGreen(), Ansi.ansi().fgCyan(), Ansi.ansi().fgRed(), Ansi.ansi().fgMagenta(), Ansi.ansi().fgYellow(), Ansi.ansi().fg(Ansi.Color.WHITE), Ansi.ansi().fgBrightBlack(), Ansi.ansi().fgBrightBlue()};
    public static String serialize(String text){

        Matcher matcher = foreground_pattern.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            char code = matcher.group(1).charAt(0);
            Ansi AnsiResult = serializeWindowsColor(code);
            matcher.appendReplacement(result, AnsiResult.toString());
        }
        matcher.appendTail(result);
        return result.toString() + RESET_ALL;
    }
    public static Ansi serializeWindowsColor(char code) {
        if (Character.isDigit(code)){
            return colorList[Character.digit(code, 10)];
        }
        Ansi text = Ansi.ansi();
        return switch (code) {
            case 'l' -> text.bold().a(text);
            case 'o' -> text.a(Ansi.Attribute.ITALIC).a(text);
            case 'f' -> text.fgBright(Ansi.Color.WHITE).a(text);
            case 'a' -> text.fgBrightGreen().a(text);
            case 'b' -> text.fgBrightCyan().a(text);
            case 'c' -> text.fgBrightRed().a(text);
            case 'd' -> text.fgBrightMagenta().a(text);
            case 'e' -> text.fgBrightYellow().a(text);
            case 'r' -> text.fgDefault()
                    .a(Ansi.Attribute.STRIKETHROUGH_OFF)
                    .a(Ansi.Attribute.UNDERLINE_OFF)
                    .a(text);
            case 'n' -> text.a(Ansi.Attribute.UNDERLINE).a(text);
            case 'm' -> text.a(Ansi.Attribute.STRIKETHROUGH_ON).a(text);
            default -> text;
        };
    }

    public static Ansi serializeBackgroundColor(int code){
        return new Ansi().bg(simpleColors[code]);
    }
}
