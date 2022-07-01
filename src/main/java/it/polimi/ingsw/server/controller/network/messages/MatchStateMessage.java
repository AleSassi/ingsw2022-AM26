package it.polimi.ingsw.server.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.model.match.MatchPhase;
import org.jetbrains.annotations.NotNull;
/**
 * Class {@code MatchStateMessage} is the message for communicating the current Match phase
 */
public class MatchStateMessage extends NetworkMessage {
	
	private MatchPhase currentMatchPhase;
	
	/**
	 * Constructs a new response message with the raw data
	 * @param currentMatchPhase The current match phase
	 */
	public MatchStateMessage(@NotNull MatchPhase currentMatchPhase) {
		this.currentMatchPhase = currentMatchPhase;
	}
	
	/**
	 * Constructs a new response message by deserializing a JSON string
	 * @param serializedString The JSON string to decode
	 * @throws MessageDecodeException If the string couldn't be decoded into this message
	 */
	public MatchStateMessage(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
	/**
	 * Finds the current match phase
	 * @return The current match phase
	 */
	public MatchPhase getCurrentMatchPhase() {
		return currentMatchPhase;
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
			MatchStateMessage decoded = gson.fromJson(serializedString, MatchStateMessage.class);
			currentMatchPhase = decoded.currentMatchPhase;
			
			if (currentMatchPhase == null) {
				throw new MessageDecodeException();
			}
		} catch (JsonParseException e) {
			throw new MessageDecodeException();
		}
	}
	
	@Override
	public NotificationName clientReceivedMessageNotification() {
		return NotificationName.ClientDidReceiveMatchStateMessage;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		MatchStateMessage that = (MatchStateMessage) o;
		
		return currentMatchPhase == that.currentMatchPhase;
	}
}
