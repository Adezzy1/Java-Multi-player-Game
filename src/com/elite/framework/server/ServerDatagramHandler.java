package com.elite.framework.server;

import java.net.DatagramPacket;

/**
 * Forwards all received datagram packets to the correct <b>GameInstance</b>.
 * 
 * @author Fraser Brooks
 */
public class ServerDatagramHandler extends Thread{

    private Server server;
    private String fromClient;
    private String fromClient_; // with "~" removed
    private String[] splitUpdate;
    private String gamePassword;
    private GameInstance recipientGame;
    
    public ServerDatagramHandler(Server server){
        this.server = server;
        fromClient = "";
        splitUpdate = null;
        gamePassword = "";
        recipientGame = null;
    }
    
    public void run(){
        while(!Thread.interrupted()){
            try {
                DatagramPacket packet = server.getPacket();
                byte[] dataReceived = packet.getData();
                fromClient = new String(dataReceived).trim();
                
                // remove the "~" at the start of the message that 
                // denotes a message for the server
                fromClient_ = fromClient.split("~", 2)[1]; 
                
                // get password/ game key
                splitUpdate = fromClient_.split("@", 2);
                gamePassword = splitUpdate[0];
                
//                System.out.println("update:\n" + fromClient_ + 
//                        "\npassword:\n" + gamePassword );
                
                recipientGame = server.getGame(gamePassword);
                if (recipientGame == null){
                    System.err.println("Error: unrecognized game password in handler");
                    continue;
                }else{
                    recipientGame.addUpdate(packet);
                    recipientGame = null;
                }
                
                
                
                
                
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    
}
