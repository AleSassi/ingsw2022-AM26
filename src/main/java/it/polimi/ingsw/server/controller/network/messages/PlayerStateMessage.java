package it.polimi.ingsw.server.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.SchoolBoard;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentHost;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
/**
 * Class {@code PlayerStateMessage} represent the message containing a full description of the Player and its data
 */
public class PlayerStateMessage extends NetworkMessage {
	
	private String nickname;
	private String teamName;
	private Integer activeCharacterCardIdx;
	private AssistantCard[] availableCardsDeck;
	private AssistantCard lastPlayedAssistantCard;
	private SchoolBoard board;
	private Integer availableCoins;
	private Wizard wizard;
	
	/**
	 * Constructs a message from its raw data
	 * @param player The Player
	 * @param activeCharacterCardIdx the active Character card index (<code>null</code> if no card is active)
	 * @param teamName The Player Team name
	 */
	public PlayerStateMessage(@NotNull Player player, Integer activeCharacterCardIdx, String teamName) {
		this.nickname = player.getNickname();
		this.activeCharacterCardIdx = activeCharacterCardIdx;
		this.availableCardsDeck = player.getAvailableAssistantCards().toArray(new AssistantCard[0]);
		this.lastPlayedAssistantCard = player.getLastPlayedAssistantCard();
		this.board = player.getBoard();
		this.availableCoins = player.getAvailableCoins();
		this.wizard = player.getWizard();
		this.teamName = teamName;
	}
	
	/**
	 * Decodes a JSON serialized string into a message
	 * @param serializedString The serialized string
	 * @throws MessageDecodeException If the decode fails
	 */
	public PlayerStateMessage(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
	/**
	 * Extracts the player nickname
	 * @return The player nickname
	 */
	public String getNickname() {
		return nickname;
	}
	
	/**
	 * Extracts the player team name
	 * @return The player team name
	 */
	public String getTeamName() {
		return teamName;
	}
	
	/**
	 * Extracts the active character chard index
	 * @return The active character card index
	 */
	public Integer getActiveCharacterCardIdx() {
		return activeCharacterCardIdx;
	}
	
	/**
	 * Extracts the list of available assistant cards
	 * @return The list of available assistant cards
	 */
	public AssistantCard[] getAvailableCardsDeck() {
		return availableCardsDeck;
	}
	
	/**
	 * Extracts the last played assistant card
	 * @return The last played assistant card
	 */
	public AssistantCard getLastPlayedAssistantCard() {
		return lastPlayedAssistantCard;
	}
	
	/**
	 * Extracts the School Board
	 * @return The School Board
	 */
	public SchoolBoard getBoard() {
		return board;
	}
	
	/**
	 * Extracts the number of coins
	 * @return The number of coins
	 */
	public int getAvailableCoins() {
		return availableCoins;
	}
	
	/**
	 * Extracts the wizard
	 * @return The wizard
	 */
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
			teamName = decoded.teamName;
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
		if (!Objects.equals(teamName, that.teamName))
			return false;
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
