/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer;

import common.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import peer.engine.text.TextEngine;
import peer.engine.file.FileEngine;
import peer.engine.request.RequestEngine;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class ChatEngine {

    Peer user;
    long TIMESTAMP; // of last access to Rendezvous Server
    HashMap<String, Peer> peerMap;
    TextEngine tEngine;
    FileEngine fEngine;
    RequestEngine rEngine;
    
    public ChatEngine(String username) {
        TIMESTAMP = (long)0.00;
        peerMap = new HashMap<String, Peer>();
        tEngine = new TextEngine();
        fEngine = new FileEngine();
        rEngine = new RequestEngine(this);
        
        tEngine.start();
        fEngine.start();
        // Create an new Peer and assign it to ChatEngine object
        this.user = new Peer(this.getSystemInetAddress(), username);
        // Get other peer details from server
        this.updatePeerMap();
    }

    private String getSystemInetAddress() {
        String ip = "0.0.0.0";
        // Try to get wlan0 Interface
        NetworkInterface netIface;
        try {
            for(Enumeration<NetworkInterface> netIfaces = NetworkInterface.getNetworkInterfaces(); netIfaces.hasMoreElements();) {
                netIface = netIfaces.nextElement();
                
                if(netIface.isLoopback() || netIface.isVirtual() || !netIface.isUp()) {
                    // Ignore these interfaces
                    continue;
                }
                
                // Got the right interface (Assuming only one connected interface is present)
                for (Enumeration<InetAddress> iAddrs = netIface.getInetAddresses(); iAddrs.hasMoreElements();) {
                    InetAddress iAddr = iAddrs.nextElement();
                    if (iAddr instanceof Inet4Address) {
                        // Set this as IP
                        ip = iAddr.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(ChatEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Selected " + ip + " as the host IP Address");
        return ip;
    }

    public void updatePeerMap() {
        try {
            
            Socket sock = new Socket(Constants.RENDEZOUS_SERVER_ADDRESS, Constants.RENDEZOUS_SERVER_PORT);
            ObjectOutputStream t_out = new ObjectOutputStream(sock.getOutputStream());
            ObjectInputStream t_in   = new ObjectInputStream(sock.getInputStream());
            // Send query
            t_out.writeObject(new Query("ALL", this.user));
            t_out.flush();
            // Receive response
            HashMap<String, Peer> recvMap = (HashMap<String, Peer>)t_in.readObject();
            TIMESTAMP =(long)t_in.readLong();
            
            System.err.println("Abpout to print recieved map");
            printMap(recvMap);
            // Update the current map
            //this.peerMap.clear();
            //this.peerMap.putAll(recvMap);
            this.peerMap = (HashMap)recvMap.clone();
            System.err.println("printing updated map");
            printMap(this.peerMap);
            t_out.close();
            t_in.close();
            sock.close();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ChatEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Boolean sendMsg(String username, String msg) {
        Peer targetPeer = this.peerMap.get(username);
        if(targetPeer == null) {
            // Peer details not available
            this.updatePeerMap();
        }
        if(this.peerMap.get(username) != null) {
         targetPeer = this.peerMap.get(username);
         tEngine.sendMsg(targetPeer, getMessagePacket(msg));   
         return true;
        }
        else {
            System.out.println("Peer currently not connected");
            return false;
        }
    }
    
    
    
    public Boolean sendFile(String username,String pathToFile){      
        Peer targetPeer = this.peerMap.get(username);
        if(targetPeer == null) {
            // Peer details not available
            this.updatePeerMap();
        }
        if(this.peerMap.get(username) != null) {
         targetPeer = this.peerMap.get(username);
         Path p = Paths.get(pathToFile);
         File file = p.toFile();
         if(file.exists()){
             System.err.println("File exists");
         }
         else{
             System.err.println("File not there");
         }
         
         try{
         fEngine.sendFile(targetPeer, pathToFile);   
         }catch(Exception e){
             e.printStackTrace();
         }
         
         return true;
        }
        else {
            System.out.println("Peer currently not connected");
            return false;
        }
    }

    public Message getMessagePacket(String msg){
        Message msgPkt = new Message(this.user.username,msg);
        return msgPkt;
    }
    
    public static void printMap(Map mp) {
    Iterator it = mp.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry pair = (Map.Entry)it.next();
        System.out.println(pair.getKey() + " = " + pair.getValue());
        //it.remove(); // avoids a ConcurrentModificationException
    }
}
 
}
