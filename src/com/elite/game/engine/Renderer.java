package com.elite.game.engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.elite.game.assets.Assets;
import com.elite.game.assets.Bitmaps;
import com.elite.game.client.NetworkWrapper;
import com.elite.game.entity.Announcement;
import com.elite.game.entity.Particle;
import com.elite.game.entity.Player;
import com.elite.game.entity.RandomAiPlayer;
import com.elite.game.gameAI.AiPlayer;
import com.elite.game.state.PlayState;
import com.elite.game.weapons.Weapon;

/**
 * Class created to refactor the rendering out of the <b>PlayState</b>.
 * 
 * <p> The static final variables hold the key values necessary for rendering including
 * the size of the tiles (32x32 pixels), the number of tiles that will fit onto the screen (both
 * vertically and horizontally) and the X and Y coordinates to draw the player at (which are
 * declared final because the player is always centered and therefore always drawn in the
 * same place).</p>
 * 
 *  <p> Throughout the game (including in all the menu states) 
 *  each frame is first drawn to a Graphics object that comes from an Image object with
 *  a fixed size: TILES_ACROSS*TILE_SIZE, TILES_DOWN*TILE_SIZE. Then after
 *  everything has been drawn to that Graphics object it's drawn onto the <b>Game</b>
 *  JPanel, scaled to fit whatever size the user has said JPanel set to. </p>
 * 
 * @author Fraser Brooks
 */
public class Renderer {

    // Must be odd so player can be centered in the screen
    // ie. there will be an even number of tiles either side of 
    // the player at all times during rendering
    public static final int TILES_ACROSS = 13;
    public static final int TILES_DOWN = 7;
    public static final int TILE_SIZE = 32;
    
    // fixed because the player is always centered
    public static final int  PLAYERXDRAW = (TILES_ACROSS / 2) * TILE_SIZE;
    public static final int PLAYERYDRAW = (TILES_DOWN / 2) * TILE_SIZE;
    
    private PlayState playState;
    
    public Renderer(PlayState playState){
        this.playState = playState;
    }
    
    public void render(Graphics g) {

        // get the map coordinates of the tile that will be on the top left of
        // the screen
        int topLeftTileX = playState.getPlayer().getX() - (TILES_ACROSS / 2);
        int topLeftTileY = playState.getPlayer().getY() - (TILES_DOWN / 2);
        // draw tiles of map (only the ones needed to fill the viewport)
        for (int y = topLeftTileY; y < topLeftTileY + TILES_DOWN + 2; y++) {
            for (int x = topLeftTileX; x < topLeftTileX + TILES_ACROSS + 2; x++) {
                int xDraw = (TILE_SIZE * (x - topLeftTileX) - playState.getPlayer().getXOffset());
                int yDraw = (TILE_SIZE * (y - topLeftTileY) - playState.getPlayer().getYOffset());
                try {
                    int t = playState.getMap().getTile(x, y);
                    if(t >= 200 || t <= -200){
                        int backgroundTile = t % 200;
                        g.drawImage(Assets.getTile(backgroundTile), xDraw, yDraw, null);
                        t -= backgroundTile;
                    }
                    g.drawImage(Assets.getTile(t), xDraw, yDraw, null);
                } catch (ArrayIndexOutOfBoundsException exception) {
                    g.drawImage(Bitmaps.nullTile, xDraw, yDraw, null);
                }
            }
        }
        //
        
        for (Weapon weapon : playState.getWeapons()) {
            int diffX = (((int) weapon.getX()) * 32)
                    - (((byte) playState.getPlayer().getX()) * 32 + playState.getPlayer().getXOffset());
            int diffY = (((int) weapon.getY()) * 32 )
                    - (((byte) playState.getPlayer().getY()) * 32 + playState.getPlayer().getYOffset());
            int drawX = PLAYERXDRAW + diffX;
            int drawY = PLAYERYDRAW + diffY;
            if (drawX < -32 || drawX > (PLAYERXDRAW * 2) + 32 || drawY < -32 || drawY > (PLAYERYDRAW * 2) + 32) {
                continue;
            }
            renderWeapon(g, weapon, drawX, drawY);

        }

        //Draw ai players
        for (RandomAiPlayer aiPlayers : playState.getAiPlayers()) {
            int diffX = (((int) aiPlayers.getX()) * 32 + aiPlayers.getXOffset()) - (playState.getPlayer().getX() * 32 + playState.getPlayer().getXOffset());
            int diffY = (((int) aiPlayers.getY()) * 32 + aiPlayers.getYOffset()) - (playState.getPlayer().getY() * 32 + playState.getPlayer().getYOffset());
            int drawX = PLAYERXDRAW + diffX;
            int drawY = PLAYERYDRAW + diffY;
            if (drawX < -32 || drawX > (PLAYERXDRAW * 2) + 32 || drawY < -32 || drawY > (PLAYERYDRAW * 2) + 32) {
                continue; // don't render this character as it's off screen
            }
            renderCharacter(g, aiPlayers, drawX, drawY);
        }
        
        // Draw other players:
        for (Player otherPlayer : playState.getOtherPlayers()) {
            int diffX = (((int) otherPlayer.getX()) * 32 + otherPlayer.getXOffset()) - (playState.getPlayer().getX() * 32 + playState.getPlayer().getXOffset());
            int diffY = (((int) otherPlayer.getY()) * 32 + otherPlayer.getYOffset()) - (playState.getPlayer().getY() * 32 + playState.getPlayer().getYOffset());
            int drawX = PLAYERXDRAW + diffX;
            int drawY = PLAYERYDRAW + diffY;
            if (drawX < -32 || drawX > (PLAYERXDRAW * 2) + 32 || drawY < -32 || drawY > (PLAYERYDRAW * 2) + 32) {
                continue; // don't render this character as it's off screen
            }
            renderCharacter(g, otherPlayer, drawX, drawY);
        }

        // Draw player
        renderCharacter(g, playState.getPlayer(), PLAYERXDRAW, PLAYERYDRAW);
        
        
        renderGUI(g, playState.getPlayer());
        
        renderParticles(g);

        // render the messages that anounce when someone has found a dead body
        renderMessages(g);
    }

