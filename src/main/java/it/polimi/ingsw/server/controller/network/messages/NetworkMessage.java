package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;

public abstract class NetworkMessage {
	
	NetworkMessage(String serializedString) throws MessageDecodeException {
		deserialize(serializedString);
	}
	
	NetworkMessage() {}
	
	public abstract String serialize();
	protected abstract void deserialize(String serializedString) throws MessageDecodeException;
	public abstract NotificationName clientReceivedMessageNotification();
}
