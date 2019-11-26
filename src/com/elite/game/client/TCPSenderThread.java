package com.elite.game.client;

import java.io.IOException;

/**
 * Thread designed to run on the client side and continuously send to the server 
 * the String objects stored in the tcpOutQueue in <b>NetworkWrapper</b>
 * 
 * A flag in <b>NetworkWrapper</b> is also used by the host's client to tell this 
 * thread to send the weapon coordinates after loading the map. So the server can
 * adjudicate weapon pickups (if two players walk over the same weapon at roughly
 * the same time, they shouldn't both pick up the weapon so the server needs to know
 * what weapons are on the map so it can decide who got there first).</p>
 * 
 * @author Fraser Brooks
 *
 */
public class TCPSenderThread extends Thread{
    
    public void run(){
        byte[] toSend;
        while(!Thread.interrupted()){
            try {
                toSend = NetworkWrapper.tcpOutQueue.take();
                System.out.println(NetworkWrapper.username + " sending  :" + new String(toSend));
                NetworkWrapper.oos.writeObject(new String(toSend));
                if(NetworkWrapper.weaponsToSend){
                    NetworkWrapper.oos.writeObject(NetworkWrapper.mapWithWeaponsToBeSent.getWeaponCoords());
                    NetworkWrapper.mapWithWeaponsToBeSent = null;
                    NetworkWrapper.weaponsToSend = false;
                }
                Thread.sleep(60);
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