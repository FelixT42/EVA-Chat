package gui;
import java.util.StringTokenizer;
import chat.Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;


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
