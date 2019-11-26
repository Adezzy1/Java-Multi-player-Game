package com.elite.game.state;


import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;

import com.elite.framework.misc.RandomNG;
import com.elite.game.assets.Bitmaps;
import com.elite.game.assets.Sounds;
import com.elite.game.client.NetworkWrapper;
import com.elite.game.client.ServerUpdateHandler;
import com.elite.game.engine.CollisionDetector;
import com.elite.game.engine.CoordinateConverter;
import com.elite.game.engine.Renderer;
import com.elite.game.engine.ShootHandler;
import com.elite.game.entity.Map;
import com.elite.game.entity.Announcement;
import com.elite.game.entity.OtherPlayer;
import com.elite.game.entity.Particle;
import com.elite.game.entity.Player;
import com.elite.game.entity.RandomAiPlayer;
import com.elite.game.weapons.Weapon;

/**
 * 
 * Class that represents the state of the game when it is being played
 * and the game is on going.
 * 
 * <p>The game has a list of Weapons that is mirrored on the server as
 * well as a list of all particle currently in existence and a list of all anouncements.
 * It also keeps a reference to the lobby state that created it so control flow can
 * loop back round to the lobby after the game is over.
 * </p>
 * 
 * <p> This class upon being instantiated created four helper classes that
 * it's functionality depends on. These are:
 * <ul>
 * <li> Renderer </li>
 * <li> CollisionDetector </li>
 * <li> ShootHandle </li>
 * <li> ServerUpdateHandler </li>
 * </ul></p>
 * 
 * @author Fraser Brooks
 * @author Sabeeka Qureshi
 * @author Beenita Shah
 * @author Adeola Aderibigbe
 * @author Andreea Merariu
 *
 */
public class PlayState extends State {
    
    private Renderer renderer;
    private CollisionDetector collisionDetector;   
    private ShootHandler shootHandler;
    private ServerUpdateHandler servereUpdateHandler;
    
    
    private ArrayList<Weapon> weapons;
    private ArrayList<Announcement> announcements;
    private ArrayList<Particle> particles;
    
    private ArrayList<RandomAiPlayer> aiPlayers;
    
    private LobbyState gameLobby;
    
    private float updateTickMax = 0.1f;
    private float updateTick;
      
    Map map;
    
    private byte[][] udpUpdates;
    private String[] tcpUpdates;
    
    public PlayState(LobbyState lobby, Map map) {
        gameLobby = lobby;
        this.map = map;
    }
    
    @Override
    public void init() {
        
        Sounds.playStateSound();
        
        updateTick = updateTickMax;
        
        renderer = new Renderer(this);
        collisionDetector = new CollisionDetector(this);
        shootHandler = new ShootHandler(this);
        servereUpdateHandler = new ServerUpdateHandler(this);
        
        weapons = new ArrayList<>();
        particles = new ArrayList<>();
        announcements = new ArrayList<>();
        
        aiPlayers = new ArrayList<>();
        initAiPlayers();
        
        map.initWeapons(this);
        map.spawnPlayerRandomly(getPlayer());
        
        System.out.println("Client Starting Game!");
    }

    @Override
    public void update(float delta) {

        applyUpdatesFromServer();
        
        for(OtherPlayer otherPlayer : getOtherPlayers()){
            if(!otherPlayer.isAlive){
                if(otherPlayer.getWeapon() != null){
                    map.dropWeapon(otherPlayer);
                }
            }
            otherPlayer.update(delta);
        }
        
        for(RandomAiPlayer aiPlayer : aiPlayers){
            aiPlayer.update(delta);
        }
        
        if (!getPlayer().isAlive()) {
            if(getPlayer().getWeapon() != null){
                int[] coords = map.dropWeapon(getPlayer());
                if (coords != null) {
                    NetworkWrapper.sendTCP(
                            (NetworkWrapper.username + ">dropping>" + coords[0] + ">" + coords[1] + ">" + coords[2])
                                    .getBytes());
                }
            }
        }

        getPlayer().update(delta);
        if (collisionDetector.hasCollidedWithWall(getPlayer())) {
            getGameLobby().getPlayer().bumpBack();
        }
        
        for(RandomAiPlayer ai : getAiPlayers()){
            if(collisionDetector.hasCollidedWithWall(ai)){
                ai.bumpBack();
            }
        }
        
        // detect collisions with weapons on the ground and
        // request pickup from server if unarmed
        collisionDetector.weaponCollisionDetection(getPlayer());
        
        int j = getParticles().size();
        for(int i = 0; i < j; i++){
            Particle p = getParticles().get(i);
            p.update(delta);
            if(p.isAlive() == false){
                getParticles().remove(i);
                j -= 1;
            }
        }
        
        updateAnimations(delta);
        
        sendUpdatesToServer(delta);
        
        for(Announcement m : getAnnouncements()){
            if(m.isVisible()){
                m.update(delta);
                break;
            }
        }
    }

    private void applyUpdatesFromServer() {
        udpUpdates = NetworkWrapper.getUDPMessages();
        for (byte[] update : udpUpdates) {
            servereUpdateHandler.handleUDP(update);
        }

        tcpUpdates = NetworkWrapper.getTCPMessages();
        for(String update : tcpUpdates){
            servereUpdateHandler.handleTCP(update);
        }
    }

    private void sendUpdatesToServer(float delta) {
        updateTick -= delta;
        if (updateTick < 0) {
            updateTick = updateTickMax;
            // System.out.println("sending coords to server");
            NetworkWrapper.sendUDP(
                    (">move>" + getPlayer().getX() + ">" 
                            + getPlayer().getY() + ">" 
                            + getPlayer().getXOffset() + ">" 
                            + getPlayer().getYOffset())
                            .getBytes());
        }
    }

