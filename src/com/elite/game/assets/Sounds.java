package com.elite.game.assets;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

/**
 * Sound class for loading and storing all the sound files used throughout the game.
 * 
 * @author Adeola Aderibigbe
 */
public class Sounds{
    
    
	public static AudioClip gunCock;
	public static AudioClip gunshot;
	public static AudioClip airSound;
	public static AudioClip menuSound;
	public static AudioClip deadSound;
	public static URL fileURL;

	 
	 
	public Sounds() {
		
	}

    public static void load() {
        airSound = loadSound("0595.wav");
        menuSound = loadSound("OffLimits.wav");
        gunCock = loadSound("Gun+Cock.wav");
        gunshot = loadSound("Gun+Shot2.wav");
        deadSound = loadSound("deadSound.wav");
    }

    public static AudioClip loadSound(String filename) {
        fileURL = Assets.class.getResource("/assets/sounds/" + filename);
    	return Applet.newAudioClip(fileURL);
    }

    public static void playMenuSound() {
        menuSound.loop();
    }

    public static void playStateSound() {
        
	    	 menuSound.stop();
	    	 //airSound.loop();

    }
   
    public static void playGunshot() {
    	gunshot.play();
    }
    
    public static void playGuncock() {
    	gunCock.play();
    }
    
    public static void playDeadSound() {
    	deadSound.play();
    }
    
        
}



 