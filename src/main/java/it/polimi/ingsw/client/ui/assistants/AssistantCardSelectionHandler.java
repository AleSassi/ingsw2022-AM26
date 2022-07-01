package it.polimi.ingsw.client.ui.assistants;

import it.polimi.ingsw.server.model.assistants.AssistantCard;

/**
 * Interface {@code AssistantCardSelectionHandler} represents a callback for executing an action depending on the chosen assistant card
 */
@FunctionalInterface
public interface AssistantCardSelectionHandler {
	
	/**
	 * Executes the action with the chosen assistant
	 * @param pickedAssistant The assistant chosen by the user
	 */
	void handle(AssistantCard pickedAssistant);
	
}
