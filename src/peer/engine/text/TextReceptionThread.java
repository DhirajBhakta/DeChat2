/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer.engine.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author root
 */
public class TextReceptionThread extends Thread {
    protected Socket clientsocket;
    InputStream inp;
    BufferedReader binp;
    String msg;
    
    
    public TextReceptionThread(Socket clientSocket){
        this.clientsocket = clientSocket;
        System.err.println("recieved a new request....in TestReceptionThread");

    }
    
    public void run(){
        System.err.println("inside RUN() of TextReceptionThread.()");

        inp  = null;
        binp = null;
        msg = null;
        try{
            inp = clientsocket.getInputStream();
            binp = new BufferedReader(new InputStreamReader(inp));
        }catch(IOException ioe){
            System.err.println("ERROR: While MsgReceptionThread run()");
            ioe.printStackTrace();
        }
        
        while(true){
            try{
                //get recieved msg
                msg = binp.readLine();
                System.err.println("recieved a new MESSAGE!!!!YAY");

                //dump this msg assoiating it to who sent it , in DB maybe
                
                //WHen the user at the other end deliberately sends a message to serverscoket of some other peer ,then force him to send
                //"END" message to this peer ,so that this guy's TCP connection can be closed.
                if(msg.equals("END")){
                    binp.close();
                    inp.close();
                    clientsocket.close();                    
                    return;
                }
                System.out.println("message recieved:"+msg);
                
            }catch(IOException ioe){
                System.err.println("ERROR: while recieving messages.MsgReceptionThread  ");
            }
        }
    }
    
    
}