package chat;

import java.io.*; 
import java.net.*; 
import java.util.Scanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage; 

public class Client  extends Application
{ 
 final static int ServerPort = 1234; 

 public static void main(String args[]) throws UnknownHostException, IOException  
 { 
	 launch(args);
     Scanner scn = new Scanner(System.in); 
       
     // getting localhost ip 
     InetAddress ip = InetAddress.getByName("192.168.178.62"); 
       
     // establish the connection 
     Socket s = new Socket(ip, ServerPort); 
       
     // obtaining input and out streams 
     DataInputStream dis = new DataInputStream(s.getInputStream()); 
     DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 

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

     sendMessage.start(); 
     readMessage.start(); 

 } 
 
 @Override
 public void start(Stage primaryStage) throws IOException {
    // +++++++++++++++++++++++++++++++++++++++++++++
    // Layout
    // +++++++++++++++++++++++++++++++++++++++++++++
     
    // FXML-Datei laden!
    Parent root = FXMLLoader.load(getClass().getResource("../gui/Chatroom.fxml"));
      
   // Szene
   Scene scene = new Scene(root);
    
    // +++++++++++++++++++++++++++++++++++++++++++++
    // Stage konfigurieren
    // +++++++++++++++++++++++++++++++++++++++++++++

    // Titel setzen
   primaryStage.setTitle("AxxG - FXML Beispiel");
    
   // Szene setzen
   primaryStage.setScene(scene);
   primaryStage.sizeToScene();
    
   // Stage anzeigen
   primaryStage.show();
}
 
} 