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
	//region Rescale variables
	private String chosenUsername;
	private MatchVariant selectedMatchType;
	private static final double cranioFitWidth = 81;
	private static final double cranioFitHeight = 90;
	private static final double cranioX = 800;
	private static final double cranioY = 42;
	private static final double eriantysFitWidth = 308;
	private static final double eriantysFitHeight = 89;
	private static final double eriantysX = 420;
	private static final double eriantysY = 42;

	private final double nicknameBoxWidth = 429;
	private final double nicknameBoxHeight = 26;
	private final double nicknameBoxY = 174;
	private final double loginButtonWidth = 103;
	private final double loginButtonHeight = 35;
	private final double loginButtonY = 565;
	private final double gameBoxWidth = 429;
	private final double gameBoxHeight = 26;
	private final double gameBoxY = 423;
	private final double wizardBoxWidth = 429;
	private final double wizardBoxHeight = 26;
	private final double wizardBoxY = 339;
	private final double chosenPlayerCountWidth = 429;
	private final double chosenPlayerCountHeight = 25;
	private final double chosenPlayerCountY = 256;
	private final double nicknameFieldTitleLabelWidth = 429;
	private final double nicknameFieldTitleLabelHeight = 17;
	private final double nicknameFieldTitleLabelY = 154;
	private final double playerNumberTitleLabelWidth = 429;
	private final double playerNumberTitleLabelHeight = 17;
	private final double playerNumberTitleLabelY = 236;
	private final double wizardTitleLabelWidth = 429;
	private final double wizardTitleLabelHeigth = 17;
	private final double wizardTitleLabelY = 319;
	private final double matchTypeTitleLabelWidth = 429;
	private final double matchTypeTitleLabelHeight = 17;
	private final double matchTypeTitleLabelY = 403;

	//endregion




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
		RescaleUtils.rescaleToCenter(nicknameBox, nicknameBoxWidth, nicknameBoxHeight, nicknameBoxY, scale);
		RescaleUtils.rescaleToCenter(loginButton, loginButtonWidth, loginButtonHeight, loginButtonY, scale);
		RescaleUtils.rescaleToCenter(gameBox, loginButtonWidth, loginButtonHeight, loginButtonY, scale);
		RescaleUtils.rescaleToCenter(wizardBox, wizardBoxWidth, wizardBoxHeight, wizardBoxY, scale);
		RescaleUtils.rescaleToCenter(chosenPlayerCount, chosenPlayerCountWidth, chosenPlayerCountHeight, chosenPlayerCountY, scale);
		RescaleUtils.rescaleToCenter(nicknameFieldTitleLabel, nicknameFieldTitleLabelWidth, nicknameFieldTitleLabelHeight, nicknameFieldTitleLabelY, scale);
		RescaleUtils.rescaleToCenter(playerNumberTitleLabel, playerNumberTitleLabelWidth, playerNumberTitleLabelHeight, playerNumberTitleLabelY, scale);
		RescaleUtils.rescaleToCenter(wizardTitleLabel, wizardTitleLabelWidth, wizardTitleLabelHeigth, wizardTitleLabelY, scale);
		RescaleUtils.rescaleToCenter(matchTypeTitleLabel, matchTypeTitleLabelWidth, matchTypeTitleLabelHeight, matchTypeTitleLabelY, scale);
		
		layoutEriantysHeader(scale, cranioImg, eriantysImg);
	}



	static void layoutEriantysHeader(double scale, ImageView cranioImg, ImageView eriantysImg) {
		cranioImg.setFitWidth(cranioFitWidth * scale);
		cranioImg.setFitHeight(cranioFitHeight * scale);
		cranioImg.setLayoutX((cranioX - GUI.referenceWidth * 0.5) * scale + GUI.getWindowWidth() * 0.5);
		cranioImg.setLayoutY(cranioY * scale);
		
		eriantysImg.setFitWidth(eriantysFitWidth * scale);
		eriantysImg.setFitHeight(eriantysFitHeight * scale);
		eriantysImg.setLayoutX((eriantysX - GUI.referenceWidth * 0.5) * scale + GUI.getWindowWidth() * 0.5);
		eriantysImg.setLayoutY(eriantysY * scale);
	}
}
