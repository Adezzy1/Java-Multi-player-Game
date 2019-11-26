package com.elite.framework.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The main Server class that is ran on an AWS server.
 * 
 * <p>Constantly listens for new clients and creates a new <b>ConnectedPlayer</b>
 * object for each connected player.</p>
 * 
 * <p> Starts up and owns three Threads:
 * <ul>
 * <li><b>ServerDatagramListener</b> to catch incoming UDP messages</li>
 * <li><b>ServerDatagramHandler</b> to forward each UDP message 
 *              caught by the above to the correct <b>GameInstance</b></li>
 * <li><b>GameEndListener</b> Forever check for and remove any games that have ended
 *              from the list of running games.</li> 
 * </ul></p>
 * 
 * @author Fraser Brooks
 * @author Beenita Shah
 *
 */
public class Server{

    private ArrayList<GameInstance> runningGames;
    private BlockingQueue<DatagramPacket> packetQueue;
    private DatagramSocket datagramSocket;
    private GameEndListener gameEndListener;
    
    public Server(int port) throws SocketException {
        datagramSocket = new DatagramSocket(port);
        runningGames = new ArrayList<>();
        packetQueue = new LinkedBlockingQueue<>();
        gameEndListener = new GameEndListener(runningGames);
        gameEndListener.start();
    }

    public static void main(String[] args) {

        Server server = null;
        ServerSocket serverMainSocket = null;
        ServerDatagramListener datagramListener = null;
        ServerDatagramHandler datagramHandler = null;
        
        try {
            serverMainSocket = new ServerSocket(Ports.MAINPORTNUMBER);
            server = new Server(Ports.UDPPORT);
            datagramListener = new ServerDatagramListener(server);
            datagramHandler = new ServerDatagramHandler(server);
            datagramListener.start();
            datagramHandler.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Couldn't listen on port " + Ports.MAINPORTNUMBER);
            System.exit(1);
        }

        while (true) {
            try {
                // Loop forever listening for new players
                while (true) {

                    // wait for next player to connect
                    Socket socket = serverMainSocket.accept();

                    // Create input and output streams
                    ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());

                    System.out.println("Someone has connected");

                    (new ConnectedPlayer(socket, server, toClient, fromClient)).start();

                }
            } catch (IOException e) {
                System.err.println("IO error " + e.getMessage());

            } finally {
                try {
                    serverMainSocket.close();
                    datagramListener.interrupt();
                    datagramHandler.interrupt();;
                } catch (Exception e) {
                    // Nothing to do
                }
            }
        }

    }

    public boolean newGame(String password) {

        for (GameInstance game : runningGames) {
            if (game.getGamePassword().equals(password)) {
                return false;
            }
        }
        return runningGames.add(new GameInstance(password));
    }

    public boolean joinGame(ConnectedPlayer cp, String password) {
        for (GameInstance game : runningGames) {
            if (game.getGamePassword().equals(password)) {
                return game.addPlayer(cp);
            }
        }
        return false;
    }
    
    public GameInstance getGame(String password){
        for (GameInstance game : runningGames) {
            if (game.getGamePassword().equals(password)) {
                return game;
            }
        }
        return null;
    }

    
    public boolean offerPacket(DatagramPacket p){
        return packetQueue.offer(p);
    }
    
    public DatagramPacket getPacket() throws InterruptedException{
        return packetQueue.take();
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }
    
    private class GameEndListener extends Thread{
        
        private ArrayList<GameInstance> runningGames;
        
        public GameEndListener(ArrayList<GameInstance> games){
            runningGames = games;
        }
        
        public void run(){
            try {
                while(true){
                    Thread.sleep(500);
                    int numberOfGames = runningGames.size();
                    for(int j = 0; j < numberOfGames ; j++){
                        if(runningGames.get(j).isAlive() == false){
                            String gameName = runningGames.get(j).getGamePassword(); 
                            runningGames.remove(j);
                            System.out.println(gameName + " game removed from running games");
                            numberOfGames -= 1;
                        }
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("GameEndListener interrupted");
            }
        }
    }
    
}
