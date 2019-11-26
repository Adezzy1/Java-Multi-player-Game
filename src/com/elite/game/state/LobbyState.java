package com.elite.game.state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import com.elite.game.assets.Bitmaps;
import com.elite.game.client.NetworkWrapper;
import com.elite.game.entity.Map;
import com.elite.game.entity.OtherPlayer;
import com.elite.game.entity.Player;

/**
 * State to represent the game before the players have all joined
 * and before the host has started the game.
 * 
 * Players can change their character model and the host can select
 * the desired map to play. Upon doing so, the game will start.
 * 
 * @author Beenita Shah
 * @author Fraser Brooks
 *
 */
public class LobbyState extends State{
	
    private ArrayList<OtherPlayer> otherPlayers;
    private ArrayList<String> mapsAvailableToAllPlayers;
    private Player player;
    private Map map;
    private float updateTimer;
    private final float UPDATE_TIMER_MAX = 1f;
    
    
	public LobbyState() {
		otherPlayers = new ArrayList<>();
		player = new Player();
        player.setUsername(NetworkWrapper.username);
        updateTimer = UPDATE_TIMER_MAX;
        mapsAvailableToAllPlayers = new ArrayList<>();
        String[] maps = Map.getAvailableMaps();
        
        try {
            NetworkWrapper.oos.writeObject(maps);
        } catch (IOException e) {
            System.err.println("Error: could not send maps") ;
            e.printStackTrace();
        }
	}

	@Override
	public void init() {
        player.resetPlayer();
        for(OtherPlayer op : otherPlayers){
            op.resetPlayer();
        }
	}

    @Override
    public void update(float delta) {

        String serverMessage = "";

        updateTimer -= delta;
        if(updateTimer < 0){
            int currentModelIndex = Bitmaps.player_model_names.indexOf(player.getPlayerModel());
            NetworkWrapper.sendTCP(("model~"+ currentModelIndex).getBytes());
            updateTimer = UPDATE_TIMER_MAX;
        }
        
        // this is where the game freezes before starting the
        // game. This code below needs to be in a GameLobbyState
        byte[] fromServer = NetworkWrapper.getTCPMessage();
        if (fromServer == null) {
            return;
        } else {
            serverMessage = new String(fromServer);
        }

        // check for new player being added to the game
        if (serverMessage.split(":")[1].equals("player")) {
            String playerName = serverMessage.split("player:")[1];
            boolean alreadyAddedToGame = false;
            for (OtherPlayer op : otherPlayers) {
                if (op.getUsername().equals(playerName)) {
                    alreadyAddedToGame = true;
                }
            }
            if (!alreadyAddedToGame) {
                System.out.println("Adding '" + playerName + "' to the game");
                otherPlayers.add(new OtherPlayer(playerName));
            }
            return;

        } else if (serverMessage.matches(".*is_spy.*")) {
            System.out.println(serverMessage);
            String spyName = serverMessage.split("is_spy:")[1]; // get spy name

            if (spyName.equals(player.getUsername())) { 
                player.setSpy(true);
            }
            
            for (OtherPlayer op : otherPlayers) {
                if (op.getUsername().equals(spyName)) { 
                    op.setSpy(true);
                }
            }


        }
        // check for "GameStart!" message from server
        else if (serverMessage.matches(".*GameStart.*")) {
            NetworkWrapper.sendTCP((player.getUsername() + " is starting game").getBytes());
            setCurrentState(new PlayState(this, map));
            
        }
        else if (serverMessage.matches(".*player_has_quit.*")) {
            String playerThatQuit = serverMessage.split("has_quit:")[1];
            int quitIndex = -1;
            for(int i = 0; i < otherPlayers.size(); i++){
                if(otherPlayers.get(i).getUsername().equals(playerThatQuit)){
                    quitIndex = i;
                }
            }
            if(quitIndex != -1) otherPlayers.remove(quitIndex);
        }
        
        // check for "GameStart!" message from server
        else if (serverMessage.matches(".*map_selected~.*")) {
            int mapChoiceIndex = 
                    Integer.parseInt(serverMessage.split("map_selected~")[1]);
            String mapName = mapsAvailableToAllPlayers.get(mapChoiceIndex);
            Bitmaps.loadTiles(mapName);
            map = new Map();
            map.initMap(mapName + "/" + mapName + ".csv");
            if (NetworkWrapper.isHost) {
                NetworkWrapper.sendTCP("sending_map".getBytes());
                NetworkWrapper.sendMap(map);
            }
        } 
        else if (serverMessage.matches(".*map_option.*")) {
            String mapName = serverMessage.split("ap_option:")[1];
            if(!mapsAvailableToAllPlayers.contains(mapName)){
                mapsAvailableToAllPlayers.add(mapName);
            }
        
        }else if (serverMessage.matches(".*model.*")) {
            //System.out.println(serverMessage);
            String senderName = serverMessage.split(":")[1].split("~")[0];
            for(OtherPlayer op : otherPlayers){
                if(op.getUsername().equals(senderName)){
                    int index = Integer.parseInt(serverMessage.split(":")[1].split("~")[2]);
                    op.setPlayerModel(Bitmaps.player_model_names.get(index));
                }
            }
        
        }else {
            System.out.println("Unrecognized TCP message received in lobby: ");
            System.out.println(serverMessage);
        }

    }

