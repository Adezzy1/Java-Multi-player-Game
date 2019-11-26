package com.elite.game.engine;

import com.elite.game.entity.Player;

/**
 * Utility class used to convert between tile coordinates (i.e. -128-128)
 * and the Universal coordinates that include the precision of the tile offsets
 * (i.e. -128*32-128*32).
 * 
 * <p>Also included is a distance function for calculating the distance between two
 * points (of either coordinate system) </p>
 * 
 * @author Fraser Brooks
 */
public class CoordinateConverter {
    
    public static int[] universalCoords(int x, int y, int xOffset, int yOffset){
        return  new int[] {(x*32 + xOffset), (y*32 + yOffset)};
    }
    
    public static int[] tileCoords(int x, int y){
        
        int tileX = x / 32;
        int tileY = y / 32;
        
        if(x < 0){
            tileX -= 1;
        }
        
        if(y<0){
          tileY -= 1;  
        }
        
        return new int[]{tileX, tileY};
    }
    
    public static int[] renderCoordsFromUniversal(Player p, int x, int y){
        int[] playerCs = universalCoords(p.getX(), p.getY(), p.getXOffset(), p.getYOffset());
        
        int xDiff = x - playerCs[0];
        int yDiff = y - playerCs[1];
        
        return new int[] {Renderer.PLAYERXDRAW + xDiff, Renderer.PLAYERYDRAW + yDiff};
        
    }
    
    public static int[] screenToUniversal(float xRatio, float yRatio, Player p){
        int[] playerCs = universalCoords(p.getX(), p.getY(), p.getXOffset(), p.getYOffset());
        
        int x = (int) ((playerCs[0] - ((Renderer.TILES_ACROSS/2) * 32)) 
                + (Renderer.TILES_ACROSS * 32) * xRatio);
        
        int y = (int) ((playerCs[1] - ((Renderer.TILES_DOWN/2) * 32)) 
                + (Renderer.TILES_DOWN * 32) * yRatio);
        
        return new int[]{x,y};
    }
    
    public static int distance(int x1, int y1, int x2, int y2){
        
        int a = x1 - x2;
        int b = y1 - y2;
        
        int cSq = (a * a) + (b * b);
        
        return (int) Math.sqrt(cSq);
        
    }
    
    
    
}
