package gui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class ChatroomController {
	
	boolean send =false;
	
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
		tChatverlauf.setText(receivedTxt);		
		
	}
	
	public  boolean isSendClicked(){
		
		return send;
	}
	
	public String getMessage() {
		send=false;
		return tEingabe.getText();
	}
}
