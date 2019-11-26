package com.elite.framework.server;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * A ConnectedPlayer object represents a player that is connected 
 * to the <b>Server</b> that owns it.
 * 
 * <p>
 * This class contains the means by which to communicate with the player.
 * i.e. the <b>Socket</b>, the input and output streams, the player's IP address
 * ,and the port the player is sending/recieving UDP datagrams from.
 * </p>
 * 
 * @author Fraser Brooks
 *
 */
public class ConnectedPlayer extends Thread {

    public Socket toClient;
    public Server server;
    public ObjectInputStream ois;
    public ObjectOutputStream oos;
    
    
    public BlockingQueue<String> tcpInQueue;
    
    private Boolean inLobby;
    private GameInstance gameInstance;
    private String username;
    public String gamePassword;
    private String[] availableMaps;
    
    private InetAddress ip;
    private int playerPort;
    
    private boolean isSpy;
    private boolean playerHasDied;
    private boolean playerHasQuit;
    
    
    
    ConnectedPlayer(Socket toClient, Server server, 
            ObjectOutputStream oos, ObjectInputStream ois){
        this.toClient = toClient;
        this.server = server;
        this.ois = ois;
        this.oos = oos;
        inLobby = false;
        gameInstance = null;
        username = " NULL ";
        this.playerHasDied = false;
        this.isSpy = false;
        this.playerHasQuit = false;
        gamePassword = null;
        availableMaps = null;
        ip = toClient.getInetAddress();
        tcpInQueue = new LinkedBlockingQueue<>();
        
    }
    
    
    /**
     *  The main loop that runs so long as this player is connected.
     */
    @SuppressWarnings("unchecked")
    public void run() {
        String fromUser = null;

        try {
            username = (String) ois.readObject();
            //this.setName( "some player's Thread");
            System.out.println("received string from user");
            while (!Thread.interrupted()) {
                inLobby = false;
                while (!inLobby) {
                    fromUser = (String) ois.readObject();
                    switch (fromUser.substring(0, 2)) {
                    case "JG": // Join Game
                        gamePassword = fromUser.substring(2);
                        if (server.joinGame(this, gamePassword)) {
                            inLobby = true;
                            break;
                        }
                    case "CG": // Create Game
                        gamePassword = fromUser.substring(2);
                        if (server.newGame(gamePassword)) {
                            server.joinGame(this, gamePassword);
                            inLobby = true;
                            break;
                        }
                    default:
                        // shouldn't ever end up here
                        break;
                    }
                }

                availableMaps = (String[]) ois.readObject();

                tcpInQueue.clear();

                while (inLobby) {
                    Object objectFromUser = ois.readObject();
                    try {
                        String stringfromUser = (String) objectFromUser;
                        tcpInQueue.add(stringfromUser);
                    } catch (ClassCastException e) {
                        gameInstance.setWeaponCoords((HashMap<Integer, ArrayList<Point>>) objectFromUser);
                        System.out.println("Received weapon coords from host");
                    }

                }
                System.out.println(username + " has finished a game");
            }
            System.out.println(username + " has been interrupted");
        } catch (IOException e) {
            System.err.println("ConnectedPlayer: IOException: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("ConnectedPlayer: ClassNotFoundException: " + e.getMessage());
        }
        System.out.println(username + " has quit");
        
        // Attempt to close socket and streams
        try {
            oos.close();
        } catch (IOException e) {
            // Nothing to do
        }
        try {
            ois.close();
        } catch (IOException e) {
            // Nothing to do
        }

        try {
            toClient.close();
        } catch (IOException e) {
            // Nothing to do
        }
        
        
        playerHasQuit = true;
        // Exit point of Thread 

    }
    
    
    /**
     * Player's might want to create and share their own maps. To prevent a host selecting a
     * map that one player does not have, the list of available maps sent to the host is made
     * up of only the maps that are present on all player's machines.
     * 
     * @return - a String Array containing the list of maps that this player has on their machine
     */
    public String[] getAvailableMaps(){
        if(availableMaps != null){
            return availableMaps;
        }else{
            return new String[0];
        }
    }
    
    /**
     * 
     * @param gi - the gameInstance that this player has joined
     */
    public void setGameInstance(GameInstance gi){
        this.gameInstance = gi;
    }
    
    /**
     * Send a TCP message to this client. Potentially slower than UDP but guaranteed to arrive.
     * @param message - the message to send the client.
     * @return - true if the message was sent else false.
     */
    public boolean sendTCP(String message){
        String toSend = gamePassword + "@" + username + ":" + message;
        try {
            oos.writeObject(toSend);
            //System.out.println("sending tcp to " + username + "\n sending: " + toSend);
            return true;
        } catch (IOException e) {
            System.out.println("Error sending TCP to " + username + " @ " + ip.getHostAddress() );
            return false;
        }
        
    }
    
    /**
     * Send a UDP message to the client. Potentially faster than TCP but not guaranteed 
     * to arrive in order or at all.
     * @param message - the message to send the client.
     * @return - true if the message was sent else false.
     */
    public boolean sendUDP(String message){
        byte[] toSend = (gamePassword + "@" + username +":"+ message).getBytes();
        DatagramPacket dp = new DatagramPacket(toSend, toSend.length,
                                                                                                ip, Ports.UDPPORT );
        dp.setPort(getPlayerPort());
        try {
            server.getDatagramSocket().send(dp);
            //System.out.println("sending udp to " + username);
            return true;
        } catch (IOException e) {
            System.out.println("Error sending packet to " + username + " @ " + ip.getHostAddress() );
            return false;
        }

    }


    public int getPlayerPort() {
        return playerPort;
    }

    public String getUsername(){
        return username;
    }

    public void setPlayerPort(int playerPort) {
        this.playerPort = playerPort;
    }
    
    public void setSpy(boolean b){
    	isSpy = b;
    }

    
    public boolean isSpy(){
    	return isSpy;
    }
    
    public void setPlayerHasDied(boolean b){
        playerHasDied = b;
    }
    
    
    public boolean playerHasDied(){
        return playerHasDied == true;
    }
 
    public boolean playerHasQuit(){
        return playerHasQuit;
    }
    
}
