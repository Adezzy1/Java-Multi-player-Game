package com.elite.game.state;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.elite.game.client.ClientLaunch;

/**
 * Abstract class to represent a state of the game
 * 
 * @author Fraser Brooks
 *
 */
public abstract class State {

    
  //used within MenuState & QuitState
    public int menubuttonsXcoord = 50; 
    public int creategameYcoord = 20;
    public int joingameYcoord = 90;
    public int quitgameYcoord = 160;

    public abstract void init();
    
    public abstract void update(float f);
    
    public abstract void render (Graphics g, int x, int y);
    
    public abstract void onClick(MouseEvent e);
    
    public abstract void onClickRelease(MouseEvent e);
    
    public abstract void onKeyPress(KeyEvent e);
    
    public abstract void onKeyRelease(KeyEvent e);
    
    
    
    public void setCurrentState(State newState){
        ClientLaunch.sGame.setCurrentState(newState);
    }
    
}