    private void renderMessages(Graphics g) {
        for(Announcement m : playState.getAnnouncements()){
            if(m.isVisible()){
                g.setColor(new Color(0f, 0f, 0f, 0.8f));
                g.fillRect(25, 5, 366, 20);
                g.setColor(Color.WHITE);
                g.drawString(m.getMessage(), 75, 18);
            }
        }
    }

    private void renderParticles(Graphics g) {
        int j = playState.getParticles().size();
        for(int i = 0; i < j; i++){
            Particle p = playState.getParticles().get(i);

            int[] renderCoords = CoordinateConverter.renderCoordsFromUniversal(playState.getPlayer(), p.getX(), p.getY());
            g.drawImage(p.getImage(), renderCoords[0], renderCoords[1], null);
        }
    }

    // render weapons on the ground
    private void renderWeapon(Graphics g, Weapon w, int x, int y) {

        g.drawImage(w.flatBitmap, x, y, null);

    }

    private void renderCharacter(Graphics g, Player p, int x, int y) {
        
        if(!p.isAlive){
            g.drawImage(Bitmaps.getPlayerImage(p.getPlayerModel(), "dead"), x, y, null);
            return;
        }
        
        if(p.getWeapon() != null){
            if (p.isFacingLeft()) {
                if(p.getWeapon().hasFired()){
                    g.drawImage(p.getWeapon().leftBitmapF, x, y, null);
                }else{
                    g.drawImage(p.getWeapon().leftBitmap, x, y, null);
                }
                if (p.isMoving()) {
                    Bitmaps.getPlayerAnim(p.getPlayerModel(), "left").render(g, x, y);
                } else {
                    g.drawImage(Bitmaps.getPlayerImage(p.getPlayerModel(), "left_three"), x, y, null);
                }
            } else if (p.isFacingRight()) {
                if (p.isMoving()) {
                    Bitmaps.getPlayerAnim(p.getPlayerModel(), "right").render(g, x, y);
                } else {
                    g.drawImage(Bitmaps.getPlayerImage(p.getPlayerModel(), "right_three"), x, y, null);
                }
                if(p.getWeapon().hasFired()){
                    g.drawImage(p.getWeapon().rightBitmapF, x, y, null);
                }else{
                    g.drawImage(p.getWeapon().rightBitmap, x, y, null);
                }
            } else if (p.isFacingUp()) {
                if(p.getWeapon().hasFired()){
                    g.drawImage(p.getWeapon().backBitmapF, x, y, null);
                }else{
                    g.drawImage(p.getWeapon().backBitmap, x, y, null);
                }
                if (p.isMoving()) {
                    Bitmaps.getPlayerAnim(p.getPlayerModel(), "up").render(g, x, y);
                } else {
                    g.drawImage(Bitmaps.getPlayerImage(p.getPlayerModel(), "back_three"), x, y, null);
                }
            } else {
                if (p.isMoving()) {
                    Bitmaps.getPlayerAnim(p.getPlayerModel(), "down").render(g, x, y);
                } else {
                    g.drawImage(Bitmaps.getPlayerImage(p.getPlayerModel(), "front_three"), x, y, null);
                }
                if(p.getWeapon().hasFired()){
                    g.drawImage(p.getWeapon().frontBitmapF, x, y, null);
                }else{
                    g.drawImage(p.getWeapon().frontBitmap, x, y, null);
                }
            }
        }else{
            if (p.isFacingLeft()) {
                if (p.isMoving()) {
                    Bitmaps.getPlayerAnim(p.getPlayerModel(), "left").render(g, x, y);
                } else {
                    g.drawImage(Bitmaps.getPlayerImage(p.getPlayerModel(), "left_three"), x, y, null);
                }
            } else if (p.isFacingRight()) {
                if (p.isMoving()) {
                    Bitmaps.getPlayerAnim(p.getPlayerModel(), "right").render(g, x, y);
                } else {
                    g.drawImage(Bitmaps.getPlayerImage(p.getPlayerModel(), "right_three"), x, y, null);
                }
            } else if (p.isFacingUp()) {
                if (p.isMoving()) {
                    Bitmaps.getPlayerAnim(p.getPlayerModel(), "up").render(g, x, y);
                } else {
                    g.drawImage(Bitmaps.getPlayerImage(p.getPlayerModel(), "back_three"), x, y, null);
                }
            } else {
                if (p.isMoving()) {
                    Bitmaps.getPlayerAnim(p.getPlayerModel(), "down").render(g, x, y);
                } else {
                    g.drawImage(Bitmaps.getPlayerImage(p.getPlayerModel(), "front_three"), x, y, null);
                }
            }
        }
        
        // render S above other Spies if you yourself are a spy
        if(playState.getPlayer().isSpy() && p.isSpy() && 
                !(playState.getPlayer().getUsername().equals(p.getUsername()))){
            g.setColor(Color.BLACK);
            g.fillRect(x + 10, y - 14, 12, 12);
            g.setColor(Color.RED);
            g.fillRect(x + 11, y - 13, 10, 10);
            g.setColor(Color.WHITE);
            g.setFont(new Font(null, Font.BOLD, 10));
            g.drawString("S", x + 12, y -5);
        }
        
    }
    
