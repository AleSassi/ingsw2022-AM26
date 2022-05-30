package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.utils.ui.StudentDropTarget;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public class SchoolBoardContainer extends AnchorPane implements JavaFXRescalable {
	
	private Label playerLabel;
	private SchoolBoardPane boardPane;
	private final boolean isPrimary;
	
	public SchoolBoardContainer(boolean isPrimary, String ownerNickname) {
		this.isPrimary = isPrimary;
		this.boardPane = new SchoolBoardPane(isPrimary, ownerNickname);
		this.playerLabel = new Label(isPrimary ? "Your School Board" : ownerNickname + "'s School Board");
		rescale(1);
		getChildren().addAll(this.playerLabel, this.boardPane);
		
		NotificationCenter.shared().addObserver(this::didReceiveWindowDidResizeNotification, NotificationName.JavaFXWindowDidResize, null);
	}
	
	protected String getOwnerNickname() {
		return boardPane.getOwnerNickname();
	}
	
	protected void forwardInitialPlayerStatusNotification(Notification notification) {
		boardPane.didReceivePlayerStatusNotification(notification);
	}
	
	private void didReceiveWindowDidResizeNotification(Notification notification) {
		if (notification.getUserInfo() != null) {
			if (notification.getUserInfo().containsKey("newWidth")) {
				// When we resize the width dimension, the container should not change (stays anchored to the left side of the window)
			} else if (notification.getUserInfo().containsKey("newHeight")) {
				// When we resize the height dimension, the container should rescale to fit into the container
				double newHeight = ((Number) notification.getUserInfo().get("newHeight")).doubleValue();
				double heightScale = newHeight / GUI.referenceHeight;
				double widthScale = GUI.getWindowWidth() / GUI.referenceWidth;
				double scale = Math.min(widthScale, heightScale);
				rescale(scale);
			}
		}
	}
	
	public void rescale(double scale) {
		this.boardPane.rescale(scale);
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
