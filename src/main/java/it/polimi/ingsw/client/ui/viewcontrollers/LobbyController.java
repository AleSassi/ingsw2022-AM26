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

/**
 * Class {@code LobbyController} represent the JavaFX controller for the Lobby
 */
public class LobbyController extends RescalableController {

	private int numberOfPlayersToFill = 4;
	private List<Notification> playerStateMessagesQueue; //Used to forward them to the main controller, since it might happen that the main controller is initialized and presented after the notification arrives
	private Notification tableMessage, activePlayerMessage, matchStateMessage;
	//region Rescale variables
	private final double matchVariantLabelWidth = 570;
	private final double matchVariantLabelHeight = 20;
	private final double matchVariantLabelY = 315;
	private final double matchVariantFont = 20;
	private final double statusLabelWidth = 570;
	private final double statusLabelHeight = 100;
	private final double statusLabelY = 389;
	private final double statusLabelFont = 35;
	//endregion

	
	@FXML
	private Label statusLabel;
	@FXML
	private Label matchVariantLabel;
	@FXML
	private ImageView cranioImg;
	@FXML
	private ImageView eriantysImg;

	/**
	 * Initialize the {@code LobbyController} and creates the {@link it.polimi.ingsw.notifications.Notification Notification's} observers
	 * @param url (type URL)
	 * @param resourceBundle (type ResourceBundle)
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
	 * Sets the {@link it.polimi.ingsw.server.model.match.MatchVariant MatchVariant} and the remainingNumberOfPlayers label
	 * @param matchVariant (type MatchVariant) {@code MatchVariant} to sets to
	 * @param remainingNumberOfPlayers (type int) The remaining number of players to fill the lobby
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
	 * Updates and display the number of remaining {@link it.polimi.ingsw.server.model.Player Players} from the {@link it.polimi.ingsw.notifications.Notification Notification}, or starts the game if the lobby is full
	 * @param notification (type Notification) The login notification
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
	
	/**
	 * Updates the text of the status label
	 * @param numberOfRemainingPlayers The remaining number of players to fill the lobby
	 */
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
	 * @param notification (type Notification) The player state notification
	 */
	private void didReceivePlayerStateMessage(Notification notification) {
		playerStateMessagesQueue.add(notification);
	}

	/**
	 * {@link it.polimi.ingsw.server.controller.network.messages.TableStateMessage TableStateMessage's} callBack, adds the message to the queue
	 * @param notification (type Notification) The table state notification
	 */
	private void didReceiveTableStateMessage(Notification notification) {
		tableMessage = notification;
	}

	/**
	 * {@link it.polimi.ingsw.server.controller.network.messages.ActivePlayerMessage ActivePlayerMessage's} callback, adds the message to the queue
	 * @param notification (type Notification) The active player notification
	 */
	private void didReceiveActivePlayerMessage(Notification notification) {
		activePlayerMessage = notification;
	}

	/**
	 * {@link it.polimi.ingsw.server.controller.network.messages.MatchStateMessage MatchStateMessage's} callback, adds the message to the queue
	 * @param notification (type Notification) The match phase notification
	 */
	private void didReceiveMatchPhaseMessage(Notification notification) {
		matchStateMessage = notification;
	}

	/**
	 * Moves to the main scene and forwards all the initial network messages to the next scene
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
		RescaleUtils.rescaleToCenter(matchVariantLabel, matchVariantLabelWidth, matchVariantLabelHeight, matchVariantLabelY, scale);
		RescaleUtils.rescaleToCenter(statusLabel, statusLabelWidth, statusLabelHeight, statusLabelY, scale);
		matchVariantLabel.setStyle("-fx-font-size: " + (matchVariantFont * scale));
		statusLabel.setStyle("-fx-font-size: " + (statusLabelFont * scale));
		
		LoginController.layoutEriantysHeader(scale, cranioImg, eriantysImg);
	}
}
