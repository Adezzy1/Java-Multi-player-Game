package com.elite.game.entity;


import java.awt.image.BufferedImage;

/**
 * This class represents a small particle/Bitmap that is rendered
 * at the point where a bullet/projectile colides with something.
 * 
 * @author Fraser Brooks
 */
public class HitParticle extends Particle {

    private int x;
    private int y;
    private float timeToLive;
    
   
    
    public HitParticle(int x, int y, BufferedImage hitParticle){
        super(null, hitParticle);
        this.x = x;
        this.y = y;
        timeToLive = 1f;
        particleType = COLLISION;
    }
    
    @Override
    public void update(float delta) {
        timeToLive -= delta;
        if (timeToLive < 0) {
            alive = false;

        }

    }
    
    @Override
    public int getX(){
        return x;
    }
    
    @Override
    public int getY(){
        return y;
    }
    
}