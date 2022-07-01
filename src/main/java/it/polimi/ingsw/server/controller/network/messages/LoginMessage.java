package it.polimi.ingsw.server.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.match.MatchVariant;
import org.jetbrains.annotations.NotNull;

/**
 * Class {@code LoginMessage} represents the message of a new login
 */
public class LoginMessage extends NetworkMessage {
	
	private String nickname;
	private Integer desiredNumberOfPlayers;
	private MatchVariant matchVariant;
	private Wizard chosenWizard;
	
	public LoginMessage(@NotNull String nickname, int desiredNumberOfPlayers, @NotNull MatchVariant matchVariant, @NotNull Wizard chosenWizard) {
		this.nickname = nickname;
		this.desiredNumberOfPlayers = desiredNumberOfPlayers;
		this.matchVariant = matchVariant;
		this.chosenWizard = chosenWizard;
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
	
	public Wizard getChosenWizard() {
		return chosenWizard;
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
			LoginMessage decoded = gson.fromJson(serializedString, LoginMessage.class);
			nickname = decoded.nickname;
			desiredNumberOfPlayers = decoded.desiredNumberOfPlayers;
			matchVariant = decoded.matchVariant;
			chosenWizard = decoded.chosenWizard;
			
			if (nickname == null || desiredNumberOfPlayers == null || matchVariant == null || chosenWizard == null) {
				throw new MessageDecodeException();
			}
		} catch (JsonParseException e) {
			throw new MessageDecodeException();
		}
	}
	
	@Override
	public NotificationName clientReceivedMessageNotification() {
		return null;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		LoginMessage that = (LoginMessage) o;
		
		if (!nickname.equals(that.nickname)) return false;
		if (!desiredNumberOfPlayers.equals(that.desiredNumberOfPlayers)) return false;
		if (matchVariant != that.matchVariant) return false;
		return chosenWizard == that.chosenWizard;
	}
	
}
