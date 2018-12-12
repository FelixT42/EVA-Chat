package gui;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class OverviewController implements Initializable {
	
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}

}
