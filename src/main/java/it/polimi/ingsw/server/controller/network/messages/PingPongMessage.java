package it.polimi.ingsw.server.controller.network.messages;

import com.google.gson.*;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
/**
 * Class {@code PingPongMessage} represent the PingPong message
 */
public class PingPongMessage extends NetworkMessage {
	
	private Boolean isPing;
	
	/**
	 * Creates a message with the Ping value
	 * @param isPing <code>true</code> if the message is a PING, <code>false</code> if it's a PONG
	 */
	public PingPongMessage(boolean isPing) {
		super();
		this.isPing = isPing;
	}
	
	/**
	 * Decodes a JSON serialized string into a PingPong message
	 * @param serializedString The serialized string
	 * @throws MessageDecodeException If the decode fails
	 */
	public PingPongMessage(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
	/**
	 * Finds out whether the message is PING or PONG
	 * @return Whether the message is PING or PONG
	 */
	public boolean isPing() {
		return isPing;
	}
	
	@Override
	public String serialize() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	@Override
	protected void deserialize(String serializedString) throws MessageDecodeException {
		Gson gson = new Gson();
		try {
			PingPongMessage decoded = gson.fromJson(serializedString, PingPongMessage.class);
			if (decoded == null || decoded.isPing == null) {
				throw new MessageDecodeException();
			}
			isPing = decoded.isPing;
		} catch (JsonParseException e) {
			throw new MessageDecodeException();
		}
	}
	
	@Override
	public NotificationName clientReceivedMessageNotification() {
		return null;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		PingPongMessage that = (PingPongMessage) o;
		
		return isPing == that.isPing;
	}
}
