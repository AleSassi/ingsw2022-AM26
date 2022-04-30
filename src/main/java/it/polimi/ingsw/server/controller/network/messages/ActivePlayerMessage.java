package it.polimi.ingsw.server.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.server.controller.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.model.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ActivePlayerMessage extends NetworkMessage {
	
	private String activeNickname;
	
	public ActivePlayerMessage(@NotNull Player activePlayer) {
		super();
		this.activeNickname = activePlayer.getNickname();
	}
	
	public ActivePlayerMessage(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
	public String getActiveNickname() {
		return activeNickname;
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
			ActivePlayerMessage decoded = gson.fromJson(serializedString, ActivePlayerMessage.class);
			activeNickname = decoded.activeNickname;
			if (activeNickname == null) {
				throw new MessageDecodeException();
			}
		} catch (JsonParseException e) {
			throw new MessageDecodeException();
		}
	}
	
	@Override
	public NotificationName clientReceivedMessageNotification() {
		return NotificationName.ClientDidReceiveActivePlayerMessage;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		ActivePlayerMessage that = (ActivePlayerMessage) o;
		
		return Objects.equals(activeNickname, that.activeNickname);
	}
}
