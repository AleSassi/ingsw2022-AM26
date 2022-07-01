package it.polimi.ingsw.client.ui.characters;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;
import it.polimi.ingsw.server.model.characters.CharacterCardBean;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

/**
 * Class {@code CharacterCardStopCardHostPane} represent the {@link it.polimi.ingsw.server.model.characters.CharacterCard CharacterCard} which sets Stop Cards
 */
public class CharacterCardStopCardHostPane extends CharacterCardGenericPane {
	
	private Label stopCardLabel;

	/**
	 * Constructor creates a new {@code CharacterCardStopCardHostPane}
	 * @param cardIndex (type int) {@link it.polimi.ingsw.server.model.characters.CharacterCard CharacterCard's} index in table list
	 * @param cardBean (type CharacterCardBean) The card data
	 */
	public CharacterCardStopCardHostPane(int cardIndex, CharacterCardBean cardBean) {
		super(cardIndex, cardBean);
	}
	
	@Override
	protected void performAdditionalInitializationForCard(int cardIndex, CharacterCardBean cardBean) {
		super.performAdditionalInitializationForCard(cardIndex, cardBean);
		stopCardLabel = new Label();
		stopCardLabel.setStyle("-fx-background-color: white");
		stopCardLabel.setFont(new Font("Avenir", 20));
		updateStopCardText(cardBean.getAvailableStopCards());
		AnchorPane.setRightAnchor(stopCardLabel, 0.0);
		AnchorPane.setBottomAnchor(stopCardLabel, 0.0);
		Platform.runLater(() -> getChildren().add(stopCardLabel));
	}

	/**
	 * Updates the StopCard {@code Label}
	 * @param newStopCardCount (type int) The number of Stop cards in the card
	 */
	private void updateStopCardText(int newStopCardCount) {
		Platform.runLater(() -> stopCardLabel.setText("SC: " + newStopCardCount));
	}
	
	@Override
	protected void didReceiveTableStateNotification(Notification notification) {
		super.didReceiveTableStateNotification(notification);
		if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof TableStateMessage tableStateMessage) {
			//Update with new state
			CharacterCardBean bean = tableStateMessage.getPlayableCharacterCards().get(getCharacterCardIndexInTableList());
			updateStopCardText(bean.getAvailableStopCards());
		}
	}
}
