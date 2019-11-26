package com.elite.game.weapons;

import com.elite.game.assets.Bitmaps;
import com.elite.game.entity.Map;

public class Shotgun extends Weapon {

    
    public Shotgun(int x, int y){
        super(x, y);
        
        id = Map.SHOTGUN_ID;
        
        flatBitmap = Bitmaps.getWeaponImage("shotgun","flat");
        leftBitmap = Bitmaps.getWeaponImage("shotgun","left");
        rightBitmap = Bitmaps.getWeaponImage("shotgun","right");
        backBitmap = Bitmaps.getWeaponImage("shotgun","back");
        frontBitmap = Bitmaps.getWeaponImage("shotgun","front");
        leftBitmapF = Bitmaps.getWeaponImage("shotgun","leftF");
        rightBitmapF = Bitmaps.getWeaponImage("shotgun","rightF");
        backBitmapF = Bitmaps.getWeaponImage("shotgun","backF");
        frontBitmapF = Bitmaps.getWeaponImage("shotgun","frontF");
        projectile = Bitmaps.getWeaponImage("shotgun", "projectile");
        hitParticle = Bitmaps.getWeaponImage("shotgun", "hitParticle");
        
        
        fireRate = 1f;
        range = 25;
    }
    
    @Override
    public int damage(int d) {
        int i = d/20;
        int dam = 120 - ( i * i * i);
        dam = (dam < 0) ? 5 : dam;
        System.out.println("damage: " + dam);
        return dam;
    }

    @Override
    public boolean shoot() {
        return super.shoot();
    }

}