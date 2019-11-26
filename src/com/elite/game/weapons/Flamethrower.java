package com.elite.game.weapons;

import com.elite.game.assets.Bitmaps;
import com.elite.game.entity.Map;

public class Flamethrower extends Weapon {

    public Flamethrower(int x, int y){
        super(x, y);
        
        id = Map.FLAME_ID;
        
        flatBitmap = Bitmaps.getWeaponImage("flamethrower","flat");
        leftBitmap = Bitmaps.getWeaponImage("flamethrower","left");
        rightBitmap = Bitmaps.getWeaponImage("flamethrower","right");
        backBitmap = Bitmaps.getWeaponImage("flamethrower","back");
        frontBitmap = Bitmaps.getWeaponImage("flamethrower","front");
        leftBitmapF = Bitmaps.getWeaponImage("flamethrower","leftF");
        rightBitmapF = Bitmaps.getWeaponImage("flamethrower","rightF");
        backBitmapF = Bitmaps.getWeaponImage("flamethrower","backF");
        frontBitmapF = Bitmaps.getWeaponImage("flamethrower","frontF");
        projectile = Bitmaps.getWeaponImage("flamethrower", "projectile");
        hitParticle = Bitmaps.getWeaponImage("flamethrower", "hitParticle");
        
        
        fireRate = 0.03f;
        range = 15;
    }
    
    @Override
    public int damage(int d) {
        return 15;
    }

    @Override
    public boolean shoot() {
        return super.shoot();
    }
}
