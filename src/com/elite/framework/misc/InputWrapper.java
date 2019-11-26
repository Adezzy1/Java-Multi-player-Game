package com.elite.framework.misc;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.elite.game.state.State;


/**
 * InputWrapper is the class that is used by all <b>State</b> objects
 * to detect/capture input.
 * 
 * <p> 
 * More specifically, the InputWrapper implements <b>KeyListener</b> and 
 * <b>MouseListener</b> and forwards all events needed for the game to the 
 * <b>State</b> object currently set as currentState.
 * </p>  
 * 
 * @author Fraser Brooks
 */
public class InputWrapper implements KeyListener, MouseListener {
    
    private State currentState;
    
    public void setCurrentState(State s){
        this.currentState = s;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        currentState.onClick(e);
    }

    
    
    @Override
    public void mouseEntered(MouseEvent arg0) {
        // Nothing
        
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // Nothing
        
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // Nothing
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        currentState.onClickRelease(e);
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        currentState.onKeyPress(e);
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        currentState.onKeyRelease(e);
        
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // Nothing
        
    }

}
