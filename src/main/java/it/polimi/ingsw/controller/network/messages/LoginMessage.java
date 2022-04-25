package it.polimi.ingsw.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.exceptions.MessageDecodeException;
import it.polimi.ingsw.model.match.MatchVariant;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LoginMessage extends NetworkMessage {
	
	private String nickname;
	private Integer desiredNumberOfPlayers;
	private MatchVariant matchVariant;
	
	public LoginMessage(@NotNull String nickname, int desiredNumberOfPlayers, @NotNull MatchVariant matchVariant) {
		this.nickname = nickname;
		this.desiredNumberOfPlayers = desiredNumberOfPlayers;
		this.matchVariant = matchVariant;
	}
	
	public LoginMessage(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public int getDesiredNumberOfPlayers() {
		return desiredNumberOfPlayers;
	}
	
	public MatchVariant getMatchVariant() {
		return matchVariant;
	}
	
	@Override
	String serialize() {
		//TODO: Can we put this code into the Network Message Abstract Class?
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	@Override
	protected void deserialize(String serializedString) throws MessageDecodeException {
		Gson gson = new Gson();
		try {
			LoginMessage decoded = gson.fromJson(serializedString, LoginMessage.class);
			nickname = decoded.nickname;
			desiredNumberOfPlayers = decoded.desiredNumberOfPlayers;
			matchVariant = decoded.matchVariant;
			
			if (nickname == null || desiredNumberOfPlayers == null || matchVariant == null) {
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
		
		LoginMessage that = (LoginMessage) o;
		
		if (!Objects.equals(desiredNumberOfPlayers, that.desiredNumberOfPlayers)) return false;
		if (!Objects.equals(nickname, that.nickname)) return false;
		return matchVariant == that.matchVariant;
	}
}
