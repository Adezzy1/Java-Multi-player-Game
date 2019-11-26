package com.elite.framework.animation;

import java.awt.Image;

/**
 * Frame object to represent a still image that is part of an <b>Animation</b> object.
 * 
 * <p>Composed of an Image object that represents the frame and a double
 * that represents the duration of the frame in the context of the <b>Animation</b>.</P>
 * 
 * @author Fraser Brooks
 */
public class Frame {
    private Image image;
    private double duration;
    
    public Frame(Image image, double dur){
        this.image = image;
        this.duration = dur;
    }
    
    public Image getImage(){
        return image;
    }
    
    public double getDuration(){
        return duration;
    }
    
}
