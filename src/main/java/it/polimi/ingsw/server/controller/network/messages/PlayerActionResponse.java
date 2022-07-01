package it.polimi.ingsw.server.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
/**
 * Class {@code PlayerActionResponse} represent the response message to a Player action
 */
public class PlayerActionResponse extends NetworkMessage {
	
	private String nickname;
	private PlayerActionMessage.ActionType actionType;
	private Boolean actionSuccess;
	private String descriptiveErrorMessage;
	
	/**
	 * Creates the response with the raw data
	 * @param nickname The nickname of the Player that executed the action
	 * @param actionType The attempted action type
	 * @param actionSuccess Whether the action was successful
	 * @param descriptiveErrorMessage A descriptive error message
	 */
	public PlayerActionResponse(@NotNull String nickname, @NotNull PlayerActionMessage.ActionType actionType, boolean actionSuccess, @NotNull String descriptiveErrorMessage) {
		this.nickname = nickname;
		this.actionType = actionType;
		this.actionSuccess = actionSuccess;
		this.descriptiveErrorMessage = descriptiveErrorMessage;
	}
	
	/**
	 * Decodes a JSON serialized string into a message
	 * @param serializedString The serialized string
	 * @throws MessageDecodeException If the decode fails
	 */
	public PlayerActionResponse(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
	/**
	 * Extracts the nickname of the Player that performed the action
	 * @return The nickname of the Player that performed the action
	 */
	public String getNickname() {
		return nickname;
	}
	
	/**
	 * Extracts the action type
	 * @return The action type
	 */
	public PlayerActionMessage.ActionType getActionType() {
		return actionType;
	}
	
	/**
	 * Finds out whether the action was successful
	 * @return Whether the action was successful
	 */
	public boolean isActionSuccess() {
		return actionSuccess;
	}
	
	/**
	 * Extracts the descriptive error message
	 * @return The descriptive error message
	 */
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
	public NotificationName clientReceivedMessageNotification() {
		return NotificationName.ClientDidReceivePlayerActionResponse;
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
