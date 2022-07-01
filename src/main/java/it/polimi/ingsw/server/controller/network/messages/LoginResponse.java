package it.polimi.ingsw.server.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
/**
 * Class {@code LoginResponse} represent the messages of the response of a login
 */
public class LoginResponse extends NetworkMessage {
	
	private String nickname;
	private Boolean loginAccepted;
	private Integer numberOfPlayersRemainingToFillLobby; //If 0 the match is about to start -> client prepares the UI for the match
	private String rejectionReason;
	
	/**
	 * Constructs a new response message with the raw data
	 * @param nickname The nickname of the PLayer that attempted to log in
	 * @param loginAccepted Whether the login was successful
	 * @param numberOfPlayersRemainingToFillLobby The number of players remaining before the lobby is full
	 * @param rejectionReason The reason why the login was rejected
	 */
	public LoginResponse(@NotNull String nickname, boolean loginAccepted, int numberOfPlayersRemainingToFillLobby, String rejectionReason) {
		this.nickname = nickname;
		this.loginAccepted = loginAccepted;
		this.numberOfPlayersRemainingToFillLobby = numberOfPlayersRemainingToFillLobby;
		this.rejectionReason = rejectionReason;
	}
	
	/**
	 * Constructs a new response message by deserializing a JSON string
	 * @param serializedString The JSON string to decode
	 * @throws MessageDecodeException If the sctring couldn't be decoded into this message
	 */
	public LoginResponse(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
	/**
	 * Finds the nickname of the Player contained in the response
 	 * @return The nickname of the Player contained in the response
	 */
	public String getNickname() {
		return nickname;
	}
	
	/**
	 * Finds whether the login was accepted or not
	 * @return Whether the login was accepted or not
	 */
	public boolean isLoginAccepted() {
		return loginAccepted;
	}
	
	/**
	 * Finds the number of players remaining to fill the lobby
	 * @return The number of players remaining to fill the lobby
	 */
	public int getNumberOfPlayersRemainingToFillLobby() {
		return numberOfPlayersRemainingToFillLobby;
	}
	
	/**
	 * Finds the rejection reason
	 * @return The rejection reason
	 */
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
	public NotificationName clientReceivedMessageNotification() {
		return NotificationName.ClientDidReceiveLoginResponse;
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
