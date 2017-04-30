/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import common.Constants;
import common.Peer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class RendezousServer {

    public static ServerSocket serverSocket;
    public static HashMap<String, Peer> mainPeerMap;

    public static void startServer() {
        try {
            mainPeerMap = new HashMap<String, Peer>();
            System.out.println("Initialized Main Peer Map...");
            serverSocket = new ServerSocket(Constants.RENDEZOUS_SERVER_PORT);
            System.out.println("Started server at port " + Constants.RENDEZOUS_SERVER_PORT + "...");

            // Keep listening and passing sockets to threads
            while (true) {
                Socket csock = serverSocket.accept();
                System.out.println("Client connected from address " + csock.getInetAddress().getHostAddress());
                new RendezvousThread(csock, mainPeerMap).start();                
            }

        } catch (IOException ex) {
            Logger.getLogger(RendezousServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
