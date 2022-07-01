package it.polimi.ingsw.client.ui.characters;

import it.polimi.ingsw.client.controller.network.GameClient;
import it.polimi.ingsw.client.ui.FaderPane;
import it.polimi.ingsw.client.ui.viewcontrollers.MainBoardController;
import it.polimi.ingsw.client.ui.rescale.RescalableAnchorPane;
import it.polimi.ingsw.jar.Client;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.*;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.characters.CharacterCardBean;
import it.polimi.ingsw.server.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.utils.ui.GUIUtils;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.Objects;

/**
 * Class {@code CharacterCardPane} represent the base abstract class for the rescalable pane used to display and use {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCards}
 */
public abstract class CharacterCardPane extends RescalableAnchorPane {
	
	private FaderPane disabledFader;
	private Label buyButton;
	private final Character character;
	private final int characterCardIndexInTableList;
	private int price;
	private int usesInTurnCount = 0;
	private boolean purchased = false;
	private boolean played = false;
	private MainBoardController parentController;

	/**
	 * Constructor creates the {@code CharacterCardPane} from the parameters
	 * @param cardIndex (type int) {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard's} index in the table list
	 * @param cardBean (type CharacterCardBean) {@code CharacterCard's} data
	 */
	public CharacterCardPane(int cardIndex, CharacterCardBean cardBean) {
		this.character = cardBean.getCharacter();
		this.characterCardIndexInTableList = cardIndex;
		this.price = cardBean.getTotalPrice();
		//Initialize the subscription to notifications for auto-update
		NotificationCenter.shared().addObserver(this, this::didReceiveActivePlayerMessage, NotificationName.ClientDidReceiveActivePlayerMessage, null);
		NotificationCenter.shared().addObserver(this, this::didReceivePlayerStateMessage, NotificationName.ClientDidReceivePlayerStateMessage, null);
		NotificationCenter.shared().addObserver(this, this::didReceiveTableStateNotification, NotificationName.ClientDidReceiveTableStateMessage, null);
		NotificationCenter.shared().addObserver(this, this::didReceiveCharacterCardLoopClosed, NotificationName.JavaFXDidEndCharacterCardLoop, null);
		NotificationCenter.shared().addObserver(this, this::didReceivePlayerActionResponse, NotificationName.ClientDidReceivePlayerActionResponse, null);
		//Init the style
		int cardID = cardBean.getCharacter().ordinal() + 1;
		GUIUtils.setStyleWithBackgroundImage(this, "images/Character/CarteTOT_front" + cardID + ".jpg");
		performAdditionalInitializationForCard(cardIndex, cardBean);
		setupBuyButton(cardBean.getTotalPrice());
		setupActionOnPlay();
		//Init size
		rescale(getCurrentScaleValue());
	}
	
	@Override
	public double getUnscaledWidth() {
		return 90;
	}
	
	@Override
	public double getUnscaledHeight() {
		return 136;
	}
	
	/**
	 * Allows subclasses to perform custom asset initialization safely
	 * @param cardIndex (type int) {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard's} index in the table list
	 * @param cardBean (type CharacterCardBean) {@code CharacterCard's} data
	 */
	protected abstract void performAdditionalInitializationForCard(int cardIndex, CharacterCardBean cardBean);

	/**
	 * Gets the {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard's} {@link it.polimi.ingsw.server.model.characters.Character Character}
	 * @return (type Character) returns the {@code CharacterCard's} {@code Character}
	 */
	protected Character getCharacter() {
		return character;
	}

	/**
	 * Gets the {@code index} of this {@code CharacterCardPane} from the table
	 * @return (type int) returns the {@code index} of this {@code CharacterCardPane} from the table
	 */
	public int getCharacterCardIndexInTableList() {
		return characterCardIndexInTableList;
	}

	/**
	 * Gets the {@code Fader's} z-position as a child of the card, so that all elements appear below it
	 * @return (type int) returns the {@code Fader's} z-position as a child of the card
	 */
	protected int getFaderZPosition() {
		return 0;
	}

	/**
	 * Returns true if this {@code CharacterCardPane} is purchased
	 * @return (type boolean) returns true if this {@code CharacterCardPane} is purchased
	 */
	public boolean isPurchased() {
		return purchased;
	}

	/**
	 * Sets the {@code Parent controller}
	 * @param controller (type MainBoardController)
	 */
	public void setParentController(MainBoardController controller) {
		parentController = controller;
	}

