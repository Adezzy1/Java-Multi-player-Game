package com.elite.game.state;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.elite.game.assets.Bitmaps;
import com.elite.game.assets.Sounds;
import com.elite.game.client.NetworkWrapper;
import com.elite.game.engine.Game;

/**
 * 
 * Menu state for the main menu of the game
 * with the following buttons:
 * 
 * <ul>
 * <li> Create Game</li>
 * <li> Join Game</li>
 * <li> Rules </li>
 * <li> Quit </li>
 * <ul>
 * 
 * @author - Sabeeka Qureshi
 *
 */
public class MenuState extends State {

    @Override
    public void init() {
    	Sounds.playMenuSound();
        System.out.println("Entered Menu State");
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
        
        //resized based on game width and height depending on users screen size using ratios
        g.drawImage(Bitmaps.creategame, (int)(0.05*gameWidth), (int)(0.2*gameHeight), (int)(0.2*gameWidth), (int)(0.12*gameHeight), null);
        g.drawImage(Bitmaps.joingame, (int)(0.05*gameWidth), (int)(0.4*gameHeight), (int)(0.2*gameWidth), (int)(0.12*gameHeight), null);
        g.drawImage(Bitmaps.rulesbutton, (int)(0.05*gameWidth), (int)(0.6*gameHeight), (int)(0.2*gameWidth), (int)(0.12*gameHeight), null);
        g.drawImage(Bitmaps.quitgame, (int)(0.05*gameWidth), (int)(0.8*gameHeight), (int)(0.2*gameWidth), (int)(0.12*gameHeight), null);

        g.setFont(g.getFont().deriveFont(30f));
        g.setColor(Color.white);
        g.drawString("Spies vs Guys!", 120, 30);
        
    } 
  
    @Override
    public void onClick(MouseEvent e) {
        // TODO Auto-generated method stub
    	Point clicked = e.getPoint();
    	Component c = e.getComponent();
    	
    	int componentWidth = c.getWidth();
    	int componentHeight = c.getHeight();
        
        Rectangle creategameBounds = new Rectangle((int)(0.05*componentWidth), (int)(0.2*componentHeight), 
        				(int)(0.2*componentWidth), (int)(0.12*componentHeight)); //Rectangle x,y coordinate system are different to drawImage, 
																							//hence the increase in x,y
        
        if (creategameBounds.contains(clicked)) {
            // creategame image was clicked
        	System.out.println("create game was clicked");
        	NetworkWrapper.setIsHost();
        	setCurrentState(new CreateGameUsernameState()); //continue onto username popup
        }
        
        Rectangle joinGameBounds = new Rectangle((int)(0.05*componentWidth), (int)(0.4*componentHeight), 
        				(int)(0.2*componentWidth), (int)(0.12*componentHeight));
        
        if (joinGameBounds.contains(clicked)) {
            // joingame image was clicked
            System.out.println("join game was clicked");
            setCurrentState(new CreateGameUsernameState()); //continue onto username popup
        }

        Rectangle rulesBounds = new Rectangle((int)(0.05*componentWidth), (int)(0.6*componentHeight), 
        				(int)(0.2*componentWidth), (int)(0.12*componentHeight));
        
        if (rulesBounds.contains(clicked)){
        	//rules image was clicked
        	System.out.println("rules clicked");
        	setCurrentState(new RulesState()); //continue onto rules
        }
        
        Rectangle quitgameBounds = new Rectangle((int)(0.05*componentWidth), (int)(0.8*componentHeight), 
        				(int)(0.2*componentWidth), (int)(0.12*componentHeight));
       
        if (quitgameBounds.contains(clicked)){
        	// quitgame image was clicked
        	System.out.println("quit");
        	setCurrentState(new QuitState()); //continue onto QuitState
        }
    	
    }

    @Override
    public void onKeyPress(KeyEvent e) {
    	//If R is pressed it takes you to the Game Rules page.
        if(e.getKeyCode() == KeyEvent.VK_R ) {
        	setCurrentState(new RulesState());
        }
    }

    @Override
    public void onKeyRelease(KeyEvent e) {
        // Intentionally Ignored

    }

    @Override
    public void onClickRelease(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

}
