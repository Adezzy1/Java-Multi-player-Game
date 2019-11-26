package com.elite.game.weapons;

import com.elite.game.assets.Bitmaps;
import com.elite.game.entity.Map;

public class Pistol extends Weapon {

    
    public Pistol(int x, int y){
        super(x, y);
        
        id = Map.PISTOL_ID;
        
        flatBitmap = Bitmaps.getWeaponImage("pistol","flat");
        leftBitmap = Bitmaps.getWeaponImage("pistol","left");
        rightBitmap = Bitmaps.getWeaponImage("pistol","right");
        backBitmap = Bitmaps.getWeaponImage("pistol","back");
        frontBitmap = Bitmaps.getWeaponImage("pistol","front");
        leftBitmapF = Bitmaps.getWeaponImage("pistol","leftF");
        rightBitmapF = Bitmaps.getWeaponImage("pistol","rightF");
        backBitmapF = Bitmaps.getWeaponImage("pistol","backF");
        frontBitmapF = Bitmaps.getWeaponImage("pistol","frontF");
        projectile = Bitmaps.getWeaponImage("pistol", "projectile");
        hitParticle = Bitmaps.getWeaponImage("pistol", "hitParticle");
        
        fireRate = 0.08f;
        range = 30;
    }
    
    @Override
    public int damage(int d) {
        int dam = 15 - (d / 60);
        System.out.println("damage: " + dam);
        return dam ;
    }

    @Override
    public boolean shoot() {
        return super.shoot();
    }

}
