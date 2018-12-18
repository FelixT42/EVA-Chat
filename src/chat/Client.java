package chat;

import java.io.*; 
import java.net.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
import javafx.stage.Stage; 

public class Client  extends Application
{ 

	final static int ServerPort = 1234; 
	static DataInputStream dis;
	static DataOutputStream dos;
	static DataInputStream cdis;
	static DataOutputStream cdos;
	static String username = "Nicht angemeldet";
	static boolean connectionLost = false;
	
	static Socket s;
	static Socket controllSock;

	public static List<ChatroomController>ccl = Collections.synchronizedList(new LinkedList<ChatroomController>());
	static List<String>oldMessages = Collections.synchronizedList(new LinkedList<String>());

	public static void main(String args[]) throws UnknownHostException, IOException  
	{ 
		boolean startMainPage = true;
		try{
			System.out.println("Your Username: "+args[0]);
			username = args[0];
		} catch (IndexOutOfBoundsException e){
			startMainPage = false;
			Platform.runLater(new Runnable() {
				@Override public void run() {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Information Dialog");
					alert.setHeaderText(null);
					alert.setContentText("Bitte geben Sie dem Programm ihren Nutzernamen als Übergabeparameter mit !");
					alert.showAndWait();
					System.exit(0);
				}
			});	
		}


		if (startMainPage) launch(args);
	} 

	public void sendMessage(String message) {

	}

