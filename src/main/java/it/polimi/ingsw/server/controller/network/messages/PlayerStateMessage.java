package it.polimi.ingsw.server.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.server.controller.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.model.SchoolBoard;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.assistants.Wizard;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PlayerStateMessage extends NetworkMessage {
	
	private String nickname;
	private Integer activeCharacterCardIdx;
	private AssistantCard[] availableCardsDeck;
	private AssistantCard lastPlayedAssistantCard;
	private SchoolBoard board;
	private Integer availableCoins;
	private Wizard wizard;
	
	public PlayerStateMessage(@NotNull String nickname, Integer activeCharacterCardIdx, @NotNull List<AssistantCard> availableCardsDeck, AssistantCard lastPlayedAssistantCard, @NotNull SchoolBoard board, int availableCoins, @NotNull Wizard wizard) {
		this.nickname = nickname;
		this.activeCharacterCardIdx = activeCharacterCardIdx;
		this.availableCardsDeck = new AssistantCard[availableCardsDeck.size()];
		availableCardsDeck.toArray(this.availableCardsDeck);
		this.lastPlayedAssistantCard = lastPlayedAssistantCard;
		this.board = board;
		this.availableCoins = availableCoins;
		this.wizard = wizard;
	}
	
	public PlayerStateMessage(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public Integer getActiveCharacterCardIdx() {
		return activeCharacterCardIdx;
	}
	
	public AssistantCard[] getAvailableCardsDeck() {
		return availableCardsDeck;
	}
	
	public AssistantCard getLastPlayedAssistantCard() {
		return lastPlayedAssistantCard;
	}
	
	public SchoolBoard getBoard() {
		return board;
	}
	
	public int getAvailableCoins() {
		return availableCoins;
	}
	
	public Wizard getWizard() {
		return wizard;
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
			PlayerStateMessage decoded = gson.fromJson(serializedString, PlayerStateMessage.class);
			nickname = decoded.nickname;
			activeCharacterCardIdx = decoded.activeCharacterCardIdx;
			availableCardsDeck = decoded.availableCardsDeck;
			lastPlayedAssistantCard = decoded.lastPlayedAssistantCard;
			board = decoded.board;
			availableCoins = decoded.availableCoins;
			wizard = decoded.wizard;
			
			if (nickname == null || availableCardsDeck == null || board == null || availableCoins == null || wizard == null) {
				throw new MessageDecodeException();
			}
		} catch (JsonParseException e) {
			throw new MessageDecodeException();
		}
	}
	
	@Override
	public NotificationName clientReceivedMessageNotification() {
		return NotificationName.ClientDidReceivePlayerStateMessage;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		PlayerStateMessage that = (PlayerStateMessage) o;
		
		if (!nickname.equals(that.nickname)) return false;
		if (!Objects.equals(activeCharacterCardIdx, that.activeCharacterCardIdx))
			return false;
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		if (!Arrays.equals(availableCardsDeck, that.availableCardsDeck)) return false;
		if (lastPlayedAssistantCard != that.lastPlayedAssistantCard) return false;
		if (!board.equals(that.board)) return false;
		if (!availableCoins.equals(that.availableCoins)) return false;
		return wizard == that.wizard;
	}
}
