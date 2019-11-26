package com.elite.framework.server;

import java.awt.Point;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.elite.framework.misc.RandomNG;

/**
 * A class to represent a running game on the <b>Server</b> object that owns it.
 * 
 * <p>A 'running game' consists of a game password/key that players need to join and 
 * all the players/clients that have connected so far, stored as <b>ConnectedPlayer</b> objects.
 * </p><p>
 * At first all players connected are in the <b>LobbyState</b> on their machine and the
 * GameInstance waits for the host to start the game via a special TCP message.
 * </p><p>
 * Once the host has decided to start the game, no more players can join, all players are
 * informed to start their game and transition into their <b>PlayState</b>s and this GameInstance
 * object starts listening for UDP messages and forwarding them to all players in the game.
 * </p><p>
 * Once the game is over the GameInstance loops back around and more players can connect
 * again. The GameInstance thread runs untill all connected players have quit at which point this
 * game and it's associated game key/password will be removed from the 
 * <b>Server</b>'s list of running games.
 * </p>
 * @author Fraser Brooks
 * @author Beenita Shah
 * @author Sabeeka Qureshi
 */
public class GameInstance extends Thread{

    private ArrayList<ConnectedPlayer> clients;
    private BlockingQueue<DatagramPacket> updates;
    
    private HashMap<Integer, ArrayList<Point>> weaponCoords;
    
    private boolean gameStarted;
    private ArrayList<String> mapsAvailableToAll;
    
    private String gamePassword = null;
    private boolean gameover = false;
    
    
    GameInstance(String password){
        gamePassword = password;
        clients = new ArrayList<>();
        gameStarted = false;
        mapsAvailableToAll = new ArrayList<>();
        updates = new LinkedBlockingQueue<>();
        this.setName("gameThread: " + gamePassword);
        this.start();
    }
    
