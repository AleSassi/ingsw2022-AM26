package it.polimi.ingsw.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.exceptions.MessageDecodeException;
import it.polimi.ingsw.model.match.MatchPhase;
import org.jetbrains.annotations.NotNull;

public class MatchStateMessage extends NetworkMessage {
	
	private MatchPhase currentMatchPhase;
	
	public MatchStateMessage(@NotNull MatchPhase currentMatchPhase) {
		this.currentMatchPhase = currentMatchPhase;
	}
	
	public MatchStateMessage(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
	public MatchPhase getCurrentMatchPhase() {
		return currentMatchPhase;
	}
	
	@Override
	String serialize() {
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		MatchStateMessage that = (MatchStateMessage) o;
		
		return currentMatchPhase == that.currentMatchPhase;
	}
}
