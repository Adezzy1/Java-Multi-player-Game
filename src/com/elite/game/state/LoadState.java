package com.elite.game.state;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.elite.game.assets.Assets;
import com.elite.game.client.NetworkWrapper;

/**
 * Simple state that comes before all others and just loads the
 * game assets and makes a connection to the server before passing
 * on to a new <b>MenuState</b>
 * 
 * @author Fraser Brooks
 *
 */
public class LoadState extends State {

    @Override
    public void init() {
        Assets.load();
        NetworkWrapper.startConnection();
        System.out.println("Loaded Successfully");

    }

    @Override
    public void update(float delta) {
        setCurrentState(new MenuState());
    }

    @Override
    public void render(Graphics g, int x, int y) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onKeyPress(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onKeyRelease(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClickRelease(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

}
