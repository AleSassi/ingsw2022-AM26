package it.polimi.ingsw.server.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
/**
 * Class {@code MatchTerminationMessage} is the message used to tell that a Match must forcibly end
 */
public class MatchTerminationMessage extends NetworkMessage {
	
	private String terminationReason;
	
	/**
	 * Constructs a new response message with the raw data
	 * @param terminationReason The termination reason
	 * @param ignored A string param used to distinguish the constructor from the auto-decode one
	 */
	public MatchTerminationMessage(@NotNull String terminationReason, boolean ignored) {
		this.terminationReason = terminationReason;
	}
	
	/**
	 * Constructs a new response message by deserializing a JSON string
	 * @param serializedString The JSON string to decode
	 * @throws MessageDecodeException If the string couldn't be decoded into this message
	 */
	public MatchTerminationMessage(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
	/**
	 * Finds the termination reason
	 * @return The termination reason
	 */
	public String getTerminationReason() {
		return terminationReason;
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
			MatchTerminationMessage decoded = gson.fromJson(serializedString, MatchTerminationMessage.class);
			terminationReason = decoded.terminationReason;
			
			if (terminationReason == null) {
				throw new MessageDecodeException();
			}
		} catch (JsonParseException e) {
			throw new MessageDecodeException();
		}
	}
	
	@Override
	public NotificationName clientReceivedMessageNotification() {
		return NotificationName.ClientDidReceiveMatchTerminationMessage;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		MatchTerminationMessage that = (MatchTerminationMessage) o;
		
		return Objects.equals(terminationReason, that.terminationReason);
	}
}
