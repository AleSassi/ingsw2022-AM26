package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.ui.rescale.RescalableAnchorPane;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.utils.ui.StudentDropTarget;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class SchoolBoardContainer extends RescalableAnchorPane {
	
	private final Label playerLabel;
	private final SchoolBoardPane boardPane;
	private final boolean isPrimary;
	
	public SchoolBoardContainer(boolean isPrimary, String ownerNickname, Integer coins) {
		this.isPrimary = isPrimary;
		this.boardPane = new SchoolBoardPane(isPrimary, ownerNickname);
		String titleString = isPrimary ? "Your School Board" : ownerNickname + "'s School Board";
		if (coins != null) {
			titleString += " (Coins: " + coins + ")";
		}
		this.playerLabel = new Label(titleString);
		rescale(1);
		getChildren().addAll(this.playerLabel, this.boardPane);
	}
	
	public String getOwnerNickname() {
		return boardPane.getOwnerNickname();
	}
	
	public void forwardInitialPlayerStatusNotification(Notification notification) {
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
