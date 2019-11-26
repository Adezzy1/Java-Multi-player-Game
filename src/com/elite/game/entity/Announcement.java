package com.elite.game.entity;

/**
 * An anouncement to be displayed on the top of the screen while the game
 * is playing.
 * 
 * <p> Messages will be of the form:
 * <ul>
 * <li> 'livingPlayer' found the body of 'deadPlayer' they were just a guy. OR:</li>
 * <li> 'livingPlayer' found the body of 'deadPlayer' they were a devious spy. </li>
 * </ul>
 *</p>
 * 
 * @author Fraser Brooks
 *
 */
public class Announcement {

    private static final float MESSAGE_TIME_TO_LIVE = 3f;
    
    private String message;
    private float timeToLive;
    
    public Announcement(String m){
        message = m;
        timeToLive = MESSAGE_TIME_TO_LIVE;
    }
    
    
    public void update(float delta){
        if(timeToLive > 0){
            timeToLive -= delta;
        }
    }
    
    public boolean isVisible(){
        return timeToLive > 0;
    }
    
    public String getMessage(){
        return message;
    }
    
}
