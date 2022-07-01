package it.polimi.ingsw.client.ui.viewcontrollers;


import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.client.ui.GUI;
import it.polimi.ingsw.client.ui.rescale.RescalableController;
import it.polimi.ingsw.client.ui.rescale.RescaleUtils;
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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The JavaFX controller handling the initial login page
 */
public class LoginController extends RescalableController {
	
	@FXML
	private ImageView cranioImg;
	@FXML
	private ImageView eriantysImg;
	@FXML
	private Label nicknameFieldTitleLabel;
	@FXML
	private Label playerNumberTitleLabel;
	@FXML
	private Label wizardTitleLabel;
	@FXML
	private Label matchTypeTitleLabel;
	@FXML
	private ChoiceBox<String> gameBox;
	@FXML
	private ChoiceBox<String> wizardBox;
	@FXML
	private ChoiceBox<String> chosenPlayerCount;
	@FXML
	private TextField nicknameBox;
	@FXML
	private Button loginButton;
	
	private final ObservableList<String> wizardChoices = FXCollections.observableArrayList("Wizard 1", "Wizard 2", "Wizard 3", "Wizard 4");
	private final ObservableList<String> gameChoices = FXCollections.observableArrayList("Basic (simplified rules)", "Expert (full rules for more action)");
	private final ObservableList<String> chosenNumberOfPlayers = FXCollections.observableArrayList("2", "3", "4");
	
	private String chosenUsername;
	private MatchVariant selectedMatchType;
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		super.initialize(url, resourceBundle);
		gameBox.setValue(gameChoices.get(0));
		gameBox.setItems(gameChoices);
		wizardBox.setValue(wizardChoices.get(0));
		wizardBox.setItems(wizardChoices);
		chosenPlayerCount.setValue(chosenNumberOfPlayers.get(0));
		chosenPlayerCount.setItems(chosenNumberOfPlayers);
		rescale(getCurrentScaleValue());
	}
	
	/**
	 * Callback called when the Send button is clicked, to send all login data to the server
	 */
	public void sendFormValuesToServer() {
		chosenUsername = nicknameBox.getText();
		if (chosenUsername == null || chosenUsername.length() == 0 || chosenUsername.contains(" ")) {
			//Error when validating the username
			// Present an alert
			Platform.runLater(() -> {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText("Invalid username");
				alert.setContentText("You must type a username to use for the Match (non-null, non-empty, no whitespaces).");
				alert.show();
			});
		} else {
			int chosenLobbySize = Integer.parseInt(chosenPlayerCount.getSelectionModel().getSelectedItem());
			
			if (gameBox.getSelectionModel().getSelectedItem().equals(gameChoices.get(0))) {
				selectedMatchType = MatchVariant.BasicRuleSet;
			} else {
				selectedMatchType = MatchVariant.ExpertRuleSet;
			}
			
			Wizard wiz = Wizard.Wizard4;
			for (int i = 0; i < wizardChoices.size(); i++) {
				if (wizardBox.getSelectionModel().getSelectedItem().equals(wizardChoices.get(i))) {
					wiz = Wizard.values()[i];
				}
			}
			
			NetworkMessage loginMessage = new LoginMessage(chosenUsername, chosenLobbySize, selectedMatchType, wiz);
			NotificationCenter.shared().addObserver(this, this::didReceiveLoginResponse, NotificationName.ClientDidReceiveLoginResponse, GameClient.shared());
			
			//Connect to the server
			String buttonText = loginButton.getText();
			loginButton.setText("Connecting to the server...");
			GameClient.createClient(Client.getServerIP(), Client.getServerPort(), true);
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
	}
	
	/**
	 * Callback for the Login Response notification, used to display the Lobby controller or an alert for failures
	 * @param notification The Login Response notification
	 */
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
					NotificationCenter.shared().removeObserver(this);
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
	
	@Override
	protected void cleanupAfterTermination() {
	}
	
	@Override
	public void rescale(double scale) {
		//Set everything up so that it is centered
		RescaleUtils.rescaleToCenter(nicknameBox, 429, 26, 174, scale);
		RescaleUtils.rescaleToCenter(loginButton, 103, 35, 565, scale);
		RescaleUtils.rescaleToCenter(gameBox, 429, 26, 423, scale);
		RescaleUtils.rescaleToCenter(wizardBox, 429, 26, 339, scale);
		RescaleUtils.rescaleToCenter(chosenPlayerCount, 429, 25, 256, scale);
		RescaleUtils.rescaleToCenter(nicknameFieldTitleLabel, 429, 17, 154, scale);
		RescaleUtils.rescaleToCenter(playerNumberTitleLabel, 429, 17, 236, scale);
		RescaleUtils.rescaleToCenter(wizardTitleLabel, 429, 17, 319, scale);
		RescaleUtils.rescaleToCenter(matchTypeTitleLabel, 429, 17, 403, scale);
		
		layoutEriantysHeader(scale, cranioImg, eriantysImg);
	}
	
	/**
	 * Lays out the common Eriantys header
	 * @param scale The scaling factor to apply
	 * @param cranioImg The Cranio Creations logo pane
	 * @param eriantysImg The Eriantys logo pane
	 */
	static void layoutEriantysHeader(double scale, ImageView cranioImg, ImageView eriantysImg) {
		cranioImg.setFitWidth(81 * scale);
		cranioImg.setFitHeight(90 * scale);
		cranioImg.setLayoutX((800 - GUI.referenceWidth * 0.5) * scale + GUI.getWindowWidth() * 0.5);
		cranioImg.setLayoutY(42 * scale);
		
		eriantysImg.setFitWidth(308 * scale);
		eriantysImg.setFitHeight(89 * scale);
		eriantysImg.setLayoutX((420 - GUI.referenceWidth * 0.5) * scale + GUI.getWindowWidth() * 0.5);
		eriantysImg.setLayoutY(42 * scale);
	}
}
