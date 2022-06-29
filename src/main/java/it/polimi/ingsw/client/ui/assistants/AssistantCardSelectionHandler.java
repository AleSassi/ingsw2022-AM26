package it.polimi.ingsw.client.ui.assistants;

import it.polimi.ingsw.server.model.assistants.AssistantCard;

@FunctionalInterface
public interface AssistantCardSelectionHandler {
	
	void handle(AssistantCard pickedAssistant);
	
}
