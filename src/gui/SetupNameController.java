package gui;

import chat.Client;
import javafx.fxml.FXML;
import javafx.scene.control.*;



public class SetupNameController {
	@FXML
	private TextArea tSetupUsername;

	@FXML 
	private Button btnStartChat;

	
	public void pressStart() {
		//open Overview.fxml here
		Client.openOverview(tSetupUsername.getText());
		
	}
	
}
