package com.elite.game.state;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.elite.game.assets.Bitmaps;
import com.elite.game.client.NetworkWrapper;
import com.elite.game.engine.Game;

/**
 * State for inputting the Game Key
 * 
 * @author - Sabeeka Qureshi
 *
 */
public class CreateGameKeyState extends State {
    
    String gameKey = ""; 
    int maxLength = 8;
    
    @Override
    public void init() {
        System.out.println("Entered Create Game Key State");
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
        
        //drawing a create game pop up box
        g.drawImage(Bitmaps.creategamekeybox, (int)(0.22*gameWidth), (int)(0.07*gameHeight), (int)(0.6*gameWidth), (int)(0.6*gameHeight), null);
        
        //textbox to enter unique game key
        g.drawImage(Bitmaps.textbox, (int)(0.3*gameWidth), (int)(0.27*gameHeight), (int)(0.4*gameWidth), (int)(0.12*gameHeight), null); 
        
        //cross to exit pop up box
        g.drawImage(Bitmaps.cross, (int)(0.75*gameWidth), (int)(0.12*gameHeight), (int)(0.05*gameWidth), (int)(0.1*gameHeight), null);
        
        //submit button
        g.drawImage(Bitmaps.submit, (int)(0.55*gameWidth), (int)(0.43*gameHeight), (int)(0.15*gameWidth), (int)(0.1*gameHeight), null);
        
        g.setFont(g.getFont().deriveFont(20f));
        g.setColor(Color.black);
        if (gameKey.length()>maxLength){
            gameKey = gameKey.substring(0, maxLength);
        }
        g.drawString(gameKey, 130, 82);
        
        
    } 

    @Override
    public void onClick(MouseEvent e) {
        // TODO Auto-generated method stub
        Point clicked = e.getPoint();
        Component c = e.getComponent();
    	
    	int componentWidth = c.getWidth();
    	int componentHeight = c.getHeight();
        System.out.println("you clicked something");
         
        Rectangle submitBounds = new Rectangle((int)(0.55*componentWidth), (int)(0.43*componentHeight), 
        				(int)(0.15*componentWidth), (int)(0.1*componentHeight));  
        
        if (submitBounds.contains(clicked) && (gameKey.length() > 0)){
            // "submit" image was clicked
            System.out.println("'submit' was clicked with gameKey: " + gameKey);
            NetworkWrapper.setGameKey(gameKey);
            NetworkWrapper.connectToGame();
            setCurrentState(new LobbyState());
            System.out.println("Game created successfully");
        }
        
        Rectangle crossBounds = new Rectangle((int)(0.75*componentWidth), (int)(0.12*componentHeight), 
        				(int)(0.05*componentWidth), (int)(0.1*componentHeight));
        
        if (crossBounds.contains(clicked)){
            //"cross" image was clicked
            System.out.println("'cross' was clicked");
            setCurrentState(new MenuState());
        }

    }

    @Override
    public void onKeyPress(KeyEvent e) {
        // Intentionally Ignored
        
        if ((e.getKeyCode() == KeyEvent.VK_BACK_SPACE) && (gameKey.length()>0) ){ 
            //length must be >0 or it will throw an exception
            
            //if backspace, then remove the last character
            
            gameKey = gameKey.substring(0, (gameKey.length()-1));
        } 
        else if (!(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) && isAscii(e.getKeyChar()) && 
                        !(e.getKeyCode() == KeyEvent.VK_SPACE) && Character.toString(e.getKeyChar()).matches("[a-zA-Z0-9]*")){ 
            
            //checking only valid characters (letters and numbers) are added to gameKey
            //No backspaces, only ASCII characters allowed (no action keys), no spaces, no punctuation
   
            gameKey = gameKey + e.getKeyChar();
        }
    }

    public static boolean isAscii(char ch) {
        
        //checks if character is ASCII or not
        
        return ch < 128;
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
