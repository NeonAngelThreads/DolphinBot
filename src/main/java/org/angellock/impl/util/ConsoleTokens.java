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

import org.angellock.impl.Start;
import org.angellock.impl.util.colorutil.SimpleColor;
import org.angellock.impl.win32terminal.Win32ColorSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ConsoleTokens implements IComparable<SimpleColor>{
    BLACK("\u001B[30m", '0', new SimpleColor(0,0,0)),
    DARK_BLUE("\u001B[34m", '1', new SimpleColor(0,0,0xAA)),
    DARK_GREEN("\u001B[32m", '2', new SimpleColor(0,0xAA,0)),
    DARK_AQUA("\u001B[36m", '3', new SimpleColor(0,0xAA,0xAA)),
    DARK_RED("\u001B[31m", '4', new SimpleColor(0xaa,0,0)),
    DARK_PURPLE("\u001B[35m", '5', new SimpleColor(0xaa,0,0xaa)),
    GOLD("\u001B[33m", '6', new SimpleColor(0xFF,0xAA,0)),
    GRAY("\u001B[37m", '7', new SimpleColor(0xAA,0xAA,0xAA)),
    DARK_GRAY("\u001B[90m", '8', new SimpleColor(0x55,0x55,0x55)),
    BLUE("\u001B[94m", '9', new SimpleColor(0x55,0x55,0xFF)),
    GREEN("\u001B[92m", 'A', new SimpleColor(0x55,0xFF,0x55)),
    AQUA("\u001B[96m", 'B', new SimpleColor(0x55,0xFF,0xFF)),
    RED("\u001B[91m", 'C', new SimpleColor(0xFF,0x55,0x55)),
    LIGHT_PURPLE("\u001B[95m", 'D', new SimpleColor(0xFF,0x55,0xFF)),
    YELLOW("\u001B[93m", 'E', new SimpleColor(0xFF,0xFF,0x55)),
    WHITE("\u001B[97m", 'F', new SimpleColor(0xFF,0xFF,0xFF)),
    RESET_ALL("\u001B[0m", '~', SimpleColor.invalid()),
    NONE("", Character.MAX_VALUE, SimpleColor.invalid());
    private final String colorToken;
    private final char colorCode;
    private final SimpleColor hexColor;
    private static final Pattern foreground_pattern = Pattern.compile("[&§]([0-9a-flonNRU])");

    public static String colorizeText(String msg){
        if (Start.isWindows()) {
            return Win32ColorSerializer.serialize(msg);
        }else {
            Matcher matcher = foreground_pattern.matcher(msg);
            StringBuilder result = new StringBuilder();
            while (matcher.find()) {
                char code = matcher.group(1).charAt(0);
                matcher.appendReplacement(result, parseColorFormCode(code).toString());
            }
            matcher.appendTail(result);
            return standardizeText(result.toString());
        }
    }

    public static String fadeText(String text){
        Matcher matcher = foreground_pattern.matcher(text);
        return matcher.replaceAll("");
    }

    public static String standardizeText(String text){
        return text + ConsoleTokens.RESET_ALL;
    }
    ConsoleTokens(String colorToken, char colorCode, SimpleColor hexColor) {
        this.colorToken = colorToken;
        this.colorCode = colorCode;
        this.hexColor = hexColor;
    }

    @Override
    public String toString() {
        return this.colorToken;
    }

    public char getColorCode() {
        return colorCode;
    }

    public static ConsoleTokens parseColorFormCode(char code){
        for (ConsoleTokens instance: values()){
            if (instance.colorCode == Character.toUpperCase(code)){
                return instance;
            }
        }
        return ConsoleTokens.NONE; // return the default colour
    }
    public static String getColorName(char code){
        if (code == 'l'){
            return ConsoleDecorations.BOLD.name();
        }else if(code == 'o'){
            return ConsoleDecorations.ITALIC.name();
        }
        return parseColorFormCode(code).name();
    }

    public SimpleColor getHexColor(){
        return this.hexColor;
    }

    public static ConsoleTokens findMostSimilarANSIColor(SimpleColor searcher){
        ConsoleTokens mostSimilar = ConsoleTokens.NONE;
        if(!searcher.isValid()){
            return mostSimilar; // returning BLACK as fallback color for invalids
        }
        int difference = Integer.MAX_VALUE;
        for (ConsoleTokens instance: values()){
            int delta = searcher.getDelta(instance);
            if (difference > delta) {
                difference = delta;
                mostSimilar = instance;
                if (difference == 0){
                    return mostSimilar;
                }
            }
        }
        return mostSimilar;
    }
    @Override
    public int getDelta(SimpleColor object) {
        if(!object.isValid() || !this.getHexColor().isValid()){
            return Integer.MAX_VALUE;
        }

        int redD = Math.abs(object.getR() - this.getHexColor().getR());
        int greD = Math.abs(object.getG() - this.getHexColor().getG());
        int bluD = Math.abs(object.getB() - this.getHexColor().getB());

        return redD + greD + bluD;
    }
}
