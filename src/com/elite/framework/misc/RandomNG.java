package com.elite.framework.misc;

import java.util.Random;


/**
 * Simple random number generator class.
 * 
 * <p> 
 * Allows for a single, static, random number generator that can be
 * accessed from anywhere in the code. This prevents the need to have to create
 * a generator at some point in the game loop every time a random number is needed. 
 * (Which, if happening 60x a second would be very inefficient.)
 * </p>
 * 
 * @author Fraser Brooks
 */
public class RandomNG {
    
    private static Random rand = new Random();
    
    /**
     * @param lowerBound
     * @param upperBound
     * @return A random number between lowerBound (inclusive) and upperBound (exclusive).
     */
    public  static int randR(int lowerBound, int upperBound){
        return rand.nextInt(upperBound - lowerBound) + lowerBound;
    }
    
    public static int getRandInt(int upperBound){
        return rand.nextInt(upperBound);
    }
    
}