    /**
     * The main game loop here on the Server.
     */
    public void run() {
        try {

            while (!clients.isEmpty()) {
                
                gameStarted = false;
                weaponCoords = null;
                
                
                for(ConnectedPlayer cp : clients){
                    System.out.println("removing all TCP from " + cp.getUsername());
                    cp.tcpInQueue.clear();
                }
                
                
                while (!gameStarted) {
                    System.out.println("game not started");
                    Thread.sleep(500);
                    
                    
                    int numberOfClients = clients.size();
                    for(int j = 0; j < numberOfClients; j++){
                        if(clients.get(j).playerHasQuit()){
                            for(ConnectedPlayer cp : clients){
                                cp.sendTCP( "player_has_quit:" + clients.get(j).getUsername());
                            }
                            System.out.println("removing " + clients.get(j).getUsername() + " from game");
                            clients.remove(j);
                            numberOfClients -= 1;
                        }
                    }
                    
                    if(clients.isEmpty()){
                        break;
                    }
                    
                    mapsAvailableToAll.clear();
                    mapsAvailableToAll.addAll(Arrays.asList(clients.get(0).getAvailableMaps()));

                    for (ConnectedPlayer cp : clients) {
                        ArrayList<String> maps = new ArrayList<String>(Arrays.asList(cp.getAvailableMaps()));
                        int k = mapsAvailableToAll.size();
                        for (int i = 0; i < k; i++) {
                            if (!maps.contains(mapsAvailableToAll.get(i))) {
                                mapsAvailableToAll.remove(i);
                                k -= 1;
                            }
                        }
                    }

                    for (ConnectedPlayer cp : clients) {
                        for (String mapName : mapsAvailableToAll) {
                            cp.sendTCP("map_option:" + mapName);
                        }
                        String tcpMessage = cp.tcpInQueue.poll();
                        if (tcpMessage == null) {
                            continue;
                        }
                        // Client's currently selected player model/character
                        if (tcpMessage.matches(".*model.*")) {
                            for (ConnectedPlayer recipient : clients) {
                                if (!recipient.getUsername().equals(cp.getUsername())) {
                                    recipient.sendTCP(cp.getUsername() + "~" + tcpMessage);
                                }
                            }
                        } else if (tcpMessage.matches(".*starting_game~.*")) {
                            int mapChoiceIndex = Integer.parseInt(tcpMessage.split("map_choice~")[1]);
                            for (ConnectedPlayer recipient : clients) {
                                recipient.sendTCP("map_selected~" + mapChoiceIndex);
                            }
                        }
                    }
                    // Send player names to each client
                    for (ConnectedPlayer cp : clients) {
                        for (ConnectedPlayer player : clients) {
                            if (!player.getUsername().equals(cp.getUsername())) {
                                cp.sendTCP("player:" + player.getUsername());
                            }
                        }
                    }

                    if (weaponCoords != null) {
                        // game starts once we have receive the coordinates of weapons from host
                        startGame();
                    }
                    
                }
                
                if(clients.isEmpty()){
                    break;
                }

                
                // TODO
                // Create AI players as necessary
                
                
                // Assign teams and inform all clients
                assignTeams();

                
                System.out.println("starting game on server");
                for (ConnectedPlayer cp : clients) {
                    cp.sendTCP("GameStart");
                    
                    System.out.println(cp.getUsername() + " is a spy = " + cp.isSpy());
                    
                }

                
                // Main game loop:
                // listen for/send updates from/to clients
                for(ConnectedPlayer cp : clients){
                    cp.setPlayerHasDied(false);
                    System.out.println("removing all TCP from " + cp.getUsername());
                    cp.tcpInQueue.clear();
                }
                for(int i = 0 ; i < clients.size(); i++){
                    System.out.println("player " + i + " = " + clients.get(i).getUsername() );
                }
                
                
                gameover = false;
                boolean somePlayersLeft = true;
                while (!gameover && somePlayersLeft) {

                    somePlayersLeft = false;
                    for(ConnectedPlayer cp : clients){
                        if(cp.playerHasQuit() == false){
                            somePlayersLeft = true;
                        }else{
                            for(ConnectedPlayer cp2 : clients){
                                cp2.sendTCP("player_disconnect:" + cp.getUsername() );
                            }
                            cp.setPlayerHasDied(true);
                        }
                    }
                    
                    DatagramPacket[] updatesFromClients = getUpdates();

                    for (DatagramPacket packet : updatesFromClients) {
                        handlePacket(packet);
                    }

                    boolean spies_alive = false;
                    boolean guys_alive = false;

                    for(int i = 0 ; i < clients.size(); i++){
                        System.out.print("player " + i + " = " + clients.get(i).getUsername() );
                        System.out.println("    is dead = " + clients.get(i).playerHasDied());
                    }
                    
                    if (clients.size() == 1) {
                        spies_alive = true;
                        guys_alive = true;
                    }

                    for (ConnectedPlayer cp : clients) {

                        if (!cp.playerHasDied()) {
                            if (cp.isSpy()) {
                                spies_alive = true;
                            } else {
                                guys_alive = true;
                            }
                        } 

                        String fromCP = cp.tcpInQueue.poll();
                        while (fromCP != null) {
                            handleTCP(fromCP, cp);
                            fromCP = cp.tcpInQueue.poll();
                        }
                    }

                    if (!spies_alive && !guys_alive) {
                        gameover = true;
                        for (ConnectedPlayer cp : clients) {
                            cp.sendTCP("gameover~draw");
                        }
                    } else if (!spies_alive) {
                        gameover = true;
                        for (ConnectedPlayer cp : clients) {
                            cp.sendTCP("gameover~guys_win");
                        }
                    } else if (!guys_alive) {
                        gameover = true;
                        for (ConnectedPlayer cp : clients) {
                            cp.sendTCP("gameover~spies_win");
                        }
                    }

                }
                System.out.println(gamePassword + ": gameover ");
            }
            
            
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
        System.out.println("All players disconnected from game with key " + gamePassword
                + ". Ending Thread.");
        
        
    }
    
    
    public String getGamePassword() {
        return gamePassword;
    }
    
    public void startGame() {
    	gameStarted = true;
    }
    
    public boolean gameInPlayState(){
        return gameStarted;
    }
    
    public void setWeaponCoords(HashMap<Integer, ArrayList<Point>> wc){
        this.weaponCoords = wc;
    }
    
    public ArrayList<ConnectedPlayer> getClients() {
        return clients;
    }
    
    /**
     * take a datagram packet and send it to all clients except the one that sent it
     * @param packet
     */
    private void handlePacket(DatagramPacket packet){
        String updateString = new String (packet.getData());
        String[] split = updateString.split("@", 3);
        String playerName = split[1];
        String message = split[2];
        // reroute packet to all clients except the one that sent it
        for(ConnectedPlayer cp: clients){
            if (!cp.getUsername().equals(playerName)){
                cp.sendUDP(playerName + message);
            }else{
                cp.setPlayerPort(packet.getPort());
            }
        }
    }
    
    /**
     * take a TCP message/update from a player and apply any relevant updates here 
     * in the GameInstance and send to all players as necessary
     * @param tcpIn - the TCP message received 
     * @param cp - the ConnectedPlayer that sent this TCP message 
     */
    private void handleTCP(String tcpIn, ConnectedPlayer cp) {
        //System.out.println(tcpIn + "\n\n");
        String[] splitMessage = tcpIn.split("@");
        if (splitMessage[0].equals("pickup")) {
            String[] coords = splitMessage[1].split(":");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            for (Integer i : weaponCoords.keySet()) {
                ArrayList<Point> points = weaponCoords.get(i);
                if (points == null) {
                    continue;
                }
                
                int numberOfWeapons = points.size();
                for (int k = 0; k < numberOfWeapons; k++) {
                    Point p = points.get(k);
                    if (p.x == x && p.y == y) {
                        points.remove(k);
                        numberOfWeapons -= 1;
                        for(ConnectedPlayer player : clients){
                            player.sendTCP(cp.getUsername() + ">>PickedUp>>" + i + "at" + x + "~" + y);
                        }
                    }

                }
            }
        }else if(tcpIn.contains("dropping")){
            String[] parts = tcpIn.split(">");
            String username = parts[0];
            String weaponID = parts[2];
            String x = parts[3];
            String y = parts[4];
            ArrayList<Point> points = weaponCoords.get(new Integer(weaponID));
            if(points == null){
                points = new ArrayList<>();
            }
            points.add(new Point(Integer.parseInt(x),
                    Integer.parseInt(y)));
            weaponCoords.put(new Integer(weaponID), points); 
            for(ConnectedPlayer player : clients){
                player.sendTCP(username + ">weaponDrop>" + weaponID + ">" + x + ">" + y );
            }
        }else if(tcpIn.matches(".*I am dead.*")){
            //System.out.println(cp.getUsername() + " is dead in this round");
            cp.setPlayerHasDied(true);
        }else if(tcpIn.matches(".*discovered.*")){
            String deadPlayer = tcpIn.split("discovered:")[1];
            for(ConnectedPlayer player: clients){
                player.sendTCP(">>" + cp.getUsername() + "~discovered~" + deadPlayer );
            }
        }
    }
    
    public boolean addPlayer(ConnectedPlayer cp) {
        System.out.println("trying to add: " + cp.getUsername() + " to game " + getGamePassword());
        if (gameStarted) {
            return false;
        } else {
            cp.setGameInstance(this);
            return clients.add(cp);
        }
    }
    
    /**
     * The Server catches all UDP messages and forwards to the correct game instance
     * via this method which placed the packet in a BlockingQueue to be processed by the next
     * pass of the loop in the run() method.
     * @param dataFromClient
     * @return true if successfully added to queue
     */
    public boolean addUpdate(DatagramPacket dataFromClient){
        return updates.offer(dataFromClient);
    }
    
    /**
     * Each call to this method will return the an array of all the Datagram Packets
     * that were in the BlockingQueue at the point this function is started.
     * @return - an array of DatagramPackets that need to be handled
     */
    public DatagramPacket[] getUpdates(){
        int size = updates.size();
        DatagramPacket[] allUpdates = new DatagramPacket[size];
        for(int i = 0; i < size; i++){
            allUpdates[i] = updates.poll();
        }
        return allUpdates;
    }
    
    /**
     * Assign teams on a 3:1 ratio randomly so teams are different every time.
     * Always at least one Spy.
     * @author Sabeeka Qureshi
     * @author Fraser Brooks
     */
    public void assignTeams() {

        for (ConnectedPlayer cp : clients) {
            cp.setSpy(false);
        }

        int numberOfSpies = 1;

        if (clients.size() > 4) {
            numberOfSpies = clients.size() / 4 + 1;
        }

        ArrayList<Integer> randomIndices = new ArrayList<>();

        while (randomIndices.size() != numberOfSpies) {
            int randomIndex = RandomNG.getRandInt(clients.size());
            while (randomIndices.contains(randomIndex)) {
                System.out.println("picking random spy");
                randomIndex = RandomNG.getRandInt(clients.size());
            }
            randomIndices.add(randomIndex);
        }

        for (int i : randomIndices) {
            System.out.println("setting " + clients.get(i).getUsername() + " to Spy");
            clients.get(i).setSpy(true);
        }

        for (ConnectedPlayer cp : clients) {
            for (ConnectedPlayer cp2 : clients) {
                if (cp2.isSpy()) {
                    cp.sendTCP("is_spy:" + cp2.getUsername());
                }
            }
        }

    }
    
    
}
