package it.polimi.ingsw.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.exceptions.model.MessageDecodeException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LoginResponse extends NetworkMessage {
	
	private String nickname;
	private Boolean loginAccepted;
	private Integer numberOfPlayersRemainingToFillLobby; //If 0 the match is about to start -> client prepares the UI for the match
	private String rejectionReason;
	
	public LoginResponse(@NotNull String nickname, boolean loginAccepted, int numberOfPlayersRemainingToFillLobby, String rejectionReason) {
		this.nickname = nickname;
		this.loginAccepted = loginAccepted;
		this.numberOfPlayersRemainingToFillLobby = numberOfPlayersRemainingToFillLobby;
		this.rejectionReason = rejectionReason;
	}
	
	public LoginResponse(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public boolean isLoginAccepted() {
		return loginAccepted;
	}
	
	public int getNumberOfPlayersRemainingToFillLobby() {
		return numberOfPlayersRemainingToFillLobby;
	}
	
	public String getRejectionReason() {
		return rejectionReason;
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
			LoginResponse decoded = gson.fromJson(serializedString, LoginResponse.class);
			nickname = decoded.nickname;
			loginAccepted = decoded.loginAccepted;
			numberOfPlayersRemainingToFillLobby = decoded.numberOfPlayersRemainingToFillLobby;
			rejectionReason = decoded.rejectionReason;
			
			if (nickname == null || loginAccepted == null || numberOfPlayersRemainingToFillLobby == null) {
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
		
		LoginResponse that = (LoginResponse) o;
		
		if (loginAccepted != that.loginAccepted) return false;
		if (!Objects.equals(numberOfPlayersRemainingToFillLobby, that.numberOfPlayersRemainingToFillLobby)) return false;
		if (!Objects.equals(rejectionReason, that.rejectionReason)) return false;
		return Objects.equals(nickname, that.nickname);
	}
}
