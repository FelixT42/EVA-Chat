package gui;
import java.util.StringTokenizer;
import chat.Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;


public class OverviewController {
	
	String lastClickedUser="nobody";
	String userName;
	
	@FXML
	private ListView<String> lstOnline;

	@FXML 
	private Button btnLogout;
	@FXML 
	private Label lblUsername;

	@FXML
	protected void logout(ActionEvent event) {
		lblUsername.setText("Test");
	}

	@FXML
	protected void clickOnOnlineList(MouseEvent event) {
		
		//Nur auf Doppelklick reagieren. Dazu dient lastClickedUser
		String user = (String) lstOnline.getSelectionModel().getSelectedItem();
		
		
		if(user !=null && user.equals(lastClickedUser)) {
			System.out.println(user);
			Client.openChatroom(user);
			lastClickedUser="nobody";
		}
		else
			lastClickedUser=user;
		
	}
	
	// Füllt die Liste der Angemeldeten Benutzer
	public void updateOnlineUsers(String onlineUsers) {
		//runLater sorgt dafür das änderungen der UI auch in einem Thread gemacht werden können
		Platform.runLater(new Runnable() {
			@Override public void run() {		
				String otherUserName;
				lstOnline.getItems().clear();
				StringTokenizer st = new StringTokenizer(onlineUsers, "###"); 
				while(st.hasMoreTokens()) {
					otherUserName = st.nextToken();
					if (!otherUserName.equals(userName)) {
						lstOnline.getItems().add(otherUserName);
					}
				}
			}
		});
	}
	
	public void setLabelUsername(String name) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				userName = name;
				lblUsername.setText(name);
			}
		});
		
	}
	
	@FXML
	public void exitApplication(ActionEvent event) {
	   Platform.exit();
	}

	
}



