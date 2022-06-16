package it.polimi.ingsw.client.ui;

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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class LobbyController implements Initializable {
	
	private int numberOfPlayersToFill = 4;
	private List<Notification> playerStateMessagesQueue; //Used to forward them to the main controller, since it might happen that the main controller is initialized and presented after the notification arrives
	private Notification tableMessage, activePlayerMessage, matchStateMessage;
	
	@FXML
	Label statusLabel;
	@FXML
	Label matchVariantLabel;
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		playerStateMessagesQueue = new ArrayList<>();
		GUI.registerForDisconnectionEvents();
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
	
	public void setInitialData(MatchVariant matchVariant, int remainingNumberOfPlayers) {
		numberOfPlayersToFill = remainingNumberOfPlayers;
		Platform.runLater(() -> {
			matchVariantLabel.setText("Match Variant: " + matchVariant);
			statusLabel.setText("Waiting for " + remainingNumberOfPlayers + " more players");
		});
		if (numberOfPlayersToFill == 0) {
			moveToGameScene();
		}
	}
	
	private void otherPlayerLoggedInReceived(Notification notification) {
		LoginResponse message = (LoginResponse) notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue());
		
		if (!message.getNickname().equals(Client.getNickname()) && message.isLoginAccepted()) {
			numberOfPlayersToFill = message.getNumberOfPlayersRemainingToFillLobby();
			Platform.runLater(() -> {
				if (numberOfPlayersToFill == 0) {
					statusLabel.setText("The lobby is full. You are now ready to start the game");
				} else {
					statusLabel.setText("Waiting for " + numberOfPlayersToFill + " more players");
				}
			});
			if (numberOfPlayersToFill == 0) {
				moveToGameScene();
			}
		}
	}
	
	private void didReceivePlayerStateMessage(Notification notification) {
		playerStateMessagesQueue.add(notification);
	}
	
	private void didReceiveTableStateMessage(Notification notification) {
		tableMessage = notification;
	}
	
	private void didReceiveActivePlayerMessage(Notification notification) {
		activePlayerMessage = notification;
	}
	
	private void didReceiveMatchPhaseMessage(Notification notification) {
		matchStateMessage = notification;
	}
	
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
		} catch (IOException e) {
			// Present an alert
			Platform.runLater(() -> {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText(e.getLocalizedMessage());
				alert.show();
			});
		}
	}
}
