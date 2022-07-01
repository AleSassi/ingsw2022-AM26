package it.polimi.ingsw.server.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
/**
 * Class {@code VictoryMessage} represent the Victory message
 */
public class VictoryMessage extends NetworkMessage {
	
	private String[] winners;
	
	/**
	 * Constructs a Victory message with its raw data
	 * @param winners The list of winner nicknames
	 */
	public VictoryMessage(@NotNull String[] winners) {
		this.winners = winners;
	}
	
	/**
	 * Decodes a JSON serialized string into a message
	 * @param serializedString The serialized string
	 * @throws MessageDecodeException If the decode fails
	 */
	public VictoryMessage(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
	public String[] getWinners() {
		return winners;
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
			VictoryMessage decoded = gson.fromJson(serializedString, VictoryMessage.class);
			winners = decoded.winners;
			
			if (winners == null) {
				throw new MessageDecodeException();
			}
		} catch (JsonParseException e) {
			throw new MessageDecodeException();
		}
	}
	
	@Override
	public NotificationName clientReceivedMessageNotification() {
		return NotificationName.ClientDidReceiveVictoryMessage;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		VictoryMessage that = (VictoryMessage) o;
		
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		return Arrays.equals(winners, that.winners);
	}
}
