/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer.engine.text;

import java.io.*;
import java.net.*;
import common.Constants;
import common.Peer;
import java.util.HashMap;

/**
 *
 * @author root
 */
public class TextEngine {
    ServerSocket serverSocket;//forever listening on 8888
    Socket selfSocket; //only WRITE to here
    DataOutputStream out;
    Peer currentState;
    
    public TextEngine() {
        this.currentState = null;
        try{
         serverSocket = new ServerSocket(Constants.TEXT_SERVER_PORT);        
        }catch(Exception e){
         System.err.println("ERROR: TextEngine ,while creating serverSocket.");
         e.printStackTrace();
        }
    }
    
    
    //starts listening 
    public void start(){
        while(true){
            try{
                Socket clientSocket = serverSocket.accept();
                new TextReceptionThread(clientSocket).start();
            }catch(IOException ioe){
                System.err.println("ERROR: TextEngine , start().");
                ioe.printStackTrace();
            }
        }
    }
    
    public void sendMsg(Peer destPeer, String msg){
        //Close previous state connection
        if(this.currentState!=null && !this.currentState.equals(destPeer)){
            try{
                out.flush();
                out.writeUTF("END");//signalling the old peer to close the TCP connection.
                out.flush();
                out.close();
                selfSocket.close();
            }catch(Exception e){
                System.err.println("ERROR: while closing previous connection (currentstate)");
                e.printStackTrace();
            }
        }
        //Update currentstate
        if(this.currentState == null || !this.currentState.equals(destPeer)){
            currentState = destPeer;
            try{
                 selfSocket = new Socket(destPeer.ip, Constants.TEXT_SERVER_PORT);
                 out=new DataOutputStream(selfSocket.getOutputStream());  

            }catch(Exception e){
                System.err.println("ERROR: TextEngine sendMsg() . while creating new currentstate");
                e.printStackTrace();
            }
        }
        try{
           out.writeUTF(msg);
           //dump this message associating it to whom it was sent., maybe in DB
           out.flush();
        }catch(IOException ioe){
           System.err.println("ERROR: TextEngine sendMsg(). while writing to DataOutput Stream.");
           ioe.printStackTrace();
        }
            
        
        
    }
    
}









//----------------------------------------------------------------------------------------------


//ONLY meant for getting the messages....

    