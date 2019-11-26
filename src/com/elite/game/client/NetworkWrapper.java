package com.elite.game.client;


import com.elite.game.entity.Map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;  

/**
 * Static class to store connection to server and associated data/objects.
 * 
 * <p>This class is static so as to allow easy access from anywhere in the game.</p>
 * 
 * <p> The biggest part of this class is the udp and tcp LinkedBlockingQueues which
 * store the messages that get sent and received over the network. These messages
 * contain the information needed to sync the gameplay over all clients.<p>
 * 
 * <p>The final string SERVER_ADDRESS below is set to the address of the 
 * Amazon Web Services Server but can optionally be changed to "localhost" if one
 * wishes to run the Server on their own machine.</p>
 * 
 * @author Fraser Brooks
 * @author Beenita Shah
 *
 */
public class NetworkWrapper{
    
    public static final String SERVER_ADDRESS = 
           "ec2-34-208-104-115.us-west-2.compute.amazonaws.com"; 
    //"localhost";
    public static final int MAINPORTNUMBER = 44776;
    public static final int UDPPORT = 44768;
    
    
	public static DatagramSocket datagramSocket;
	public static DatagramPacket dpClient;
	public static Socket socket;
	public static ObjectInputStream ois;
	public static ObjectOutputStream oos;
	public static InetAddress serverIP;
	
	
	public static BlockingQueue<byte[]> udpOutQueue;
	public static BlockingQueue<byte[]>udpInQueue;
	
	public static BlockingQueue<byte[]> tcpOutQueue;
    public static BlockingQueue<byte[]> tcpInQueue;
    public static BlockingQueue<Object> tcpObjectInQueue;
    
	public static UDPReceiverThread udpReceiverThread;
	public static UDPSenderThread udpSenderThread;
	
	public static TCPReceiverThread tcpReceiverThread;
    public static TCPSenderThread tcpSenderThread;
    
	
	public static String username = null;
	public static String gameKey = null;
	
	public static boolean isHost = false;
	
	public static Map mapWithWeaponsToBeSent;
	public static boolean weaponsToSend;
	
	public static void startConnection(){
	    try {
            socket = new Socket(SERVER_ADDRESS, MAINPORTNUMBER);
            datagramSocket = new DatagramSocket();
            
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            
            serverIP = socket.getInetAddress();
            
            udpOutQueue = new LinkedBlockingQueue<>();
            udpInQueue = new LinkedBlockingQueue<>();
            
            tcpOutQueue = new LinkedBlockingQueue<>();
            tcpInQueue = new LinkedBlockingQueue<>();
            tcpObjectInQueue = new LinkedBlockingQueue<>();
            
            udpReceiverThread = new UDPReceiverThread();
            udpSenderThread = new UDPSenderThread();
            udpReceiverThread.start();
            udpSenderThread.start();
            
            tcpReceiverThread = new TCPReceiverThread();
            tcpSenderThread = new TCPSenderThread();
            tcpReceiverThread.start();
            tcpSenderThread.start();
            
	    } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error connecting to server in Network Wrapper");
        }
	}
	
	public static void setUsername(String name){
	    username = name;
	}
	
	public static void setGameKey(String key){
	    gameKey = key;
	}
	
	public static void setIsHost(){
	    isHost = true;
	}
	
	public static void connectToGame(){
	    
	    try {
	        System.out.println(username + "  " + gameKey);
            oos.writeObject(username);// first thing server expects is username
            oos.flush();
            String secondMessage = "";
            if(isHost){
                secondMessage += "CG"; // this client is creating the game on the server
            } else{
                secondMessage += "JG"; // this client is joining an existing game on the server
            }
            secondMessage += gameKey;
            oos.writeObject(secondMessage);
            
            
        } catch (IOException e) {
            e.printStackTrace();
        } 
	}
	
	public static int udpAvailable(){
	    return udpInQueue.size();
	}
	
	public static int tcpAvailable(){
	    return tcpInQueue.size();
	}
	
	public static byte[] getTCPMessage(){
	    // will return null if the queue is empty
	    return tcpInQueue.poll();
	}
	
	public static byte[] getUDPMessage(){
	    // will return null if the queue is empty
	    return udpInQueue.poll();
	}
	
	public static byte[][] getUDPMessages(){
	    int size = udpInQueue.size();
        byte[][] allUpdates = new byte[size][];
        for(int i = 0; i < size; i++){
            allUpdates[i] = udpInQueue.poll();
        }
        return allUpdates;
	}
	
	public static String[] getTCPMessages(){
        int size = tcpInQueue.size();
        String[] allUpdates = new String[size];
        for(int i = 0; i < size; i++){
            allUpdates[i] = new String (tcpInQueue.poll());
        }
        return allUpdates;
    }
	
	public static boolean sendUDP(byte[] bytes){
	    return udpOutQueue.offer(bytes);
	}
	
	public static boolean sendTCP(byte[] bytes){
        return tcpOutQueue.offer(bytes);
    }
	
	public static void sendMap(Map map){
	    mapWithWeaponsToBeSent = map;
	    weaponsToSend = true;
	    
	}

 
}
