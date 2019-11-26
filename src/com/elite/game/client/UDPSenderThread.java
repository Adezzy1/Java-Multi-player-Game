package com.elite.game.client;

import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Thread designed to run on the client side and continuously send to the server 
 * the byte arrays stored in the udpOutQueue in <b>NetworkWrapper</b>
 * 
 * @author Fraser Brooks
 */
public class UDPSenderThread extends Thread{
    
    public void run(){
        DatagramPacket packet;
        byte[] toSend;
        while(!Thread.interrupted()){
            try {
                byte[] fromQueue = NetworkWrapper.udpOutQueue.take();
                toSend = ("~" + NetworkWrapper.gameKey + "@" + NetworkWrapper.username + "@" + new String(fromQueue)).getBytes();
                //System.out.println("toSend:" + new String(toSend));
                packet = new DatagramPacket(toSend, toSend.length,
                        NetworkWrapper.serverIP, NetworkWrapper.UDPPORT); 
                NetworkWrapper.datagramSocket.send(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Error: SenderThread: InterruptedException ");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error: SenderThread: IOException ");
            }
            
        }
    }
}