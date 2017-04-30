/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer.engine.text;

import java.io.*;
import java.net.*;
import common.*;

/**
 *
 * @author root
 */
public class TextEngine {
    ServerSocket serverSocket;//forever listening on 8888
    Socket selfSocket; //only WRITE to here
    PrintWriter out;
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
       System.err.println("about to start the listerning thread");
        new Thread(){
            @Override
            public void run() {
                
                while(true){
                    try{
                        Socket clientSocket = serverSocket.accept();
                        System.err.println("recieved a new request");
                        new TextReceptionThread(clientSocket).start();
                       }
                    catch(IOException ioe){
                        System.err.println("ERROR: TextEngine , start().");
                        ioe.printStackTrace();
                       }
                }
            }                       
        }.start();   
    }
    
    public void sendMsg(Peer destPeer, Message msgPkt){
        //Close previous state connection
        if(this.currentState!=null && !this.currentState.equals(destPeer)){
            try{
                out.flush();
                out.println("END");//signalling the old peer to close the TCP connection.
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
                 out=new PrintWriter(selfSocket.getOutputStream(),true);  

            }catch(Exception e){
                System.err.println("ERROR: TextEngine sendMsg() . while creating new currentstate");
                e.printStackTrace();
            }
        }
        
           out.println(msgPkt);
           //dump this message associating it to whom it was sent., maybe in DB
           out.flush();
      
        
        
    }
    
}









//----------------------------------------------------------------------------------------------


//ONLY meant for getting the messages....

