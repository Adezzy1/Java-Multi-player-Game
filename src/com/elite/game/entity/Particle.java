package com.elite.game.entity;

import java.awt.image.BufferedImage;

/**
 * Particle class to represent particle effects that fly across the screen
 * along a given trajectory/list of coordinates.
 * 
 * @author Fraser Brooks
 *
 */
public class Particle {

    public static final int PROJECTILE = 1;
    public static final int COLLISION = 2;
    
    private int[] coordinates;
    protected boolean alive;
    private int arrayIndex;
    private float counter;
    private float speed;
    
    protected int particleType;
    
    private BufferedImage img;
    
    public Particle(int[] coords, BufferedImage img){
        coordinates = coords;
        counter = 0;
        arrayIndex = 0;
        alive = true;
        speed = 0.01f;
        particleType = PROJECTILE;
        this.img = img;
    }
    
    
    public void update(float delta){
        counter += delta;
        if(counter > speed){
            arrayIndex += 2;
            if(arrayIndex > coordinates.length - 2){
                alive = false;
            }
            counter = 0;
        }
        
        
    }
    
    public int getX(){
        if(alive){
            return coordinates[arrayIndex];
        }else{
            return -9999;
        }
    }
    
    public int getY(){
        if(alive){
            return coordinates[arrayIndex + 1];
        }else{
            return -9999;
        }
    }
    
    public boolean isAlive(){
        return alive;
    }
    
    public int getType(){
        return particleType;
    }
    
    public BufferedImage getImage(){
        return img;
    }
    
}
