/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.RendezousServer;
/**
 *
 * @author root
 */
public class DeChat {

    /**
     * @param args the command line arguments
     */
       public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String option = sc.nextLine();
        
        if(option.equals("SERVER")){
           RendezousServer.startServer();
        }
        else{
        String username;
        // Temporary reading of username; will be done via GUI later
        System.out.print("Enter username? ");
        username = new Scanner(System.in).nextLine();

        // Instantiate ChatEngine object
        ChatEngine chatEngine = new ChatEngine(username);
        
        
        
        //WARNING--dirty -test code ahead....:( :( :(
        new InputListener(chatEngine).start();
        }
    }
    
}




class InputListener extends Thread{
    ChatEngine CE;
    
    
    public InputListener(ChatEngine CE){
      this.CE = CE;
    }
    public void run()
    {  Scanner scan = new Scanner(System.in);
       String input,username,msg,pathToFile;
        while(true){
          input = scan.nextLine();
          String[] list = input.split(":");
          if(list[0].equals("FILE"))
          {
              username   = list[1];
              pathToFile = list[2];
              CE.sendFile(username, pathToFile);
          }
          else if(list[0].equals("UPDATE"))
          {
              new Thread(){
                  public void run(){
                      CE.requestTIMESTAMPFromAll();
                      try {
                          Thread.sleep(10000);
                      } catch (InterruptedException ex) {
                          Logger.getLogger(InputListener.class.getName()).log(Level.SEVERE, null, ex);
                      }
                      CE.requestPeerMapFromBestPeer();
                      
                  }
              }.start();
          }
          else
          {
              username = list[0];
              msg = list[1];
              CE.sendMsg(username, msg);
          }
          
            
       } 
    }
}
