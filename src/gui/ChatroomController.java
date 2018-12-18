package gui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class ChatroomController {

	String timeStamp;
	boolean send = false;
	String chatpartner;
	boolean isOnline=true;
	
	@FXML
	protected TextArea tChatverlauf;

	@FXML
	protected TextArea tEingabe;

	@FXML 
	private Button btnSenden;

	@FXML 
	private Text lblAnzeigeName;

	@FXML
	protected void senden(MouseEvent event) {
		
		if(!tEingabe.getText().isEmpty())
			send=true;
	}
	

	
	
	public synchronized boolean setReceivedMessage(String receivedTxt) {

		String username = receivedTxt.substring(31, receivedTxt.indexOf(':',31)-1);
		if(username.equals(chatpartner)) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					tChatverlauf.appendText(receivedTxt+"\n");	
				}
			});
			return true;
		}
		return false;
	}



	public synchronized boolean isSendClicked(){
		return send;
	}

	public String getMessage() {
		timeStamp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
		//hier muss noch der teil mit der raute rausgeholt werden
		tChatverlauf.appendText("Am "+timeStamp+" schrieb ich : \n"+tEingabe.getText()+"\n");
		send=false;
		String text = tEingabe.getText();
		tEingabe.clear();
		return text;
	}

	public void setChatpartner(String name) {
		this.lblAnzeigeName.setText(name);
		this.chatpartner = name;
	}
	public String getChatpartner() {
		return chatpartner;
	}
	
	public void isChatpartnerStillOnline(String onlineUsers) {
		StringTokenizer st = new StringTokenizer(onlineUsers, "###"); 
		boolean match = false;	
		while(st.hasMoreTokens() && !match) {
			if(st.nextToken().equals(chatpartner)) {
				match = true;
				break;
			}	
		}
		
		if(!match && isOnline) {
			isOnline =false;
			Platform.runLater(new Runnable() {
				@Override public void run() {
					tChatverlauf.appendText("\n Ihr Chatpartner hat den Chat verlassen! \n Alle ab jetzt gesendeten Nachrichten erreichen den Empfänger nicht mehr! \n ");	
				}
			});
		}
		if(match && !isOnline) {
			isOnline =true;
			Platform.runLater(new Runnable() {
				@Override public void run() {
					tChatverlauf.appendText("\n Ihr Chatpartner ist wieder Online \n \n ");	
				}
			});
		}
		
		
	}
	
	@FXML
	public void enter(KeyEvent e) {
		if(e.getCode() == KeyCode.ENTER) {
			MouseEvent me = new MouseEvent(null, 0, 0, 0, 0, null, 0, 
					 isOnline, isOnline, isOnline, isOnline, isOnline, isOnline, isOnline, 
					 isOnline, isOnline, isOnline, null);
			senden(me);
		}
			
	}
	
}
