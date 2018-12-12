package chat;

import java.io.*; 
import java.net.*; 
import java.util.Scanner; 

public class Client  
{ 
 final static int ServerPort = 1234; 

 public static void main(String args[]) throws UnknownHostException, IOException  
 { 
     Scanner scn = new Scanner(System.in); 
       
     // getting localhost ip 
     //IP adress from the server
     InetAddress ip = InetAddress.getByName("localhost"); 

    
     
     // establish the connection 
     Socket s = new Socket(ip, ServerPort); 
       
     // obtaining input and out streams 
     DataInputStream dis = new DataInputStream(s.getInputStream()); 
     DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
     
     
  // username thread 
     Thread sendUsername = new Thread(new Runnable()  
     { 
         @Override
         public void run() { 
             while (true) { 

                 // read the message to deliver. 
            	 String username = scn.nextLine(); 
            	 username=username+"##*";
                   
                 try { 
                     // write on the output stream 
                     dos.writeUTF(username); 
                 } catch (IOException e) { 
                     e.printStackTrace(); 
                 } 
             } 
         } 
     });

     // sendMessage thread 
     Thread sendMessage = new Thread(new Runnable()  
     { 
         @Override
         public void run() { 
             while (true) { 

                 // read the message to deliver. 
                 String msg = scn.nextLine(); 
                   
                 try { 
                     // write on the output stream 
                     dos.writeUTF(msg); 
                 } catch (IOException e) { 
                     e.printStackTrace(); 
                 } 
             } 
         } 
     }); 
       
     // readMessage thread 
     Thread readMessage = new Thread(new Runnable()  
     { 
         @Override
         public void run() { 

             while (true) { 
                 try { 
                     // read the message sent to this client 
                     String msg = dis.readUTF(); 
                     System.out.println(msg); 
                 } catch (IOException e) { 

                     e.printStackTrace(); 
                 } 
             } 
         } 
     }); 
     sendUsername.start();
     sendMessage.start(); 
     readMessage.start(); 

 } 
} 