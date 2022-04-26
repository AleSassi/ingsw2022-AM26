package it.polimi.ingsw.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.exceptions.model.MessageDecodeException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MatchTerminationMessage extends NetworkMessage {
	
	private String terminationReason;
	
	public MatchTerminationMessage(@NotNull String terminationReason, boolean ignored) {
		this.terminationReason = terminationReason;
	}
	
	public MatchTerminationMessage(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		MatchTerminationMessage that = (MatchTerminationMessage) o;
		
		return Objects.equals(terminationReason, that.terminationReason);
	}
}
