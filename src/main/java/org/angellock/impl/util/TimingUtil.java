package org.angellock.impl.util;

import lombok.Getter;

import java.util.Random;

public class TimingUtil {
    @Getter
    protected final static Random randomizer = new Random();
    public static int getRandomDelay(Random randomizer, int previous){
        int i;
        do {
            i = randomizer.nextInt(1000)%8;
        } while (Math.abs(previous - i) < 1);
        return i;
    }

}
