package it.polimi.ingsw.client.ui.assistants;

import it.polimi.ingsw.server.model.assistants.AssistantCard;

/**
 * Interface {@code AssistantCardSelectionHandler} for the control of the click events
 */
@FunctionalInterface
public interface AssistantCardSelectionHandler {
	
	void handle(AssistantCard pickedAssistant);
	
}
