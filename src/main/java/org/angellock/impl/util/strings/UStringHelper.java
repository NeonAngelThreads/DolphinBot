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

package org.angellock.impl.util.strings;

import java.util.HashMap;
import java.util.Map;

public class UStringHelper {
    private static final Map<Character, String> charMap = new HashMap<>();
    private static final String intArray = "˦ϨՅϤƼδ7θƍO";

    static {
        charMap.put('a', "\uD835\uDCEA");charMap.put('b', "\uD835\uDCEB");charMap.put('c', "\uD835\uDCEC");charMap.put('d', "\uD835\uDCED");charMap.put('e', "\uD835\uDCEE");
        charMap.put('f', "\uD835\uDCEF");charMap.put('g', "\uD835\uDCF0");charMap.put('h', "\uD835\uDCBD");charMap.put('i', "\uD835\uDCBE");charMap.put('j', "\uD835\uDCBF");
        charMap.put('k', "\uD835\uDCC0");charMap.put('l', "\uD835\uDCC1");charMap.put('m', "\uD835\uDCC2");charMap.put('n', "\uD835\uDCF7");charMap.put('o', "\uD835\uDCF8");
        charMap.put('p', "\uD835\uDCF9");charMap.put('q', "\uD835\uDCFA");charMap.put('r', "\uD835\uDCFB");charMap.put('s', "\uD835\uDCFC");charMap.put('t', "\uD835\uDCFD");
        charMap.put('u', "\uD835\uDCFE");charMap.put('v', "\uD835\uDCFF");charMap.put('w', "\uD835\uDD00");charMap.put('x', "\uD835\uDD01");charMap.put('y', "\uD835\uDD02");
        charMap.put('z', "\uD835\uDD03");
        charMap.put('1',"１");charMap.put('2',"２");charMap.put('3',"３");charMap.put('4',"４");charMap.put('5',"５");
        charMap.put('8',"８");charMap.put('9',"９");charMap.put('0',"０");charMap.put('7',"７");charMap.put('6',"６");
    }

    public static String forceUnicode(String orgText){
        orgText = orgText.toLowerCase();

        StringBuilder stringBuilder = new StringBuilder();
        for (char ch : orgText.toCharArray()){
            if(Character.isDigit(ch)){
                stringBuilder.append(intArray.charAt(Character.digit(ch,10)));
                continue;
            }
            stringBuilder.append(ch);

        }

        return stringBuilder.toString();
    }

}
