package com.elite.game.weapons;

import com.elite.game.assets.Bitmaps;
import com.elite.game.entity.Map;

public class SniperRifle extends Weapon {

    
    public SniperRifle(int x, int y){
        super(x,y);
        
        id = Map.SNIPER_ID;
        
        flatBitmap = Bitmaps.getWeaponImage("sniper","flat");
        leftBitmap = Bitmaps.getWeaponImage("sniper","left");
        rightBitmap = Bitmaps.getWeaponImage("sniper","right");
        backBitmap = Bitmaps.getWeaponImage("sniper","back");
        frontBitmap = Bitmaps.getWeaponImage("sniper","front");
        leftBitmapF = Bitmaps.getWeaponImage("sniper","leftF");
        rightBitmapF = Bitmaps.getWeaponImage("sniper","rightF");
        backBitmapF = Bitmaps.getWeaponImage("sniper","backF");
        frontBitmapF = Bitmaps.getWeaponImage("sniper","frontF");
        projectile = Bitmaps.getWeaponImage("sniper", "projectile");
        hitParticle = Bitmaps.getWeaponImage("sniper", "hitParticle");
        
        
        fireRate = 1.5f;
        range = 65;
        
        
    }
    
    
    @Override
    public int damage(int d) {
        int i = (d-10)/45;
        int dam = i * i * i ;
        dam = (dam < 0) ? 0 : dam; 
        System.out.println("damage: " + dam);
        return dam;
    }

}
