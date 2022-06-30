package it.polimi.ingsw.server.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
/**
 * Class {@code NetworkMessageDecoder} represent the message decoder
 */
public class NetworkMessageDecoder {
	
	private static class DummyMessage extends NetworkMessage {
		
		public DummyMessage(String serializedString) throws MessageDecodeException {
			super(serializedString);
		}
		
		public DummyMessage() {
			super();
		}
		
		@Override
		public String serialize() {
			return null;
		}
		
		@Override
		protected void deserialize(String serializedString) throws MessageDecodeException {
			Gson gson = new Gson();
			try {
				DummyMessage decoded = gson.fromJson(serializedString, DummyMessage.class);
				setClassType(decoded.getClassType());
			} catch (JsonParseException e) {
				throw new MessageDecodeException();
			}
		}
		
		@Override
		public NotificationName clientReceivedMessageNotification() {
			return null;
		}
	}
	
	public NetworkMessage decodeMessage(String serializedString) throws MessageDecodeException {
		DummyMessage dummyMessage = new DummyMessage(serializedString);
		switch (dummyMessage.getClassType()) {
			case "ActivePlayerMessage" -> {
				return new ActivePlayerMessage(serializedString);
			}
			case "LoginMessage" -> {
				return new LoginMessage(serializedString);
			}
			case "LoginResponse" -> {
				return new LoginResponse(serializedString);
			}
			case "MatchStateMessage" -> {
				return new MatchStateMessage(serializedString);
			}
			case "MatchTerminationMessage" -> {
				return new MatchTerminationMessage(serializedString);
			}
			case "PingPongMessage" -> {
				return new PingPongMessage(serializedString);
			}
			case "PlayerActionMessage" -> {
				return new PlayerActionMessage(serializedString);
			}
			case "PlayerActionResponse" -> {
				return new PlayerActionResponse(serializedString);
			}
			case "PlayerStateMessage" -> {
				return new PlayerStateMessage(serializedString);
			}
			case "TableStateMessage" -> {
				return new TableStateMessage(serializedString);
			}
			case "VictoryMessage" -> {
				return new VictoryMessage(serializedString);
			}
			default -> throw new MessageDecodeException();
		}
	}
}