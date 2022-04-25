package it.polimi.ingsw.controller.network.messages;

import com.google.gson.*;
import it.polimi.ingsw.exceptions.model.MessageDecodeException;

public class PingPongMessage extends NetworkMessage {
	
	private Boolean isPing;
	
	public PingPongMessage(boolean isPing) {
		super();
		this.isPing = isPing;
	}
	
	public PingPongMessage(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
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
			isPing = decoded.isPing;
			
			if (isPing == null) {
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
		
		PingPongMessage that = (PingPongMessage) o;
		
		return isPing == that.isPing;
	}
}
