package com.elite.framework.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

/**
 * Used by the <b>Server</b> to catch all incoming datagram packets and add to
 * a queue for the <b>ServerDatagramHandler</b> to process.
 * 
 * @author Fraser Brooks
 */
public class ServerDatagramListener extends Thread {
    
    private Server server;

    public ServerDatagramListener(Server server) throws SocketException{
        this.server = server;
    }
    
    public void run(){
        
        byte[] data;
        
        DatagramPacket packet;
        
        while(!Thread.interrupted()){
            data = new byte[32];
            packet  = new DatagramPacket(data, data.length);
            try {
                server.getDatagramSocket().receive(packet);
            } catch (IOException e) {
                System.out.println("Something went wrong when trying to recive packet in client.");
            }
            if (packet.getData()[0] == 126) {
                // 126 = "~" the special character used to
                // identify a message from client to server
                // (without this the server will try to receive all
                // udp messages it sends when testing on localhost)
                server.offerPacket(packet);
            }

        }

    }
    

}
