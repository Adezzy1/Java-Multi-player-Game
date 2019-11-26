package com.elite.game.assets;

import java.awt.image.BufferedImage;


/**
 * Assets class that loads all necessary assets for the game into memory.
 * 
 * @author Fraser Brooks
 */
public class Assets {
    
  
    public static void load() {
        
        Bitmaps.load();
        Sounds.load();
    
    }

    public static BufferedImage getTile(int tile){
        return Bitmaps.getTile(tile);
    }

}
