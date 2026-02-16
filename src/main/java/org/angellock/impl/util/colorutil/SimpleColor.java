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

package org.angellock.impl.util.colorutil;

import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.IComparable;

public class SimpleColor implements IComparable<ConsoleTokens> {
    private short R;
    private short G;
    private short B;

    public SimpleColor(short r, short g, short b) {
        this.R = r;
        this.G = g;
        this.B = b;
    }

    public SimpleColor(int r, int g, int b) {
        this((short) r, (short) g ,(short) b);
    }
    public SimpleColor(){
        this(0, 0, 0);
    }

    public short getR() {
        return R;
    }

    public short getG() {
        return G;
    }

    public short getB() {
        return B;
    }

    public static SimpleColor parseColorFromHex(String hexString){
        short red = Short.parseShort(hexString.substring(0,2), 16);
        short gre = Short.parseShort(hexString.substring(2,4), 16);
        short blu = Short.parseShort(hexString.substring(4,6), 16);
        return new SimpleColor(red, gre, blu);
    }

    public static SimpleColor invalid(){
        return new SimpleColor(-1, -1, -1);
    }

    public boolean isValid(){
        return this.R >= 0 || this.G >= 0 || this.B >= 0;
    }

    @Override
    public int getDelta(ConsoleTokens object) {
        if(!object.getHexColor().isValid()){
            return Integer.MAX_VALUE;
        }

        int redD = Math.abs(this.R - object.getHexColor().R);
        int greD = Math.abs(this.G - object.getHexColor().G);
        int bluD = Math.abs(this.B - object.getHexColor().B);

        return redD + greD + bluD;
    }

}
