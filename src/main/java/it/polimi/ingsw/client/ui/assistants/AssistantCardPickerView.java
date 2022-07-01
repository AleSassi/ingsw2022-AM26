package it.polimi.ingsw.client.ui.assistants;

import it.polimi.ingsw.client.ui.ModalPopup;
import it.polimi.ingsw.server.model.assistants.AssistantCard;

/**
 * A modal popup which presents the user with a grid of possible assistant card choices
 */
public class AssistantCardPickerView extends ModalPopup {
	
	private final AssistantPickerPane assistantPickerPane;

	/**
	 * Constructor creates the popup with the list of available assistant cards
	 * @param availableAssistantCards (type AssistantCard[]) The list of available assistant cards to display
	 */
	public AssistantCardPickerView(AssistantCard[] availableAssistantCards) {
		super("Choose your Assistant");
		assistantPickerPane = new AssistantPickerPane(availableAssistantCards);
		getChildren().add(assistantPickerPane);
		super.rescale(getCurrentScaleValue());
	}

	/**
	 * Sets the {@link it.polimi.ingsw.client.ui.assistants.AssistantCardSelectionHandler  AssistantCardSelectionHandler} to use when a card is clicked
	 * @param selectionHandler (type AssistantCardSelectionHandler) The callback for a click on a card
	 */
	public void setSelectionHandler(AssistantCardSelectionHandler selectionHandler) {
		assistantPickerPane.setSelectionHandler(selectionHandler);
	}
	
	@Override
	public void rescaleChildren(double scale) {
		assistantPickerPane.setLayoutX(10 * scale);
		assistantPickerPane.setLayoutY(50 * scale);
	}
}
