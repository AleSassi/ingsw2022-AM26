package it.polimi.ingsw.controller.network.messages;

import it.polimi.ingsw.exceptions.model.MessageDecodeException;

public abstract class NetworkMessage {
	
	NetworkMessage(String serializedString) throws MessageDecodeException {
		deserialize(serializedString);
	}
	
	NetworkMessage() {}
	
	abstract String serialize();
	protected abstract void deserialize(String serializedString) throws MessageDecodeException;
}
