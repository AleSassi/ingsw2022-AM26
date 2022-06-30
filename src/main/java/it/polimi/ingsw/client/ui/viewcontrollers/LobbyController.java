package it.polimi.ingsw.client.ui.viewcontrollers;

import it.polimi.ingsw.client.ui.GUI;
import it.polimi.ingsw.client.ui.rescale.RescalableController;
import it.polimi.ingsw.client.ui.rescale.RescaleUtils;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.LoginResponse;
import it.polimi.ingsw.server.controller.network.messages.MatchTerminationMessage;
import it.polimi.ingsw.server.model.match.MatchVariant;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class LobbyController extends RescalableController {

	private int numberOfPlayersToFill = 4;
	private List<Notification> playerStateMessagesQueue; //Used to forward them to the main controller, since it might happen that the main controller is initialized and presented after the notification arrives
	private Notification tableMessage, activePlayerMessage, matchStateMessage;
	
	@FXML
	private Label statusLabel;
	@FXML
	private Label matchVariantLabel;
	@FXML
	private ImageView cranioImg;
	@FXML
	private ImageView eriantysImg;

	/**
	 * Initialize the lobby scene and starting all the notification threads
	 * @param url
	 * @param resourceBundle
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		super.initialize(url, resourceBundle);
		playerStateMessagesQueue = new ArrayList<>();
		GUI.registerForDisconnectionEvents();
		rescale(getCurrentScaleValue());
		NotificationCenter.shared().addObserver(this, this::otherPlayerLoggedInReceived, NotificationName.ClientDidReceiveLoginResponse, null);
		NotificationCenter.shared().addObserver(this, (notification) -> {
			if (numberOfPlayersToFill > 0) {
				MatchTerminationMessage message = (MatchTerminationMessage) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
				// Present an alert
				Platform.runLater(() -> {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					// Go back to the login screen
					try {
						GUI.setRoot("scenes/login");
						alert.setContentText("The server ended the match. Reason: \"" + message.getTerminationReason() + "\"");
						alert.show();
					} catch (IOException e) {
						alert.setContentText(e.getLocalizedMessage());
						alert.show();
					}
				});
			}
		}, NotificationName.ClientDidReceiveMatchTerminationMessage, null);
		NotificationCenter.shared().addObserver(this, this::didReceivePlayerStateMessage, NotificationName.ClientDidReceivePlayerStateMessage, null);
		NotificationCenter.shared().addObserver(this, this::didReceiveTableStateMessage, NotificationName.ClientDidReceiveTableStateMessage, null);
		NotificationCenter.shared().addObserver(this, this::didReceiveActivePlayerMessage, NotificationName.ClientDidReceiveActivePlayerMessage, null);
		NotificationCenter.shared().addObserver(this, this::didReceiveMatchPhaseMessage, NotificationName.ClientDidReceiveMatchStateMessage, null);
	}

	/**
	 * This method sets the matchVariant and the remainingNumberOfPlayers label
	 * @param matchVariant
	 * @param remainingNumberOfPlayers
	 */
	public void setInitialData(MatchVariant matchVariant, int remainingNumberOfPlayers) {
		numberOfPlayersToFill = remainingNumberOfPlayers;
		Platform.runLater(() -> {
			matchVariantLabel.setText("Match Variant: " + (matchVariant == MatchVariant.BasicRuleSet ? "Basic (simplified rules)" : "Expert (full rules)"));
			updateStatusLabelText(numberOfPlayersToFill);
		});
		if (numberOfPlayersToFill == 0) {
			moveToGameScene();
		}
	}

	/**
	 * This method updates and display the number of remaining Players from the notification
	 * @param notification
	 */
	private void otherPlayerLoggedInReceived(Notification notification) {
		LoginResponse message = (LoginResponse) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
		
		if (!message.getNickname().equals(Client.getNickname()) && message.isLoginAccepted()) {
			numberOfPlayersToFill = message.getNumberOfPlayersRemainingToFillLobby();
			Platform.runLater(() -> {
				updateStatusLabelText(numberOfPlayersToFill);
			});
			if (numberOfPlayersToFill == 0) {
				moveToGameScene();
			}
		}
	}
	
	private void updateStatusLabelText(int numberOfRemainingPlayers) {
		if (numberOfRemainingPlayers == 0) {
			statusLabel.setText("The lobby is full. You are now ready to start the game");
		} else {
			statusLabel.setText("Waiting for " + numberOfRemainingPlayers + " more player" + (numberOfRemainingPlayers == 1 ? "" : "s") + " to join your lobby");
		}
		statusLabel.setWrapText(true);
	}

	/**
	 * PlayerStateMessage callback, adds the message to the queue
	 * @param notification
	 */
	private void didReceivePlayerStateMessage(Notification notification) {
		playerStateMessagesQueue.add(notification);
	}

	/**
	 * TableStateMessage callBack
	 * @param notification
	 */
	private void didReceiveTableStateMessage(Notification notification) {
		tableMessage = notification;
	}

	/**
	 * ActivePlayerMessage callback
	 * @param notification
	 */
	private void didReceiveActivePlayerMessage(Notification notification) {
		activePlayerMessage = notification;
	}

	/**
	 * MatchPhaseMessage callback
	 * @param notification
	 */
	private void didReceiveMatchPhaseMessage(Notification notification) {
		matchStateMessage = notification;
	}

	/**
	 * This method moves to the main scene and forward all the initial network messages to the next scene
	 */
	private void moveToGameScene() {
		try {
			MainBoardController mainBoardController = GUI.setRoot("scenes/mainBoard").getController();
			mainBoardController.load();
			//Resend the received player state messages, so that the controller can receive them
			for (Notification notification: playerStateMessagesQueue) {
				mainBoardController.didReceivePlayerStatusNotification(notification);
			}
			if (tableMessage != null) {
				mainBoardController.didReceiveTableStateMessage(tableMessage);
			}
			if (activePlayerMessage != null) {
				mainBoardController.didReceiveActivePlayerMessage(activePlayerMessage);
			}
			if (matchStateMessage != null) {
				mainBoardController.didReceiveMatchStateMessage(matchStateMessage);
			}
			NotificationCenter.shared().removeObserver(this);
		} catch (IOException e) {
			// Present an alert
			Platform.runLater(() -> {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText(e.getLocalizedMessage());
				alert.show();
			});
		}
	}
	
	@Override
	protected void cleanupAfterTermination() {
	}
	
	@Override
	public void rescale(double scale) {
		RescaleUtils.rescaleToCenter(matchVariantLabel, 570, 20, 315, scale);
		RescaleUtils.rescaleToCenter(statusLabel, 570, 100, 389, scale);
		matchVariantLabel.setStyle("-fx-font-size: " + (20 * scale));
		statusLabel.setStyle("-fx-font-size: " + (35 * scale));
		
		LoginController.layoutEriantysHeader(scale, cranioImg, eriantysImg);
	}
}
