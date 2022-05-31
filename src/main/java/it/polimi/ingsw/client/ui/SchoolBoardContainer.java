package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.utils.ui.StudentDropTarget;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public class SchoolBoardContainer extends RescalableAnchorPane {
	
	private Label playerLabel;
	private SchoolBoardPane boardPane;
	private final boolean isPrimary;
	
	public SchoolBoardContainer(boolean isPrimary, String ownerNickname) {
		this.isPrimary = isPrimary;
		this.boardPane = new SchoolBoardPane(isPrimary, ownerNickname);
		this.playerLabel = new Label(isPrimary ? "Your School Board" : ownerNickname + "'s School Board");
		rescale(1);
		getChildren().addAll(this.playerLabel, this.boardPane);
	}
	
	protected String getOwnerNickname() {
		return boardPane.getOwnerNickname();
	}
	
	protected void forwardInitialPlayerStatusNotification(Notification notification) {
		boardPane.didReceivePlayerStatusNotification(notification);
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
