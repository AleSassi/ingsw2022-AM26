package it.polimi.ingsw.client.ui;


import it.polimi.ingsw.client.cli.CLIManager;
import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.LoginMessage;
import it.polimi.ingsw.server.controller.network.messages.LoginResponse;
import it.polimi.ingsw.server.controller.network.messages.NetworkMessage;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.match.MatchVariant;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable {
	
	@FXML
	ChoiceBox<String> gameBox;
	@FXML
	ChoiceBox<String> wizardBox;
	@FXML
	ChoiceBox<String> chosenPlayerCount;
	@FXML
	TextField nicknameBox;
	@FXML
	Label statusLabel;
	@FXML
	Pane pane;
	@FXML
	Button loginButton;
	
	ObservableList<String> wizardChoices = FXCollections.observableArrayList("wizard1", "wizard2", "wizard3", "wizard4");
	ObservableList<String> gameChoices = FXCollections.observableArrayList("simple", "expert");
	ObservableList<String> chosenNumberOfPlayers = FXCollections.observableArrayList("2", "3", "4");
	
	private String chosenUsername;
	private MatchVariant selectedMatchType;
	private int chosenLobbySize;
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		gameBox.setValue("simple");
		gameBox.setItems(gameChoices);
		wizardBox.setValue("wizard1");
		wizardBox.setItems(wizardChoices);
		chosenPlayerCount.setValue("2");
		chosenPlayerCount.setItems(chosenNumberOfPlayers);
	}
	
	public void sendFormValuesToServer(ActionEvent actionEvent) {
		chosenUsername = nicknameBox.getText();
		chosenLobbySize = Integer.parseInt(chosenPlayerCount.getSelectionModel().getSelectedItem());
  
		if (gameBox.getSelectionModel().getSelectedItem().equals("simple")) {
			selectedMatchType = MatchVariant.BasicRuleSet;
		} else {
			selectedMatchType = MatchVariant.ExpertRuleSet;
		}
		
		Wizard wiz = switch (wizardBox.getSelectionModel().getSelectedItem()) {
			case "wizard1" -> Wizard.Wizard1;
			case "wizard2" -> Wizard.Wizard2;
			case "wizard3" -> Wizard.Wizard3;
			default -> Wizard.Wizard4;
		};
		NetworkMessage loginMessage = new LoginMessage(chosenUsername, chosenLobbySize, selectedMatchType, wiz);
		NotificationCenter.shared().addObserver(this::didReceiveLoginResponse, NotificationName.ClientDidReceiveLoginResponse, GameClient.shared());
		
		//Connect to the server
		String buttonText = loginButton.getText();
		loginButton.setText("Connecting to the server...");
		GameClient.createClient(Client.getServerIP(), Client.getServerPort());
		try {
			GameClient.shared().connectToServer();
			GameClient.shared().sendMessage(loginMessage);
		} catch (IOException e) {
			// Present an alert
			Platform.runLater(() -> {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText("Server Unreachable");
				alert.setContentText("Could not connect to the server. Please make sure that the server is active and reachable on the specified port.");
				alert.show();
			});
		} finally {
			loginButton.setText(buttonText);
		}
	}
	
	private void didReceiveLoginResponse(Notification notification) {
		LoginResponse message = (LoginResponse) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
		boolean messageIsForPlayer = message.getNickname().equals(chosenUsername);
		
		if (messageIsForPlayer) {
			if (message.isLoginAccepted()) {
				// Go to the Lobby
				Client.setNickname(chosenUsername);
				try {
					// Present the lobby controller
					LobbyController lobbyController = GUI.setRoot("scenes/lobby").getController();
					lobbyController.setInitialData(selectedMatchType, message.getNumberOfPlayersRemainingToFillLobby());
				} catch (IOException e) {
					// Present an alert
					Platform.runLater(() -> {
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText(e.getLocalizedMessage());
						alert.show();
					});
				}
			} else {
				// Present an alert
				Platform.runLater(() -> {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText(message.getRejectionReason());
					alert.show();
				});
			}
		}
	}
}
