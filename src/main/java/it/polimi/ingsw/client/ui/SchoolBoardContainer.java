package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.ui.assistants.AssistantCardPane;
import it.polimi.ingsw.client.ui.rescale.RescalableAnchorPane;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.utils.ui.StudentDropTarget;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

public class SchoolBoardContainer extends RescalableAnchorPane {
	
	private final Label playerLabel;
	private final SchoolBoardPane boardPane;
	private final boolean isPrimary;
	private final String titleTextNoCoins;
	private final String ownerNickname;
	private AssistantCardPane pickedAssistantCard;
	
	public SchoolBoardContainer(boolean isPrimary, String ownerNickname, int coins) {
		this.isPrimary = isPrimary;
		this.ownerNickname = ownerNickname;
		this.boardPane = new SchoolBoardPane(isPrimary, ownerNickname);
		this.titleTextNoCoins = isPrimary ? "Your School Board" : ownerNickname + "'s School Board";
		this.playerLabel = new Label(buildTitleWithCoins(coins));
		rescale(getCurrentScaleValue());
		getChildren().addAll(this.playerLabel, this.boardPane);
		NotificationCenter.shared().addObserver(this, this::didReceivePlayerStateNotification, NotificationName.ClientDidReceivePlayerStateMessage, null);
	}
	
	public String getOwnerNickname() {
		return boardPane.getOwnerNickname();
	}
	
	public void forwardInitialPlayerStatusNotification(Notification notification) {
		boardPane.didReceivePlayerStatusNotification(notification);
	}
	
	private void didReceivePlayerStateNotification(Notification notification) {
		if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof PlayerStateMessage message) {
			if (message.getNickname().equals(ownerNickname)) {
				if (message.getAvailableCoins() >= 0) {
					Platform.runLater(() -> playerLabel.setText(buildTitleWithCoins(message.getAvailableCoins())));
				}
				updatePickedAssistantCard(message.getLastPlayedAssistantCard());
			}
		}
	}
	
	private void updatePickedAssistantCard(AssistantCard pickedAssistant) {
		if (pickedAssistant == null) return;
		
		AssistantCardPane pickedAssistantCard = new AssistantCardPane(pickedAssistant);
		pickedAssistantCard.setScalingValue(0.7);
		Platform.runLater(() -> {
			if (this.pickedAssistantCard != null) {
				getChildren().remove(this.pickedAssistantCard);
			}
			getChildren().add(pickedAssistantCard);
			this.pickedAssistantCard = pickedAssistantCard;
			rescale(getCurrentScaleValue());
		});
	}
	
	private String buildTitleWithCoins(int coins) {
		return coins >= 0 ? titleTextNoCoins + " (Coins: " + coins + ")" : titleTextNoCoins; //Interpret negative values as Coins not available
	}
	
	public void rescale(double scale) {
		this.playerLabel.setLayoutX(5 * scale);
		this.playerLabel.setLayoutY(0);
		if (isPrimary) {
			this.playerLabel.setFont(new Font("Avenir", 20 * scale));
			this.boardPane.setLayoutY(28 * scale);
		} else {
			this.playerLabel.setFont(new Font("Avenir", 15 * scale));
			this.boardPane.setLayoutY(23 * scale);
		}
		this.boardPane.setLayoutX(0);
		if (this.pickedAssistantCard != null) {
			this.pickedAssistantCard.setLayoutX(this.boardPane.getPrefWidth() + (10 * scale));
			this.pickedAssistantCard.setLayoutY(this.boardPane.getPrefHeight() - pickedAssistantCard.getPrefHeight() + boardPane.getLayoutY());
		}
		this.setPrefSize(this.boardPane.getPrefWidth(), this.boardPane.getPrefHeight() + this.boardPane.getLayoutY());
	}
	
	public void setActive(boolean active) {
		setDisabled(!active);
		this.boardPane.setDisable(!active);
	}
	
	public void setAllowedStudentMovements(StudentDropTarget[] validStudentDestinations) {
		boardPane.setAllowedStudentDestinationsForPhase(validStudentDestinations);
	}
}
