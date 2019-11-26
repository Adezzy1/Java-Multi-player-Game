package com.elite.game.weapons;

import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.elite.framework.animation.Animation;
import com.elite.game.assets.Sounds;

public abstract class Weapon{
    
    public int id = -1;
    
    public static int PROJECTILE_ACCURACY = 10;
    
    public BufferedImage 
    flatBitmap, 
    leftBitmap, 
    rightBitmap, 
    backBitmap, 
    frontBitmap,
    leftBitmapF, 
    rightBitmapF, 
    backBitmapF,
    frontBitmapF,
    projectile,
    hitParticle; 
        
    private int x,y;    
    protected float fireRate;
    protected int range;
    protected float cooldown;
    protected boolean fired;
    
    
    public Weapon(int x, int y){
        this.x = x;
        this.y = y;
        fired = false;
        
        // default nonsense values just to initialise
        fireRate = 1000f;
        cooldown = 0;
        range = 30;
        
    }
    
    public void update(float delta){
        
        if(cooldown >= 0){
            cooldown -= delta;
        }
        if(cooldown < fireRate / 2){
            fired = false;
        }
        
    }
    
    public boolean shoot(){
        if(cooldown < 0){
            fired = true;
            cooldown = fireRate;
            System.out.println("weapon fired!");
            Sounds.playGunshot();
            return true;
        }else{
            return false;
        }
    }
    
    public abstract int damage(int d /*distance*/ );
    
    public static int[] projectilePath(int startX, int startY, 
            int dirX, int dirY, 
            int jumpAccuracy,
            int range){
        
        float yDelta = dirY - startY;
        float xDelta = dirX - startX;
        
        
        float xAbs = xDelta;
        if(xAbs < 0){
            xAbs *= -1;
        }
        float yAbs = yDelta;
        if(yAbs < 0){
            yAbs *= -1;
        }
        
        yDelta /= xAbs +  yAbs;
        xDelta /= xAbs + yAbs;
        
        int[] coords = new int[range];
        
        for(int i = 0; i < range - 1; i+=2){
            coords[i] = (int) (startX + (xDelta * jumpAccuracy * (i)));
            coords[i+1] = (int) (startY + (yDelta * jumpAccuracy * (i)));
        }
        
//        System.out.println("\nxStart = " + startX);
//        System.out.println("yStart = " + startY);
//        
//        System.out.println("\nxDelta = " + xDelta);
//        System.out.println("yDelta = " + yDelta);
//        
//        System.out.println("\nXtraj[0]: " + coords[0] + "  Ytraj[0]: " + coords[1]);
//        System.out.println("Xtraj[1]: " + coords[2] + "  Ytraj[1]: " + coords[3]);
//        System.out.println("Xtraj[2]: " + coords[4] + "  Ytraj[2]: " + coords[5]);
//        System.out.println("Xtraj[3]: " + coords[6] + "  Ytraj[3]: " + coords[7]);
        
        return coords;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public void setX(int x){
        this.x = x;
    }
    
    public void setY(int y){
        this.y = y;
    }
    
    public boolean hasFired(){
        return fired;
    }
    
    public int getRange(){
        return range;
    }
    
}
