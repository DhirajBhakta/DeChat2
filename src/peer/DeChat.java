/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer;

import java.util.Scanner;
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
       String input,username,msg;
        while(true){
          input = scan.nextLine();
          String[] list = input.split(":");
          username = list[0];
          msg = list[1];
          CE.sendMsg(username, msg);
            
       } 
    }
}
