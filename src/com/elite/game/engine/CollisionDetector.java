package com.elite.game.engine;

import com.elite.game.client.NetworkWrapper;
import com.elite.game.entity.Player;
import com.elite.game.state.PlayState;
import com.elite.game.weapons.Weapon;

/**
 * Collision Detection class for detecing collisions with walls and weapons
 * 
 * @author Andreea Merariu
 * @author Fraser Brooks
 *
 */
public class CollisionDetector {

    private PlayState playState;
    
	public CollisionDetector(PlayState playState) {
	    this.playState = playState;
	}
	
    public boolean hasCollidedWithWall(Player p){
        
        if(!p.isMoving()){
            return false;
        }
        
        int x = p.getX();
        int y = p.getY();
        
        int xO = p.getXOffset();
        int yO = p.getYOffset();
        
        // check all four corners of player sprite
        if(playState.getMap().isWall(x, y)){
            return true;
        }
        if(playState.getMap().isWall(x+1, y) && (xO > 0)){
            return true;
        }
        if(playState.getMap().isWall(x, y + 1) && (yO > 0)){
            return true;
        }
        if(playState.getMap().isWall(x + 1, y + 1) && 
                (
                (xO > 0 && p.isFacingDown()) 
                || (yO > 0 && p.isFacingRight())
                ) ){
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @param p - the player to be tested for collisions
     * @return true if a weapon collision was detected else false
     */
    public boolean weaponCollisionDetection(Player p){
        int x = p.getX();
        int y = p.getY();
        
        int xO = p.getXOffset();
        int yO = p.getYOffset();
        
        if(p.getWeapon() != null){
            return false;
        }
        
        for(Weapon w : playState.getWeapons()){
         // check all four corners of player sprite
            if(w.getX() == x && w.getY() == y){
                playState.getWeapons().remove(w);
                NetworkWrapper.sendTCP(("pickup@" + w.getX() + ":" + w.getY() ).getBytes());
                return true;
            }
            if(w.getX() == x + 1 && w.getY() == y && (xO > 0)){
                playState.getWeapons().remove(w);
                NetworkWrapper.sendTCP(("pickup@" + w.getX() + ":" + w.getY() ).getBytes());
                return true;
            }
            if(w.getX() == x && w.getY() == y + 1 && (yO > 0)){
                playState.getWeapons().remove(w);
                NetworkWrapper.sendTCP(("pickup@" + w.getX() + ":" + w.getY() ).getBytes());
                return true;
            }
            if(w.getX() == x + 1 && w.getY() == y + 1 && 
                    (
                    (xO > 0 && p.isFacingDown()) 
                    || (yO > 0 && p.isFacingRight())
                    ) ){
                playState.getWeapons().remove(w);
                NetworkWrapper.sendTCP(("pickup@" + w.getX() + ":" + w.getY() ).getBytes());
                return true;
            }
            
        }
        return false;
        
    }
    
}
