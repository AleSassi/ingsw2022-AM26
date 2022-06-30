package it.polimi.ingsw.client.ui.assistants;

import it.polimi.ingsw.client.ui.ModalPopup;
import it.polimi.ingsw.server.model.assistants.AssistantCard;

public class AssistantCardPickerView extends ModalPopup {
	
	private final AssistantPickerPane assistantPickerPane;
	
	public AssistantCardPickerView(AssistantCard[] availableAssistantCards) {
		super("Choose your Assistant");
		assistantPickerPane = new AssistantPickerPane(availableAssistantCards);
		getChildren().add(assistantPickerPane);
		super.rescale(getCurrentScaleValue());
	}
	
	public void setSelectionHandler(AssistantCardSelectionHandler selectionHandler) {
		assistantPickerPane.setSelectionHandler(selectionHandler);
	}
	
	@Override
	public void rescaleChildren(double scale) {
		assistantPickerPane.setLayoutX(10 * scale);
		assistantPickerPane.setLayoutY(50 * scale);
	}
}
