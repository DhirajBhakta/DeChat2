/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer;

import common.Constants;
import common.Peer;
import common.Query;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import peer.engine.text.TextEngine;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class ChatEngine {

    Peer user;
    HashMap<String, Peer> peerMap;
    TextEngine tEngine;

    public ChatEngine(String username) {
        peerMap = new HashMap<String, Peer>();
        tEngine = new TextEngine();
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
            // Send query
            new ObjectOutputStream(sock.getOutputStream()).writeObject(new Query("ALL", this.user));
            // Receive response
            HashMap<String, Peer> recvMap = (HashMap<String, Peer>) new ObjectInputStream(sock.getInputStream()).readObject();
            // Update the current map
            this.peerMap.clear();
            this.peerMap.putAll(recvMap);
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
         tEngine.sendMsg(targetPeer, msg);   
         return true;
        }
        else {
            System.out.println("Peer currently not connected");
            return false;
        }
    }

    public static void main(String[] args) {
        String username;

        // Temporary reading of username; will be done via GUI later
        System.out.print("Enter username? ");
        username = new Scanner(System.in).nextLine();

        // Instantiate ChatEngine object
        ChatEngine chatEngine = new ChatEngine(username);
    }
}