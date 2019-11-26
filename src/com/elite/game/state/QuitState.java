package com.elite.game.state;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.elite.game.assets.Bitmaps;
import com.elite.game.assets.Sounds;
import com.elite.game.engine.Game;

/**
 * Quit State
 * 
 * @author Sabeeka Qureshi
 *
 */
public class QuitState extends State {
	
    @Override
    public void init() {
    	Sounds.menuSound.play();
        System.out.println("Entered Quit State");
    }

    @Override
    public void update(float delta) {
        // Do Nothing

    }

    @Override
    public void render(Graphics g, int x, int y) {
        
        int gameWidth = Game.GAME_WIDTH;
        int gameHeight = Game.GAME_HEIGHT;
        
        //Draw welcome screen
        g.drawImage(Bitmaps.welcome, 0, 0,gameWidth, gameHeight, null);
        
        //redraw background of menu, however it is "unclickable"    
        g.drawImage(Bitmaps.creategame, (int)(0.05*gameWidth), (int)(0.2*gameHeight), (int)(0.2*gameWidth), (int)(0.12*gameHeight), null);
        g.drawImage(Bitmaps.joingame, (int)(0.05*gameWidth), (int)(0.4*gameHeight), (int)(0.2*gameWidth), (int)(0.12*gameHeight), null);
        g.drawImage(Bitmaps.rulesbutton, (int)(0.05*gameWidth), (int)(0.6*gameHeight), (int)(0.2*gameWidth), (int)(0.12*gameHeight), null);
        g.drawImage(Bitmaps.quitgame, (int)(0.05*gameWidth), (int)(0.8*gameHeight), (int)(0.2*gameWidth), (int)(0.12*gameHeight), null);

        g.setFont(g.getFont().deriveFont(30f));
        g.setColor(Color.white);
        g.drawString("Spies vs Guys!", 120, 30);
        
        //drawing a quit pop up box
    	g.drawImage(Bitmaps.quitbox, (int)(0.22*gameWidth), (int)(0.2*gameHeight), (int)(0.6*gameWidth), (int)(0.6*gameHeight), null);
    	
    	//'yes' button
    	g.drawImage(Bitmaps.yesquit, (int)(0.3*gameWidth), (int)(0.6*gameHeight), (int)(0.15*gameWidth), (int)(0.1*gameHeight), null);
    	
    	//'no' button
    	g.drawImage(Bitmaps.noquit, (int)(0.6*gameWidth), (int)(0.6*gameHeight), (int)(0.15*gameWidth), (int)(0.1*gameHeight), null);
    	
    } 

    @Override
    public void onClick(MouseEvent e) {
        // TODO Auto-generated method stub
    	Point clicked = e.getPoint();
        Component c = e.getComponent();
    	
    	int componentWidth = c.getWidth();
    	int componentHeight = c.getHeight();
    	
    	System.out.println("you clicked something");
    	 
        Rectangle yesquitBounds = new Rectangle((int)(0.3*componentWidth), (int)(0.6*componentHeight), 
        				(int)(0.15*componentWidth), (int)(0.1*componentHeight));  
        
        if (yesquitBounds.contains(clicked)){
        	// "yes" quit game image was clicked
        	System.out.println("'yes' quit game was clicked");
        	System.exit(0);
        	System.out.println("Quit successfully");
        }
    	
        
        Rectangle noquitBounds = new Rectangle((int)(0.6*componentWidth), (int)(0.6*componentHeight), 
        				(int)(0.15*componentWidth), (int)(0.1*componentHeight));  
        
        if (noquitBounds.contains(clicked)) {
            // "no" quit game image was clicked
        	System.out.println("'no' quit game was clicked");
        	setCurrentState(new MenuState()); //return back to original MenuState without quit pop up box
        }
        

    }

    @Override
    public void onKeyPress(KeyEvent e) {
        // Intentionally Ignored

    }

    @Override
    public void onKeyRelease(KeyEvent e) {
        // Intentionally Ignored

    }

    @Override
    public void onClickRelease(MouseEvent e) {
        // Intentionally Ignored

    }

}
