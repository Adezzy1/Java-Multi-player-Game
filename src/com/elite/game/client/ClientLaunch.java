package com.elite.game.client;
import javax.swing.JFrame;

import com.elite.game.assets.Bitmaps;
import com.elite.game.engine.Game;

/**
 * Launch point of the client.
 * 
 * <p> Creates a <b>Game</b> JPanel that contains the game.</p>
 *  
 * @author Fraser Brooks
 */
public class ClientLaunch {
    private static  final String GAME_TITLE = "Spys and Guys";
    
    public static Game sGame;
    
    public static void main(String[] args) {
        JFrame frame = new JFrame(GAME_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
        
        sGame = new Game();
        frame.add(sGame);
        frame.pack();
        frame.setVisible(true);
        frame.setIconImage(Bitmaps.iconimage);
    }
    
}
