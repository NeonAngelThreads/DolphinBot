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
