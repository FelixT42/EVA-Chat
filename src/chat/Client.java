package chat;

import java.io.*; 
import java.net.*; 
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import gui.ChatroomController;
import gui.OverviewController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage; 

public class Client  extends Application
{ 

	final static int ServerPort = 1234; 
	static DataInputStream dis;
	static DataOutputStream dos;
	static DataInputStream cdis;
	static DataOutputStream cdos;
	static String username = "masse";


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
		FXMLLoader loader = new FXMLLoader(Client.class.getResource("../gui/Overview.fxml"));
		AnchorPane pane = loader.load();

		// Szene
		Scene scene = new Scene(pane);
		//create Maincontroller
		//		
		OverviewController oc = loader.getController();

		// +++++++++++++++++++++++++++++++++++++++++++++
		// Stage konfigurieren
		// +++++++++++++++++++++++++++++++++++++++++++++

		// Titel setzen
		primaryStage.setTitle("Masse Chat");
		// Szene setzen
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		// Stage anzeigen
		primaryStage.show();
		Scanner scn = new Scanner(System.in); 

		// getting localhost ip 
		//IP adress from the server
		InetAddress ip = InetAddress.getByName("192.168.178.62"); 

		// establish the connection 
		Socket s = new Socket(ip, ServerPort); 
		Socket controllSock = new Socket(ip, ServerPort);

		// obtaining input and out streams 
		dis = new DataInputStream(s.getInputStream()); 
		dos = new DataOutputStream(s.getOutputStream()); 

		cdis = new DataInputStream(controllSock.getInputStream()); 
		cdos = new DataOutputStream(controllSock.getOutputStream());

		Thread updateOnlineUsers = new Thread(new Runnable()  
		{ 
			@Override
			public void run() { 
				boolean nameEingetragen =false;
				while (true) {

					try {
						
						

						
;
						if(!nameEingetragen) {
							cdos.writeUTF("getOwnUsername");
							cdos.writeUTF("setOwnUsername###"+username+"###"+cdis.readUTF());
							oc.setLabelUsername(cdis.readUTF());
							nameEingetragen=true;
						}
						

						cdos.writeUTF("getConnectedUsernames");
						String onlineUsers = cdis.readUTF();
						oc.updateOnlineUsers(onlineUsers);


					} catch (IOException e) {
						System.out.println(" ");
						try {
							dis.close();
							dos.close();
							cdis.close();
							cdos.close();
							TimeUnit.SECONDS.sleep(5);
						} catch (IOException e1) {
							System.out.println("Problem with Closing the Connection.");
							continue;
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 
						 
						
						
						Platform.runLater(new Runnable() {
							@Override public void run() {
								Alert alert = new Alert(AlertType.INFORMATION);
								alert.setTitle("Information Dialog");
								alert.setHeaderText(null);
								alert.setContentText("Server Connection Lost. Programm will be closed!");

								alert.showAndWait();
								System.exit(0);
							}
						});
						
						
						
					}

					// 5 Sekunden warten damit der Server nicht mit Anfragen überhäuft wird.
					try {
						TimeUnit.SECONDS.sleep(5);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} 
			} 
		});
		updateOnlineUsers.start();


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
	}

	public static void openChatroom(String user){
		FXMLLoader loader = new FXMLLoader(Client.class.getResource("../gui/Chatroom.fxml"));

		try {
			AnchorPane secondaryLayout = loader.load();
			Scene chatroomScene= new Scene(secondaryLayout);
			Stage newWindow = new Stage();
			newWindow.setTitle("Chatroom");
			newWindow.setScene(chatroomScene);
			newWindow.show();
			ChatroomController cc = loader.getController();
			cc.setChatpartner(user);

			Thread sendMessage = new Thread(new Runnable()  
			{ 
				@Override
				public void run(){ 
					while (true) { 

						if (cc.isSendClicked()) {

							try { 
								// write on the output stream 
								dos.writeUTF(cc.getMessage()+"#"+user); 
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
							System.out.println(msg);
							cc.setReceivedMessage(msg);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("Server Connection Lost. Reader");
							try {
								dis.close();
								dos.close();
								cdis.close();
								cdos.close();
							} catch (IOException e1) {
								System.out.println("Problem with Closing the Connection.");
								continue;
							} 
							 
							continue;
						} 
					} 
				} 
			}); 
			sendMessage.start(); 
			readMessage.start();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	@Override
	public void stop(){
	    System.out.println("Stage is closing");
	    System.exit(0);
	}
	
} 