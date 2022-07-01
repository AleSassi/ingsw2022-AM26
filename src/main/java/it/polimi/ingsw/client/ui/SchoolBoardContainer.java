package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.ui.assistants.AssistantCardPane;
import it.polimi.ingsw.client.ui.rescale.RescalableAnchorPane;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.utils.ui.StudentDropTarget;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

/**
 * Class {@code SchoolBoardContainer} represent the container for the {@link it.polimi.ingsw.client.ui.SchoolBoardPane SchoolBoardPane}
 */
public class SchoolBoardContainer extends RescalableAnchorPane {
	
	private final Label playerLabel;
	private final SchoolBoardPane boardPane;
	private final boolean isPrimary;
	private final String titleTextNoCoins;
	private final String ownerNickname;
	private AssistantCardPane pickedAssistantCard;
	
	/**
	 * Constructor creates the {@code SchoolBoardContainer}
	 * @param isPrimary (type boolean) true if the {@link it.polimi.ingsw.client.ui.SchoolBoardPane SchoolBoardPane} is owned by the current Player
	 * @param ownerNickname (type String) owner's nickname
	 * @param teamName (type String) the player's team name, or null if no team exists
	 * @param coins (type int) {@link it.polimi.ingsw.server.model.Player Player's} coins (-1 means no-coin mode)
	 */
	public SchoolBoardContainer(boolean isPrimary, String ownerNickname, String teamName, int coins) {
		this.isPrimary = isPrimary;
		this.ownerNickname = ownerNickname;
		this.boardPane = new SchoolBoardPane(isPrimary, ownerNickname);
		this.titleTextNoCoins = isPrimary ? "Your School Board" : ownerNickname + "'s School Board";
		this.playerLabel = new Label(buildTitleWithCoinsAndTeam(coins, teamName));
		rescale(getCurrentScaleValue());
		getChildren().addAll(this.playerLabel, this.boardPane);
		NotificationCenter.shared().addObserver(this, this::didReceivePlayerStateNotification, NotificationName.ClientDidReceivePlayerStateMessage, null);
	}
	
	@Override
	public double getUnscaledWidth() {
		return this.boardPane.getUnscaledWidth() + 10 + (this.pickedAssistantCard == null ? 0 : this.pickedAssistantCard.getUnscaledWidth());
	}
	
	@Override
	public double getUnscaledHeight() {
		return this.boardPane.getUnscaledHeight() + getBoardPaneY();
	}
	
	/**
	 * Finds the unscaled board pane Y coordinate
	 * @return The unscaled board pane Y coordinate
	 */
	private double getBoardPaneY() {
		return isPrimary ? 28 : 23;
	}
	
	/**
	 * Finds the scaled height of the pane
	 * @param scale The scale factor to apply
	 * @return The scaled height of the pane
	 */
	public double getScaledHeight(double scale) {
		return (this.boardPane.getUnscaledHeight() * this.boardPane.getScalingValue() + getBoardPaneY()) * scale;
	}
	
	/**
	 * Gets the owner of the {@link it.polimi.ingsw.client.ui.SchoolBoardPane}
	 * @return (type String) return the owner of the {@link it.polimi.ingsw.client.ui.SchoolBoardPane}
	 */
	public String getOwnerNickname() {
		return boardPane.getOwnerNickname();
	}
	
	/**
	 * Gets the {@link it.polimi.ingsw.server.model.assistants.AssistantCard} the user has active
	 * @return the {@link it.polimi.ingsw.server.model.assistants.AssistantCard} of the user which owns this board
	 */
	public AssistantCard getPickedAssistant() {
		return pickedAssistantCard.getCard();
	}
	
	/**
	 * Forwards the initial {@code PlayerStatusNotification} to the {@code boardPane}
	 * @param notification (type Notification)
	 */
	public void forwardInitialPlayerStatusNotification(Notification notification) {
		boardPane.didReceivePlayerStatusNotification(notification);
	}

	/**
	 * {@code PlayerState} callback, used to update the assistant card
	 * @param notification (type Notification) The state notification
	 */
	private void didReceivePlayerStateNotification(Notification notification) {
		if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof PlayerStateMessage message) {
			if (message.getNickname().equals(ownerNickname)) {
				if (message.getAvailableCoins() >= 0) {
					Platform.runLater(() -> playerLabel.setText(buildTitleWithCoinsAndTeam(message.getAvailableCoins(), message.getTeamName())));
				}
				updatePickedAssistantCard(message.getLastPlayedAssistantCard());
			}
		}
	}

	/**
	 * Updates the picked {@link it.polimi.ingsw.client.ui.assistants.AssistantCardPane AssistantCardPane}
	 * @param pickedAssistant (type AssistantCard) The picked assistant
	 */
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

	/**
	 * Builds the title {@code Label} with the {@link it.polimi.ingsw.server.model.Player Player's} coins
	 * @param coins (type int) The number of Coins belonging to the Player
	 * @param teamName (type String) the name of the team the Player belongs to
	 * @return (type String) returns the title {@code Label} with the {@link it.polimi.ingsw.server.model.Player Player's} coins
	 */
	private String buildTitleWithCoinsAndTeam(int coins, String teamName) {
		String teamString = teamName == null ? "" : " - " + teamName;
		return (coins >= 0 ? titleTextNoCoins + " (Coins: " + coins + ")" : titleTextNoCoins) + teamString; //Interpret negative values as Coins not available
	}
	
	@Override
	public void rescale(double scale) {
		this.playerLabel.setLayoutX(5 * scale);
		this.playerLabel.setLayoutY(0);
		if (isPrimary) {
			this.playerLabel.setFont(new Font("Avenir", 20 * scale));
		} else {
			this.playerLabel.setFont(new Font("Avenir", 15 * scale));
		}
		this.boardPane.setLayoutY(getBoardPaneY() * scale);
		this.boardPane.setLayoutX(0);
		double scaledBoardPaneWidth = this.boardPane.getUnscaledWidth() * scale * this.boardPane.getScalingValue();
		double scaledBoardPaneHeight = this.boardPane.getUnscaledHeight() * scale * this.boardPane.getScalingValue();
		if (this.pickedAssistantCard != null) {
			double scaledAssistantHeight = pickedAssistantCard.getUnscaledWidth() * scale;
			this.pickedAssistantCard.setLayoutX(scaledBoardPaneWidth + (10 * scale));
			this.pickedAssistantCard.setLayoutY(scaledBoardPaneHeight - scaledAssistantHeight + boardPane.getLayoutY());
		}
		this.setPrefSize(scaledBoardPaneWidth, scaledBoardPaneHeight + getBoardPaneY() * scale);
	}

	/**
	 * Activates this {@code SchoolBoardContainer}
	 * @param active (type boolean) true if need to be set to active
	 */
	public void setActive(boolean active) {
		setDisabled(!active);
		this.boardPane.setDisable(!active);
	}

	/**
	 * Sets the allowed {@link it.polimi.ingsw.utils.ui.StudentDropTarget StudentDropTargets} for student movement
	 * @param validStudentDestinations (type StudentDropTarget[]) The valid student movement set
	 */
	public void setAllowedStudentMovements(StudentDropTarget[] validStudentDestinations) {
		boardPane.setAllowedStudentDestinationsForPhase(validStudentDestinations);
	}
}
