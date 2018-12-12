package gui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class ChatroomController {
	
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
		lblAnzeigeName.setText("TestTestTEst");
		tChatverlauf.setText(tEingabe.getText());
	}
	public void setReceivedMessage(String receivedTxt) {
		tChatverlauf.setText(receivedTxt);		
		
	}
}
