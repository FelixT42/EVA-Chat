package chat;

import java.io.*; 
import java.net.*; 
import java.util.Scanner;

import gui.ChatroomController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage; 

public class Client  extends Application
{ 

	final static int ServerPort = 1234; 
	DataOutputStream dos;

	public static void main(String args[]) throws UnknownHostException, IOException  
	{ 
		launch(args);


	} 
	
	public void sendMessage(String message) {
		
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		// +++++++++++++++++++++++++++++++++++++++++++++
		// Layout
		// +++++++++++++++++++++++++++++++++++++++++++++

		
		
		// Load FXML file and AnchorPane
		FXMLLoader loader = new FXMLLoader(Client.class.getResource("../gui/Chatroom.fxml"));
		AnchorPane pane = loader.load();

		// Szene
		Scene scene = new Scene(pane);
		//create Maincontroller
		ChatroomController cc = loader.getController();
		
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
		Scanner scn = new Scanner(System.in); 

		// getting localhost ip 
		//IP adress from the server
		InetAddress ip = InetAddress.getByName("192.168.178.63"); 

		// establish the connection 
		Socket s = new Socket(ip, ServerPort); 

		// obtaining input and out streams 
		DataInputStream dis = new DataInputStream(s.getInputStream()); 
		dos = new DataOutputStream(s.getOutputStream()); 


		
	
		

		// username thread 
		/*
     Thread sendUsername = new Thread(new Runnable()  
     { 
         @Override
         public void run() { 
             while (true) { 

                 // read the message to deliver. 
            	 String username = scn.nextLine(); 


                 try { 
                     // write on the output stream 
                     dos.writeUTF(username); 
                 } catch (IOException e) { 
                     e.printStackTrace(); 
                 } 
             } 
         } 
     });
		 */
		// sendMessage thread 
		Thread sendMessage = new Thread(new Runnable()  
		{ 
			@Override
			public void run(){ 
				while (true) { 
					
					if (cc.isSendClicked()) {

						try { 
							// write on the output stream 
							dos.writeUTF(cc.getMessage()); 
						} catch (IOException e) { 
							e.printStackTrace(); 
						} 
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
						cc.setReceivedMessage(msg);
						System.out.println(msg); 
					} catch (IOException e) { 

						e.printStackTrace(); 
					} 
				} 
			} 
		}); 
		//sendUsername.start();
		sendMessage.start(); 
		readMessage.start(); 
	}

} 