package it.polimi.ingsw.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.exceptions.model.MessageDecodeException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PlayerActionResponse extends NetworkMessage {
	
	private String nickname;
	private PlayerActionMessage.ActionType actionType;
	private Boolean actionSuccess;
	private String descriptiveErrorMessage;
	
	public PlayerActionResponse(@NotNull String nickname, @NotNull PlayerActionMessage.ActionType actionType, boolean actionSuccess, @NotNull String descriptiveErrorMessage) {
		this.nickname = nickname;
		this.actionType = actionType;
		this.actionSuccess = actionSuccess;
		this.descriptiveErrorMessage = descriptiveErrorMessage;
	}
	
	public PlayerActionResponse(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public PlayerActionMessage.ActionType getActionType() {
		return actionType;
	}
	
	public boolean isActionSuccess() {
		return actionSuccess;
	}
	
	public String getDescriptiveErrorMessage() {
		return descriptiveErrorMessage;
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
			PlayerActionResponse decoded = gson.fromJson(serializedString, PlayerActionResponse.class);
			nickname = decoded.nickname;
			actionType = decoded.actionType;
			actionSuccess = decoded.actionSuccess;
			descriptiveErrorMessage = decoded.descriptiveErrorMessage;
			
			if (nickname == null || actionType == null || actionSuccess == null || descriptiveErrorMessage == null) {
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
		
		PlayerActionResponse that = (PlayerActionResponse) o;
		
		if (!actionSuccess.equals(that.actionSuccess)) return false;
		if (!Objects.equals(nickname, that.nickname)) return false;
		if (actionType != that.actionType) return false;
		return Objects.equals(descriptiveErrorMessage, that.descriptiveErrorMessage);
	}
}
