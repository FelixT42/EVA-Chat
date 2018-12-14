package gui;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class ChatroomController {
	
	String timeStamp;
	boolean send = false;
	String chatpartner;
	
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
		send=true;
	}
	public synchronized void setReceivedMessage(String receivedTxt) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				tChatverlauf.appendText(receivedTxt+"\n");
			}
		});
			
	}
	
	public synchronized boolean isSendClicked(){
		return send;
	}
	
	public String getMessage() {
		timeStamp = new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss").format(Calendar.getInstance().getTime());
		//hier muss noch der teil mit der raute rausgeholt werden
		tChatverlauf.appendText("Am "+timeStamp+" schrieb ich : \n"+tEingabe.getText()+"\n");
		send=false;
		return tEingabe.getText();
	}
	
	public void setChatpartner(String name) {
		this.lblAnzeigeName.setText(name);
		this.chatpartner = name;
	}
}