	@Override
	public void render(Graphics g, int x, int y) {
		
	    g.setColor(new Color(0.8f, 0.3f, 0.2f));
	    g.drawString(player.getUsername(), 25, 40);
	    g.setColor(new Color(0f, 0f, 0f));
	    g.drawString("[<] ", 95, 40);
	    g.drawString("[>] ", 130, 40);
	    g.drawImage(
                Bitmaps.getPlayerImage(
                        player.getPlayerModel(), 
                        "ui" ),
                110, 28, 16, 16, null);
	    
	    
	    for(int i = 0; i < otherPlayers.size(); i++){
	        g.setColor(new Color(0.8f, 0.3f, 0.2f));
	        g.drawString(otherPlayers.get(i).getUsername(), 25, 40 + 25*(i+1));
	        g.setColor(new Color(0f, 0f, 0f));
	        g.drawString("[<] ", 95, 40 + 25*(i+1));
	        g.drawString("[>] ", 130, 40 + 25*(i+1));
	        g.drawImage(
	                Bitmaps.getPlayerImage(
	                        otherPlayers.get(i).getPlayerModel(), 
	                        "ui" ),
	                110, 28 + 25*(i+1), 16, 16, null);
		}
	    
	    
	    for(int i = 0; i < mapsAvailableToAllPlayers.size(); i++){
	        g.setColor(new Color(0.8f, 0.3f, 0.2f));
            g.drawString(mapsAvailableToAllPlayers.get(i), 200,  40 + 25*(i));
            g.setColor(Color.BLACK);
            g.drawString(": Press " + (i + 1), 325, 40 + 25*(i));
        }
	    
	    g.setColor(new Color(0f, 0f, 0f));
	    g.setFont(new Font(null, Font.BOLD, 14));
	    g.drawString("Available Maps:", 195, 20);
	    g.drawString("Players:", 20, 20);
	    
	    g.setFont(new Font(null, Font.PLAIN, 12));
	    for(int i = 0; i < 14 ; i ++){
	        g.drawString("|", 175, 20 * (i + 1));
	    }
	    if(NetworkWrapper.isHost){
	        g.setFont(new Font(null, Font.PLAIN, 10));
	        g.drawString("Select map to start the game", 180, 218);
	    }else{
	        g.drawString("Waiting for host to start the game...", 225, 218);
	    }
	    
	}
	
	
	public ArrayList<OtherPlayer> getOtherPlayers(){
	    return otherPlayers;
	}
	
	public Player getPlayer(){
	    return player;
	}

	@Override
	public void onClick(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKeyPress(KeyEvent e) {
	    
	    int numKeyPressed = -1;
        if (e.getKeyCode() == KeyEvent.VK_1) {
            numKeyPressed = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_2) {
            numKeyPressed = 2;
        } else if (e.getKeyCode() == KeyEvent.VK_3) {
            numKeyPressed = 3;
        } else if (e.getKeyCode() == KeyEvent.VK_4) {
            numKeyPressed = 4;
        } else if (e.getKeyCode() == KeyEvent.VK_5) {
            numKeyPressed = 5;
        } else if (e.getKeyCode() == KeyEvent.VK_6) {
            numKeyPressed = 6;
        } else if (e.getKeyCode() == KeyEvent.VK_7) {
            numKeyPressed = 7;
        } else if (e.getKeyCode() == KeyEvent.VK_8) {
            numKeyPressed = 8;
        } else if (e.getKeyCode() == KeyEvent.VK_9) {
            numKeyPressed = 9;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            int currentModelIndex = Bitmaps.player_model_names.indexOf(player.getPlayerModel());
            int newIndex = currentModelIndex + 1;
            if (newIndex >= Bitmaps.player_model_names.size()) {
                newIndex = 0;
            }
            player.setPlayerModel(Bitmaps.player_model_names.get(newIndex));
            NetworkWrapper.sendTCP(("model~" + newIndex).getBytes());
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            int currentModelIndex = Bitmaps.player_model_names.indexOf(player.getPlayerModel());
            int newIndex = currentModelIndex - 1;
            if (newIndex < 0) {
                newIndex = Bitmaps.player_model_names.size() - 1;
            }
            player.setPlayerModel(Bitmaps.player_model_names.get(newIndex));
            NetworkWrapper.sendTCP(("model~" + newIndex).getBytes());
        }
		
        if(numKeyPressed != -1){
            
            if(numKeyPressed > mapsAvailableToAllPlayers.size()){
                return;
            }else{
                int optionSelected = numKeyPressed -1;
                if (NetworkWrapper.isHost == true) {
                    System.out.println("Recognised as host");
                    byte[] message = ("starting_game" + 
                    "~" + "map_choice~" + optionSelected).getBytes();
                    NetworkWrapper.sendTCP(message);
                }
            }
            
            
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
