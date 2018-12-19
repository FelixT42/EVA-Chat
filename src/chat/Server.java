package chat;

//Java implementation of Server side 
//It contains two classes : Server and ClientHandler 
//Save file as Server.java 

import java.io.*; 
import java.util.*;
import java.net.*;
import java.text.SimpleDateFormat; 

//Server class 
public class Server 
{ 

	//Vector der alle angemeldeten Benutzer enthällt
	static Vector<ClientHandler> clients = new Vector<>(); 

	//zähler für die Clients
	static int i = 0;

	private static ServerSocket ss; 

	public static void main(String[] args) throws IOException 
	{ 
		//Server Socket mit Port erstellen 
		ss = new ServerSocket(1234); 

		Socket s; 
		Socket cs;

		// warten auf einkommende verbindung
		while (true) 
		{ 
			// Verbindung wird akzeptiert
			s = ss.accept(); 
			cs =ss.accept();
			System.out.println("Server: "); 
			// in s steht Socket mit IP und Port des Clients
			System.out.println("New client request received : " + s); 

			//Input und output streams für die Nachrichtenübertragung
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
			DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 

			//Input und output streams für die Kontrolldatenübertragung
			DataInputStream cdis = new DataInputStream(cs.getInputStream()); 
			DataOutputStream cdos = new DataOutputStream(cs.getOutputStream()); 

			System.out.println("Creating a new handler for this client..."); 

			//neuer Clienthandler für jeden client
			ClientHandler client = new ClientHandler(s,"client " + i, dis, dos,cdis,cdos); 

			// neuer Thread mit diesem Client 
			Thread thread = new Thread(client); 

			System.out.println("Adding this client to active client list"); 

			//Add Client zur Online Liste 
			clients.add(client); 

			// starte thread. 
			thread.start(); 
			//notwendig für Clientinitialisierung bevor username vorhanden ist
			i++; 

		} 
	} 
} 

//ClientHandler klasse
class ClientHandler implements Runnable 
{ 
	private String name; 
	final DataInputStream dis; 
	final DataOutputStream dos; 
	final DataInputStream cdis; 
	final DataOutputStream cdos; 
	Socket s; 
	boolean isloggedin; 

	// constructor 
	public ClientHandler(Socket s, String name, 
			DataInputStream dis, DataOutputStream dos,DataInputStream cdis, DataOutputStream cdos) { 
		this.dis = dis; 
		this.dos = dos; 
		this.name = name; 
		this.s = s; 
		this.isloggedin=true; 
		this.cdis = cdis;
		this.cdos=cdos;

		//Warten auf kontrollnachrichten
		Thread getControllMessages = new Thread(new Runnable()  
		{ 
			@Override
			public void run() { 
				boolean keepGoing =true;
				while (keepGoing) {
					try {
						String controllMessage = cdis.readUTF();

						//Übertragen aller angemeldeter User
						if(controllMessage.equals("getConnectedUsernames")) {
							String usernames = Server.clients.elementAt(0).name;
							for (int i=1; i<Server.clients.size();i++) {
								usernames+="###"+Server.clients.elementAt(i).name;
							}
							cdos.writeUTF(usernames);
						}

						//Setzen des eigenen Usernamens
						if(controllMessage.startsWith("setOwnUsername###")) {
							StringTokenizer st = new StringTokenizer(controllMessage, "###"); 
							//muss hier stehen um im nexten token username zu haben
							st.nextToken(); 
							String userName = st.nextToken();
							String temp=st.nextToken();
							System.out.println(temp);						

							for (int i=0; i<Server.clients.size();i++) {
								if (Server.clients.elementAt(i).name.equals(userName)) {
									userName="Nicht angemeldet";
									break;
								}
								if (Server.clients.elementAt(i).name.equals(temp)) {
									Server.clients.elementAt(i).name=userName;
								}
							}
							//sende username zurück
							cdos.writeUTF(userName);
						}

						//Übertragen des eigenen Usernamens
						if(controllMessage.equals("getOwnUsername")) {
							cdos.writeUTF(name);
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						Runnable runnable = this;
						Server.clients.remove(runnable);
						System.out.println("Client removed: "+name);
						keepGoing =false;
						try {
							dis.close();
							dos.close();
							cdis.close();
							cdos.close();

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							continue;
						} 

						continue;
					}
				}
			}
		});
		getControllMessages.start();
	} 

	@Override
	public void run() { 
		String timeStamp ;
		String received; 
		boolean keepGoing =true;
		while (keepGoing) 
		{ 
			try
			{ 
				//empfangener String
				received = dis.readUTF();
				// break the string into message and recipient part 
				StringTokenizer st = new StringTokenizer(received, "#"); 
				String MsgToSend = st.nextToken(); 
				String recipient = st.nextToken(); 
				// search for the recipient in the connected devices list. 
				// ar is the vector storing client of active users 
				for (ClientHandler mc : Server.clients) 
				{ 
					// if the recipient is found, write on its 
					// output stream 
					if (mc.name.equals(recipient) && mc.isloggedin==true) 
					{ 
						timeStamp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
						mc.dos.writeUTF("Am "+timeStamp+" schrieb "+this.name+" : \n"+MsgToSend); 
						break; 
					} 
				} 


			} catch (IOException e) { 
				Server.clients.remove(this);
				System.out.println("Client removed: "+this.name);
				keepGoing =false;
				continue;

			} 
		}//while

		try
		{ 
			// closing resources 
			this.dis.close(); 
			this.dos.close(); 
			this.cdis.close(); 
			this.cdos.close(); 

		}catch(IOException e){ 
			e.printStackTrace(); 
		} 
	} 
} 

