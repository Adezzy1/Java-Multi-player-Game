package com.elite.game.state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.elite.game.assets.Sounds;

/**
 * State to represent the game being over.
 * 
 * <p>The winning team is displayed for a few seconds before this
 * state ends and passes control back to the lobbyState where the game
 * can be played again.</p>
 * 
 * @author Fraser Brooks
 *
 */
public class GameOverState extends State {

    private String winningTeam;
    private LobbyState lobby;
    private float backToLobbyTimer = 4f;
    
    
    public GameOverState(String winningTeam, LobbyState ls){
        this.winningTeam = winningTeam;
        this.lobby = ls;
    }
    
    
    @Override
    public void init() {
        Sounds.playMenuSound();
    }

    @Override
    public void update(float delta) {
        backToLobbyTimer -= delta;
        
        if(backToLobbyTimer < 0){
            setCurrentState(lobby);
            Sounds.playMenuSound();
        }
        
    }

    @Override
    public void render(Graphics g, int x, int y) {
        
        g.setFont(new Font(null, Font.BOLD, 18));
        if(winningTeam.contains("spies")){
            g.setColor(Color.black);
            g.fillRect(108, 87, 200, 50);
            g.setColor(Color.red);
            g.fillRect(110, 88, 196, 48);
            g.setColor(Color.WHITE);
            g.drawString("SPIES WIN", 160, 125);
        }else if(winningTeam.contains("guys")){
            g.setColor(Color.black);
            g.fillRect(108, 87, 200, 50);
            g.setColor(Color.lightGray);
            g.fillRect(110, 88, 196, 48);
            g.setColor(Color.black);
            g.drawString("GUYS WIN", 160, 125);
        }else{
            g.setColor(Color.black);
            g.fillRect(108, 87, 200, 50);
            g.setColor(Color.MAGENTA);
            g.fillRect(110, 88, 196, 48);
            g.setColor(Color.black);
            g.drawString("DRAW", 160, 125);
        }
        
        
        
    }

    @Override
    public void onClick(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClickRelease(MouseEvent e) {
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

}
