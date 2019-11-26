package com.elite.game.client;

import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Thread designed to run on the client side and continuously read in Datagram packets
 * and store their data contents in the udpInQueue in <b>NetworkWrapper</b> where 
 * they can be accessed by whatever game state is currently active.
 * 
 * @author Fraser Brooks
 */
public class UDPReceiverThread extends Thread{
    
    public void run(){
        byte[] data = new byte[64];
        DatagramPacket packet;
        while(!Thread.interrupted()){
            data = new byte[64];
            packet  = new DatagramPacket(data, data.length);
            try {
                NetworkWrapper.datagramSocket.receive(packet);
                //System.out.println("UDP received!");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error: ReceiverThread: IOException ");
            }
            NetworkWrapper.udpInQueue.offer(packet.getData());
        }
    }
}