    private void updateAnimations(float delta) {
        // Update all animations with delta
        for (Enumeration<String> e = Bitmaps.player_model_anims.keys(); e.hasMoreElements();){
            String key = e.nextElement();
            Bitmaps.player_model_anims.get(key).get("up").update(delta);
            Bitmaps.player_model_anims.get(key).get("left").update(delta);
            Bitmaps.player_model_anims.get(key).get("down").update(delta);
            Bitmaps.player_model_anims.get(key).get("right").update(delta);
        }
           
    }

    @Override
    public void render(Graphics g, int x, int y) {
        renderer.render(g);
    }   
    
    @Override
    public void onClick(MouseEvent e) {
        
    }
    
    @Override 
    public void onClickRelease(MouseEvent e){
        
        Component c = e.getComponent();
        int[] universalClickCoords = CoordinateConverter.screenToUniversal(
                ((float) e.getX())/c.getWidth(), 
                ((float) e.getY())/c.getHeight(),
                getPlayer());
          if(getShootHandler().playerShoot(universalClickCoords[0], universalClickCoords[1], getPlayer())){
              NetworkWrapper.sendUDP((">shoot>" + universalClickCoords[0] + ">" +  universalClickCoords[1] ).getBytes());
          }
    }

    @Override
    public void onKeyPress(KeyEvent e) {

        
        if (e.getKeyCode() == KeyEvent.VK_W){
            
            getGameLobby().getPlayer().addKeyPress(Player.UP_PRESSED);
            getGameLobby().getPlayer().updateDirection();

        }else if(e.getKeyCode() == KeyEvent.VK_A){
                
            getGameLobby().getPlayer().addKeyPress(Player.LEFT_PRESSED);
            getGameLobby().getPlayer().updateDirection();
                
        }else if(e.getKeyCode() == KeyEvent.VK_S){
            
            getGameLobby().getPlayer().addKeyPress(Player.DOWN_PRESSED);
            getGameLobby().getPlayer().updateDirection();
                
        }else if(e.getKeyCode() == KeyEvent.VK_D){

            getGameLobby().getPlayer().addKeyPress(Player.RIGHT_PRESSED);
            getGameLobby().getPlayer().updateDirection();
            
        } 
      
    }

    @Override
    public void onKeyRelease(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_W) {

            getGameLobby().getPlayer().removeKeyPress(Player.UP_PRESSED);
            getGameLobby().getPlayer().updateDirection();

        } else if (e.getKeyCode() == KeyEvent.VK_A) {

            getGameLobby().getPlayer().removeKeyPress(Player.LEFT_PRESSED);
            getGameLobby().getPlayer().updateDirection();

        } else if (e.getKeyCode() == KeyEvent.VK_S) {

            getGameLobby().getPlayer().removeKeyPress(Player.DOWN_PRESSED);
            getGameLobby().getPlayer().updateDirection();

        } else if (e.getKeyCode() == KeyEvent.VK_D) {

            getGameLobby().getPlayer().removeKeyPress(Player.RIGHT_PRESSED);
            getGameLobby().getPlayer().updateDirection();

        } else if (e.getKeyCode() == KeyEvent.VK_Q) {

            int[] coords = map.dropWeapon(getPlayer());
            Sounds.playGuncock();
            if (coords != null) {
                NetworkWrapper.sendTCP(
                        (NetworkWrapper.username + ">dropping>" + coords[0] + ">" + coords[1] + ">" + coords[2])
                                .getBytes());
            }
        } else if (e.getKeyCode() == KeyEvent.VK_E) {
            
            int i = getOtherPlayers().size();
            for(int j = 0; j < i ; j++){
                OtherPlayer op = getOtherPlayers().get(j);
                if(op.isAlive){
                    continue;
                }else{
                    int distance = CoordinateConverter.distance(
                            getPlayer().getX(), getPlayer().getY(), 
                            op.getX(), op.getY());
                    if(distance < 2){
                        NetworkWrapper.sendTCP(("discovered:" + op.getUsername()).getBytes());
                    }
                }
            }
            
        }
        
        
    }

    public Player getPlayer(){
        return getGameLobby().getPlayer();
    }
    
    public ArrayList<OtherPlayer> getOtherPlayers(){
        return getGameLobby().getOtherPlayers();
    }
    
    
    public ArrayList<Weapon> getWeapons(){
        return weapons;
    }
    
    public ArrayList<Particle> getParticles(){
        return particles;
    }
    
    public Map getMap(){
        return map;
    }
    
    public LobbyState getGameLobby() {
        return gameLobby;
    }

    public ShootHandler getShootHandler() {
        return shootHandler;
    }
    

    public ArrayList<Announcement> getAnnouncements() {
        return announcements;
    }

    public void initAiPlayers(){
        System.out.println("initAIPlayers");
        int count = 0;
        for(int i = 0; i < 20; i ++){
            byte x = (byte) RandomNG.randR(-50, 50);
            byte y = (byte) RandomNG.randR(-50, 50);
            while(map.isWall(x,y)){
                x = (byte) RandomNG.randR(-50, 50);
                y = (byte) RandomNG.randR(-50, 50);
                count++;
                if(count > 100){
                    return;
                }
            }
            aiPlayers.add(new RandomAiPlayer(x,y));
        }
    }
    
    public ArrayList<RandomAiPlayer>getAiPlayers(){
        return aiPlayers;
    }
    
    
}
