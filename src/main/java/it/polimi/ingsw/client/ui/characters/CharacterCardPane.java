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
		rescale(1);
	}
	
	protected abstract void performAdditionalInitializationForCard(int cardIndex, CharacterCardBean cardBean);
	
	protected Character getCharacter() {
		return character;
	}
	
	public int getCharacterCardIndexInTableList() {
		return characterCardIndexInTableList;
	}
	
	protected int getFaderZPosition() {
		return 0;
	}
	
	public boolean isPurchased() {
		return purchased;
	}
	
	public void setParentController(MainBoardController controller) {
		parentController = controller;
	}
	
	public MainBoardController getParentController() {
		return parentController;
	}
	
	private void setupBuyButton(int cardPrice) {
		if (!purchased) {
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
				Platform.runLater(() -> getChildren().add(buyButton));
			} else {
				//Update
				Platform.runLater(() -> buyButton.setText("Buy: " + cardPrice));
			}
		}
	}
	
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
	
	protected abstract void executeActionOnClick();
	
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
	
	protected abstract void adaptStudentsForControlLoopClosed(Student[] clickedStudents);
	
	void closeCardControlLoop(CharacterCardNetworkParamSet paramSet) {
		PlayerActionMessage actionMessage = new PlayerActionMessage(Client.getNickname(), PlayerActionMessage.ActionType.DidPlayCharacterCard, -1, null, false, -1, -1, -1, characterCardIndexInTableList, paramSet);
		GameClient.shared().sendMessage(actionMessage);
		played = true;
	}
	
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
	
	protected void didReceiveTableStateNotification(Notification notification) {
		if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof TableStateMessage tableStateMessage) {
			//Update with new state
			CharacterCardBean bean = tableStateMessage.getPlayableCharacterCards().get(characterCardIndexInTableList);
			//Bey default all character cards are not purchased
			this.price = bean.getTotalPrice();
			updateState(false, true);
		}
	}
	
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
	
	private boolean isCharacterPassive(Character character) {
		return character == Character.Swordsman || character == Character.Centaurus || character == Character.CheeseMan;
	}
}
