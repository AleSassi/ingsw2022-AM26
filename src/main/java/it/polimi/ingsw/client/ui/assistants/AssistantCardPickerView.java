package it.polimi.ingsw.client.ui.assistants;

import it.polimi.ingsw.client.ui.ModalPopup;
import it.polimi.ingsw.server.model.assistants.AssistantCard;

/**
 * Class {@code AssistantCardPickerView} represent the JavaFX controller for the {@link it.polimi.ingsw.client.ui.assistants.AssistantPickerPane AssistantPickerPane}
 */
public class AssistantCardPickerView extends ModalPopup {
	
	private final AssistantPickerPane assistantPickerPane;

	/**
	 * Constructor creates the view
	 * @param availableAssistantCards (type AssistantCard[])
	 */
	public AssistantCardPickerView(AssistantCard[] availableAssistantCards) {
		super("Choose your Assistant");
		assistantPickerPane = new AssistantPickerPane(availableAssistantCards);
		getChildren().add(assistantPickerPane);
		super.rescale(getCurrentScaleValue());
	}

	/**
	 * Sets the {@link it.polimi.ingsw.client.ui.assistants.AssistantCardSelectionHandler  AssistantCardSelectionHandler}
	 * @param selectionHandler (type AssistantCardSelectionHandler)
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
