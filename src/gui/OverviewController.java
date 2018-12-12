package gui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class OverviewController {
	@FXML
	private ListView lstOnline;

	@FXML 
	private Button btnLogout;
	@FXML 
	private Label lblUsername;

	@FXML
	protected void logout(ActionEvent event) {
		lblUsername.setText("Test");
	}
}