	@Override
	public void start(Stage primaryStage) {
		// +++++++++++++++++++++++++++++++++++++++++++++
		// Layout
		// +++++++++++++++++++++++++++++++++++++++++++++
		// Laden der Fxml-Datei 
		FXMLLoader loader = new FXMLLoader(Client.class.getResource("/gui/Overview.fxml"));
		AnchorPane pane;
		try {
			pane = loader.load();

			// +++++++++++++++++++++++++++++++++++++++++++++
			// Stage konfigurieren
			// +++++++++++++++++++++++++++++++++++++++++++++
			// Szene
			Scene scene = new Scene(pane);
			// Titel setzen
			primaryStage.setTitle("EVA-Chat");
			// Szene setzen
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();
			// Stage anzeigen
			primaryStage.show();
		} catch (IOException e2) {
			e2.printStackTrace();
		}


		//OverviewController laden
		OverviewController oc = loader.getController();
		
		boolean noError =true;

		try {
			// Ip-Adresse des Servers
			InetAddress ip = InetAddress.getByName("192.168.178.85");

			//Damit wird die Verbindung schneller geschlossen wenn der Server nicht erreichbar ist
			if(!ip.isReachable(4000)) {
				throw new IOException("No Connection to Server");
			}
			s = new Socket(ip, ServerPort); 
			controllSock = new Socket(ip, ServerPort);

			// Datastreams für die übertragung von Nachrichten
			dis = new DataInputStream(s.getInputStream()); 
			dos = new DataOutputStream(s.getOutputStream()); 

			// Datastreams für die übertragung von Steuerdaten
			cdis = new DataInputStream(controllSock.getInputStream()); 
			cdos = new DataOutputStream(controllSock.getOutputStream());
			
		} catch (IOException e) {
			// Fehlerbehandlung wenn der Server nach 4 Sec nicht antwortet.
			// Wenn noError auf false steht werden die lese und schreibe Threads nicht gestartet -> Beugt exeptions vor!
			noError=false;
			Platform.runLater(new Runnable() {
				@Override public void run() {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Information Dialog");
					alert.setHeaderText(null);
					alert.setContentText("Can´t connect to Server. Programm will be closed!");

					alert.showAndWait();
					System.exit(0);
				}
			});
		} 
		Thread updateOnlineUsers = new Thread(new Runnable(){ 
			@Override
			public void run() { 
				boolean nameEingetragen =false;				
				String checkUsername;
				while (true) {
					try {
						if(!nameEingetragen) {
							cdos.writeUTF("getOwnUsername");
							cdos.writeUTF("setOwnUsername###"+username+"###"+cdis.readUTF());
							checkUsername = cdis.readUTF();
							if(!checkUsername.equals(username)) {
								Platform.runLater(new Runnable() {
									@Override public void run() {
										Alert alert = new Alert(AlertType.INFORMATION);
										alert.setTitle("Information Dialog");
										alert.setHeaderText(null);
										alert.setContentText("Ihr Username ist leider schon vorhanden. Bitte wählen Sie einen anderen.");

										alert.showAndWait();
										System.exit(0);
									}
								});	
							}
							oc.setLabelUsername(checkUsername);
							nameEingetragen=true;
						}
						cdos.writeUTF("getConnectedUsernames");
						String onlineUsers = cdis.readUTF();
						oc.updateOnlineUsers(onlineUsers);
						for(ChatroomController cc:ccl) {
							cc.isChatpartnerStillOnline(onlineUsers);
						}

					} catch (IOException e) {
						try {
							dis.close();
							dos.close();
							cdis.close();
							cdos.close();
							s.close();
							TimeUnit.SECONDS.sleep(5);
						} catch (IOException e1) {
							System.out.println("Problem with Closing the Connection.");
							continue;
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 
						if (!connectionLost) {
							connectionLost =true;
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
						
					}

					// 5 Sekunden warten damit der Server nicht mit Anfragen überhäuft wird.
					try {
						TimeUnit.SECONDS.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				} 
			} 
		});


		if(noError)
			updateOnlineUsers.start();


		Thread sendMessage = new Thread(new Runnable()  
		{ 
			@Override
			public void run(){ 
				while (true) { 
					for(int i=0; i<ccl.size();i++) {
						if (ccl.size()>0 && ccl.get(i).isSendClicked()) {

							try { 
								// write on the output stream 
								dos.writeUTF(ccl.get(i).getMessage()+"#"+ccl.get(i).getChatpartner()); 
							} catch (IOException e) { 
								e.printStackTrace(); 
							} 
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
						if(ccl.isEmpty()) {
							oldMessages.add(msg);
							String username = msg.substring(31, msg.indexOf(':',31)-1);
							Platform.runLater(new Runnable() {
								@Override public void run() {
									Client.openChatroom(username);	
								}
							});

						}

						for(ChatroomController cc:ccl) {
							System.out.println("");

							if(!cc.setReceivedMessage(msg)) {
								oldMessages.add(msg);
								String username = msg.substring(31, msg.indexOf(':',31)-1);
								Platform.runLater(new Runnable() {
									@Override public void run() {
										Client.openChatroom(username);	
									}
								});
							}
						}

					} catch (IOException e) {
						// Kontrolliertes schließen der Verbindung im Fehlerfall
						try {
							dis.close();
							dos.close();
							cdis.close();
							cdos.close();
							s.close();
							controllSock.close();
						} catch (IOException e1) {
							System.out.println("Problem with Closing the Connection.");
							continue;
						} 

						continue;
					} 
				} 
			} 
		}); 

		// Wenn kein Fehler vorliegt, starte die Threads
		if(noError) {
			sendMessage.start(); 
			readMessage.start();
		}
	}

	public static void openChatroom(String user){
		FXMLLoader loader = new FXMLLoader(Client.class.getResource("/gui/Chatroom.fxml"));

		try {
			AnchorPane secondaryLayout = loader.load();
			Scene chatroomScene= new Scene(secondaryLayout);
			Stage newWindow = new Stage();
			newWindow.setTitle("Chatroom");
			newWindow.setScene(chatroomScene);

			newWindow.show();
			ChatroomController cc = loader.getController();
			cc.setChatpartner(user);
			ccl.add(cc);
			newWindow.setOnCloseRequest( event->{
				ccl.remove(cc);
			});
			for(int i=0;i<oldMessages.size();i++) {
				if(cc.setReceivedMessage(oldMessages.get(i)))
					oldMessages.remove(oldMessages.get(i));
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop(){
		System.out.println("Programm wird beendet");
		System.exit(0);
	}

} 