    private void renderGUI (Graphics g, Player p){
    	boolean isSpy = p.getTeam();
    	
    	g.drawImage(Bitmaps.getPlayerImage(p.getPlayerModel(), "ui"), 
    	        6, 185, 32, 32, null);
    	
    	
    	g.setColor(Color.black);
    	g.fillRect(6, 208, 120, 10);
    	
    	g.setColor(Color.white);
    	g.fillRect(7, 209, 118, 8);
    	
    	g.setColor(Color.GREEN);
    	g.fillRect(7, 209, (int) (118f * ((float) p.getHealth()/Player.MAX_HEALTH)), 8);
    	
    	g.setColor(Color.black);
        g.fillRect(38, 193, 45, 16);
    	if (isSpy){
    		g.setColor(Color.red);
    		g.fillRect(38, 194, 44, 14);
    		g.setColor(Color.white);
    		g.setFont(new Font(null, Font.BOLD, 12));
            g.drawString("SPY", 44, 206);
    	} else {
    	    g.setColor(Color.LIGHT_GRAY);
            g.fillRect(38, 194, 44, 14);
            g.setColor(Color.black);
            g.setFont(new Font(null, Font.BOLD, 12));
            g.drawString("GUY", 44, 206);
    	}
    	
    	g.setFont(new Font(null, Font.PLAIN, 10));
    	g.setColor(Color.black);
    	g.drawString(NetworkWrapper.username, 10, 216);

    	
    	g.setColor(Color.BLACK);
    	g.fillOval(373, 181, 36, 36);
    	g.setColor(Color.white);
    	g.fillOval(374, 182, 34, 34);
    	
        if (p.getWeapon() != null) {
            g.drawImage(p.getWeapon().flatBitmap, 375, 183, null);
        }
    	
    }



}
