/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer.engine.request;

import java.net.DatagramSocket;
import common.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author root
 */
public class RequestReceptionThread extends Thread{
    DatagramSocket recvSocket;
    DatagramPacket incomingRequestpkt;
    ByteArrayInputStream binp;
    ObjectInputStream inp;
    byte[] buffer;
    
    public RequestReceptionThread(){
        try {
            recvSocket = new DatagramSocket(Constants.MISC_UDP_RECV_PORT);
            buffer = new byte[65536];
            incomingRequestpkt = new DatagramPacket(buffer, buffer.length);
        } catch (SocketException ex) {
            Logger.getLogger(RequestReceptionThread.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    
    public void run(){
        while(true){
            try {
                recvSocket.receive(incomingRequestpkt);
                buffer = incomingRequestpkt.getData();
                binp = new ByteArrayInputStream(buffer);
                inp = new ObjectInputStream(binp);
                Message recievedRequest = (Message)inp.readObject();
                handleRequest(recievedRequest);
            } catch (Exception ex) {
                Logger.getLogger(RequestReceptionThread.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
    }
    
    
    
    public void handleRequest(Message request){
        switch(request.getMsg()){
            case "REQUEST_TIMESTAMP":
        }
        
    }
}
