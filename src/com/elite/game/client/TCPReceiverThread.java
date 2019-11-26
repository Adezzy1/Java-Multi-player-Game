package com.elite.game.client;

import java.io.IOException;

/**
 * Thread designed to run on the client side and continuously read in String objects
 * and store them in the tcpInQueue in <b>NetworkWrapper</b> where they can be
 * accessed by whatever game state is currently active.
 * 
 * <p>ClassCastExceptions are caught and then the offending objects are stored in
 * the tcpObjectInQueue where they can be processed correctly assuming some other
 * part of the Client knows how to handle it.</p> 
 * 
 * @author Fraser Brooks
 *
 */
public class TCPReceiverThread extends Thread {

    public void run() {
        while (!Thread.interrupted()) {
            Object fromServer = null;
            try {
                fromServer = NetworkWrapper.ois.readObject();
                String stringFromServer = (String) fromServer;
                NetworkWrapper.tcpInQueue.offer(stringFromServer.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error: ReceiverThread: IOException ");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Error: ReceiverThread: IOException ");
            } catch (ClassCastException e){
                System.out.println("Received Non String object from Server");
                NetworkWrapper.tcpObjectInQueue.offer(fromServer);
            }
            
        }
    }
}