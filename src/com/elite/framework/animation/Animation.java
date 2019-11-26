package com.elite.framework.animation;

import java.awt.Graphics;

/**
* <p>Animation is a class used to represent an animation. </p>
* 
* <p>It is composed of an array of <b>Frame</b> objects and an array of frameEndTimes</p>
*<p>
* This class handles the transition and timing of switching from frame to frame
* as well as the responsibilty of drawing the current frame on a Graphics object.
*</p>
*<p>
* For the animation to transition from frame to frame, a call must be made to the 
* Animation object's update method where the float passed in is assumed by the frame
* times to be the time passed in seconds since the last time the update method was called. 
*</p>
* @author Fraser Brooks
*/
public class Animation {
    private Frame[] frames;
    private double[] frameEndTimes;
    private int currentFrameIndex = 0;
    
    private double totalDuration = 0;
    private double currentTime = 0;
    
    public Animation(Frame...frames ){
        this.frames = frames;
        frameEndTimes = new double[frames.length];
        
        for (int i = 0; i < frames.length; i++){
            Frame f = frames[i];
            totalDuration += f.getDuration();
            frameEndTimes[i] = totalDuration; 
        }
    }
    
    /**
     * Move the animation from one frame to the next according to frameEndTimes.
     * 
     * @param delta - time passed in seconds since last call to this method
     */
    public synchronized void update(float delta){
        currentTime += delta;
        if(currentTime > totalDuration){
            wrapAnimation();
        }
        while(currentTime > frameEndTimes[currentFrameIndex]){
            currentFrameIndex++;
        }
        
    }
    
    /**
     * Reset the current animation back to the first frame.
     */
    public void reset(){
        currentTime = 0;
        currentFrameIndex = 0;
    }
    
    /**
     * Keep timing consistent when looping back to first frame
     */
    private synchronized void wrapAnimation(){
        currentFrameIndex = 0;
        currentTime %= totalDuration; //ct = ct % td
    }
    
    /**
     * Draw this animation on the given Graphics object
     * 
     * @param g - the graphics object to draw this animation on
     * @param x - the x coordinate to draw at
     * @param y - the y coordinate to draw at
     */
    public synchronized void render(Graphics g, int x, int y){
        g.drawImage(frames[currentFrameIndex].getImage(), x, y, null);
    }
    
    /**
     * Draw this animation on the given Graphics object
     * 
     * @param g - the graphics object to draw this animation on
     * @param x - the x coordinate to draw at
     * @param y - the y coordinate to draw at
     * @param width - the width to draw the animation
     * @param height - the height to draw the animation
     */
    public synchronized void render(Graphics g, int x, int y, int width, int height){
        g.drawImage(frames[currentFrameIndex].getImage(), x, y, width, height, null);
    }
}
