package com.elite.game.state;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.elite.game.assets.Bitmaps;
import com.elite.game.engine.Game;

/**
 * Rules State to display the game rules/premise
 * 
 * @author Beenita Shah
 *
 */
public class RulesState extends State{
    
    public boolean inRules = true;
	
	public RulesState() {
		System.out.println("In Rules State");
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(float f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(Graphics g, int windowWidth, int windowHeight) {
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, windowWidth, windowHeight);
		g.setColor(Color.white);
		g.setFont(new Font(null, Font.BOLD, 20));
		g.drawString("Welcome to Spies and Guys", 68, 20);
		g.setFont(new Font(null, Font.PLAIN, 10));
		g.drawString("This game requires stealth, secrecy and spy work (obviously!!). The game consists of", 10, 50);
		g.drawString("two teams-the Spies and the Guys. The Spies team is smaller, but they all know", 10, 65);
		g.drawString("who their team mates are. The Guys have the advantage of numbers but lack", 10, 80);
		g.drawString("knowledge about their team mates.", 10, 95);
		g.drawString("AIM: Find everyone in the other team and kill them before they get you!", 10, 115);
		g.drawString("CONTROLS: The game is controlled using the WASD keys. W = Up, A = Left, ", 10, 130);
		g.drawString(" S = Down and D = Right. To shoot a player, just click somewhere.", 10, 145);
		g.drawString("Press Q to drop your weapon and E to identify one of your fallen friends.",10,160);
		g.drawString("Remeber-the game doesn't defy physics!!", 10, 175);
		
		g.drawImage(Bitmaps.cross, 
		        (int)(0.93*Game.GAME_WIDTH), 
		        (int)(0.02*Game.GAME_HEIGHT), 
		        (int)(0.05*Game.GAME_WIDTH), 
		        (int)(0.1*Game.GAME_HEIGHT), null);
        
	}

	@Override
	public void onClick(MouseEvent e) {
		// TODO Auto-generated method stub
        Point clicked = e.getPoint();
    	Component c = e.getComponent();
    	
    	int componentWidth = c.getWidth();
    	int componentHeight = c.getHeight();
    	
        Rectangle crossBounds = new Rectangle((int)(0.93*componentWidth), (int)(0.02*componentHeight), 
        				(int)(0.05*componentWidth), (int)(0.1*componentHeight));
        
        if (crossBounds.contains(clicked)){
            //"cross" image was clicked
            System.out.println("'cross' was clicked");
            setCurrentState(new MenuState());
        }
	}

	@Override
	public void onKeyPress(KeyEvent e) {
		//If M is pressed it takes you to the Game Menu page.
        if(e.getKeyCode() == KeyEvent.VK_M ) {
        	setCurrentState(new MenuState());
        	inRules = false;
        }
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