	/**
	 * Gets the {@code Parent controller}
	 * @return (type MainBoardController) returns the {@code Parent controller}
	 */
	public MainBoardController getParentController() {
		return parentController;
	}

	/**
	 * Sets up the {@code BuyButton} used to buy a card, or updates it if it is already active
	 * @param cardPrice (type int) {@link it.polimi.ingsw.server.model.characters.CharacterCard CharacterCard's} price
	 */
	private void setupBuyButton(int cardPrice) {
		if (!purchased) {
			Platform.runLater(() -> {
				if (buyButton == null) {
					buyButton = new Label("Buy: " + cardPrice);
					buyButton.setStyle("-fx-background-color: white");
					buyButton.setPickOnBounds(true);
					buyButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
						event.consume();
						//Send the Buy message to the server
						PlayerActionMessage actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidPurchaseCharacterCard, -1, null, false, -1, -1, -1, characterCardIndexInTableList, null);
						GameClient.shared().sendMessage(actionMessage);
					});
					getChildren().add(buyButton);
				} else {
					//Update
					buyButton.setText("Buy: " + cardPrice);
				}
			});
		}
	}

	/**
	 * Sets up the action depending on the played {@link it.polimi.ingsw.server.model.characters.CharacterCard CharacterCard}
	 */
	private void setupActionOnPlay() {
		//Depending on the character we have a different card action to perform
		//For example, the Ambassador card will activate the Islands, highlight them and then when an Island is Clicked we send the message
		if (character.getHostedStudentsCount() == 0 && !isCharacterPassive(character)) {
			addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
				event.consume();
				if (!purchased) return;
				if (usesInTurnCount >= character.getMaxNumberOfUsesInTurn()) {
					//Show an alert
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("The Character Card cannot be used anymore. You reached the maximum number of uses in this turn (" + character.getMaxNumberOfUsesInTurn() + ")");
					alert.show();
					return;
				}
				usesInTurnCount += 1;
				
				executeActionOnClick();
			});
		}
	}

	/**
	 * Executes the action after the click depending on which {@link it.polimi.ingsw.server.model.characters.CharacterCard CharacterCard} is
	 */
	protected abstract void executeActionOnClick();

	/**
	 * Callback for {@code PlayerAction} notifications, used to report if the card failed to execute
	 * @param notification (type Notification) The notification with the event data
	 */
	private void didReceivePlayerActionResponse(Notification notification) {
		if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof PlayerActionResponse message) {
			if (played && (character == Character.Ambassador || character == Character.Magician)) {
				Platform.runLater(() -> {
					Alert errorAlert = new Alert(Alert.AlertType.INFORMATION, "Card Report", ButtonType.CLOSE);
					errorAlert.setContentText("The card reported the following value: " + message.getDescriptiveErrorMessage());
					Platform.runLater(errorAlert::show);
				});
			}
			played = false;
		}
	}

	/**
	 * Callback for the {@code CharacterCardLoopClosed} notification, use dto intercept when the card needs to send its parameters to the server
	 * @param notification (type Notification)
	 */
	private void didReceiveCharacterCardLoopClosed(Notification notification) {
		if (notification.getUserInfo() != null && notification.getUserInfo().containsKey(NotificationKeys.JavaFXPlayedCharacter.getRawValue())) {
			Character character = (Character) notification.getUserInfo().get(NotificationKeys.JavaFXPlayedCharacter.getRawValue());
			if (character == this.character) {
				Student clickedStudentSrc = (Student) notification.getUserInfo().get(NotificationKeys.CharacterCardSourceStudent.getRawValue());
				Student clickedStudentDst = (Student) notification.getUserInfo().get(NotificationKeys.CharacterCardDestinationStudent.getRawValue());
				Integer targetIslandIdx = (Integer) notification.getUserInfo().get(NotificationKeys.CharacterCardTargetIslandIndex.getRawValue());
				Student[] tempStudents = new Student[]{clickedStudentSrc, clickedStudentDst};
				adaptStudentsForControlLoopClosed(tempStudents);
				clickedStudentSrc = tempStudents[0];
				clickedStudentDst = tempStudents[1];
				CharacterCardNetworkParamSet paramSet = new CharacterCardNetworkParamSet(clickedStudentSrc, clickedStudentDst, false, -1, targetIslandIdx != null ? targetIslandIdx : -1, targetIslandIdx != null ? targetIslandIdx : -1, CharacterCardParamSet.StopCardMovementMode.ToIsland);
				closeCardControlLoop(paramSet);
			} //Else discard, the notification is not for this card
		}
	}

	/**
	 * Adapts the {@link it.polimi.ingsw.server.model.student.Student Student's} of the {@link it.polimi.ingsw.server.model.characters.CharacterCard CharacterCard} to send to the {@link it.polimi.ingsw.server.controller.network.GameServer GameServer}
	 * @param clickedStudents (type Student[])
	 */
	protected abstract void adaptStudentsForControlLoopClosed(Student[] clickedStudents);

	/**
	 * Ends the {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard's} control loop and sends data to the server
	 * @param paramSet (type CharacterCardNetworkParamSet) sets of {@code CharacterCard's} parameters
	 */
	void closeCardControlLoop(CharacterCardNetworkParamSet paramSet) {
		PlayerActionMessage actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidPlayCharacterCard, -1, null, false, -1, -1, -1, characterCardIndexInTableList, paramSet);
		GameClient.shared().sendMessage(actionMessage);
		played = true;
	}

	/**
	 * Updates the state of a {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard}
	 * @param purchased (type boolean) true if the {@code CharacterCard} is purchased
	 * @param availableForPurchase (type boolean) true if the {@code CharacterCard} is available for purchase
	 */
	private void updateState(boolean purchased, boolean availableForPurchase) {
		this.purchased = purchased;
		if (!purchased) {
			if (disabledFader == null) {
				disabledFader = new FaderPane();
				Platform.runLater(() -> getChildren().add(getFaderZPosition(), disabledFader));
			}
			setupBuyButton(price);
			if (!availableForPurchase) {
				Platform.runLater(() -> {
					getChildren().remove(buyButton);
					buyButton = null;
				});
			}
		} else {
			//Remove the Buy button and fader
			Platform.runLater(() -> {
				getChildren().remove(buyButton);
				getChildren().remove(disabledFader);
				buyButton = null;
				disabledFader = null;
			});
		}
	}

	/**
	 * Callback for {@code TableState} notifications, used to update the card
	 * @param notification (type Notification) The notification with data
	 */
	protected void didReceiveTableStateNotification(Notification notification) {
		if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof TableStateMessage tableStateMessage) {
			//Update with new state
			CharacterCardBean bean = tableStateMessage.getPlayableCharacterCards().get(characterCardIndexInTableList);
			//Bey default all character cards are not purchased
			this.price = bean.getTotalPrice();
			updateState(false, true);
		}
	}
	
	/**
	 * Callback for {@code PlayerState} notifications, used to update the card ownership
	 * @param notification (type Notification) The notification with data
	 */
	private void didReceivePlayerStateMessage(Notification notification) {
		if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof PlayerStateMessage playerStateMessage) {
			//Update with new appearance if purchased
			if (playerStateMessage.getNickname().equals(Client.getNickname())) {
				updateState(Objects.equals(playerStateMessage.getActiveCharacterCardIdx(), characterCardIndexInTableList), playerStateMessage.getActiveCharacterCardIdx() == null);
			} else {
				updateState(purchased, !Objects.equals(playerStateMessage.getActiveCharacterCardIdx(), characterCardIndexInTableList));
			}
		}
	}
	
	/**
	 * Callback for {@code ActivePlayer} notifications, used to update the number of times used in this turn
	 * @param notification (type Notification) The notification with data
	 */
	private void didReceiveActivePlayerMessage(Notification notification) {
		if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof ActivePlayerMessage activePlayerMessage) {
			if (!activePlayerMessage.getActiveNickname().equals(Client.getNickname())) {
				usesInTurnCount = 0;
			}
		}
	}
	
	@Override
	public void rescale(double scale) {
	}

	/**
	 * Returns true if a {@link it.polimi.ingsw.server.model.characters.Character Character} is passive (requires no action after purchase)
	 * @param character (type Character) {@code Character} to check
	 * @return (type boolean) returns true if a {@link it.polimi.ingsw.server.model.characters.Character Character} is passive
	 */
	private boolean isCharacterPassive(Character character) {
		return character == Character.Swordsman || character == Character.Centaurus || character == Character.CheeseMan;
	}
}
