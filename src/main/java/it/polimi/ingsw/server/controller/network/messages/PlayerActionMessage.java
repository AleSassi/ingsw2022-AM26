package it.polimi.ingsw.server.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.model.match.MatchPhase;
import it.polimi.ingsw.server.model.student.Student;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
/**
 * Class {@code PlayerActionMessage} represent the PlayerAction message
 */
public class PlayerActionMessage extends NetworkMessage {
	
	private String nickname;
	private ActionType playerActionType;
	// Planning phase
	// Assistant card
	private Integer assistantIndex;
	// Action phase
	// Student Movement
	private Student movedStudent;
	private Boolean movesToIsland; // If false moves the Student to the Dining Room
	private Integer destinationIslandIndex;
	// Mother Nature Movement
	private Integer chosenMNBaseSteps;
	// Cloud tile choice
	private Integer chosenCloudTileIndex;
	// Character cards
	private Integer chosenCharacterIndex;
	private CharacterCardNetworkParamSet characterCardParameters;
	
	/**
	 * Constructs a Player Action message with the raw data
	 * @param nickname The nickname of the Player that performed the action
	 * @param playerActionType The action type
	 * @param assistantIndex The chosen assistant card index
	 * @param movedStudent The chosen Student to move
	 * @param movesToIsland Whether the student moves to an Island or to the Dining Room
	 * @param destinationIslandIndex The destination island index
	 * @param chosenMNBaseSteps The number of steps Mother Nature must move by
	 * @param chosenCloudTileIndex The chosen cloud tile index
	 * @param chosenCharacterIndex The chosen character card index
	 * @param characterCardParameters The chosen character card parameters
	 */
	public PlayerActionMessage(@NotNull String nickname, @NotNull ActionType playerActionType, int assistantIndex, Student movedStudent, boolean movesToIsland, int destinationIslandIndex, int chosenMNBaseSteps, int chosenCloudTileIndex, int chosenCharacterIndex, CharacterCardNetworkParamSet characterCardParameters) {
		this.nickname = nickname;
		this.playerActionType = playerActionType;
		this.assistantIndex = assistantIndex;
		this.movedStudent = movedStudent;
		this.movesToIsland = movesToIsland;
		this.destinationIslandIndex = destinationIslandIndex;
		this.chosenMNBaseSteps = chosenMNBaseSteps;
		this.chosenCloudTileIndex = chosenCloudTileIndex;
		this.chosenCharacterIndex = chosenCharacterIndex;
		this.characterCardParameters = characterCardParameters;
	}
	
	/**
	 * Decodes a JSON serialized string into a message
	 * @param serializedString The serialized string
	 * @throws MessageDecodeException If the decode fails
	 */
	public PlayerActionMessage(String serializedString) throws MessageDecodeException {
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
	 * Extracts the performed action type
	 * @return The performed action type
	 */
	public ActionType getPlayerActionType() {
		return playerActionType;
	}
	
	/**
	 * Extracts the chosen assistant index
	 * @return The chosen assistant index
	 */
	public int getAssistantIndex() {
		return assistantIndex;
	}
	
	/**
	 * Extracts the moved student
	 * @return The moved student
	 */
	public Student getMovedStudent() {
		return movedStudent;
	}
	
	/**
	 * Finds whether the student must move to an Island or to the Dining room
	 * @return Whether the student must move to an Island or to the Dining room
	 */
	public boolean isMovesToIsland() {
		return movesToIsland;
	}
	
	/**
	 * Extracts the destination island index
	 * @return The destination island index
	 */
	public int getDestinationIslandIndex() {
		return destinationIslandIndex;
	}
	
	/**
	 * Extracts the chosen Mother Nature steps
	 * @return The chosen Mother Nature steps
	 */
	public int getChosenMNBaseSteps() {
		return chosenMNBaseSteps;
	}
	
	/**
	 * Extracts the chosen Cloud tile index
	 * @return The chosen Cloud tile index
	 */
	public int getChosenCloudTileIndex() {
		return chosenCloudTileIndex;
	}
	
	/**
	 * Extracts the chosen Character card index
	 * @return The chosen Character card index
	 */
	public int getChosenCharacterIndex() {
		return chosenCharacterIndex;
	}
	
	/**
	 * Extracts the Character card usage parameters
	 * @return The Character card usage parameters
	 */
	public CharacterCardNetworkParamSet getCharacterCardParameters() {
		return characterCardParameters;
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
			PlayerActionMessage decoded = gson.fromJson(serializedString, PlayerActionMessage.class);
			nickname = decoded.nickname;
			playerActionType = decoded.playerActionType;
			assistantIndex = decoded.assistantIndex;
			movedStudent = decoded.movedStudent;
			movesToIsland = decoded.movesToIsland;
			destinationIslandIndex = decoded.destinationIslandIndex;
			chosenMNBaseSteps = decoded.chosenMNBaseSteps;
			chosenCloudTileIndex = decoded.chosenCloudTileIndex;
			chosenCharacterIndex = decoded.chosenCharacterIndex;
			characterCardParameters = decoded.characterCardParameters;
			
			if (nickname == null || playerActionType == null || assistantIndex == null || movesToIsland == null || destinationIslandIndex == null || chosenCloudTileIndex == null || chosenCharacterIndex == null) {
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
		
		PlayerActionMessage that = (PlayerActionMessage) o;
		
		if (!Objects.equals(assistantIndex, that.assistantIndex)) return false;
		if (movesToIsland != that.movesToIsland) return false;
		if (!Objects.equals(destinationIslandIndex, that.destinationIslandIndex)) return false;
		if (!Objects.equals(chosenMNBaseSteps, that.chosenMNBaseSteps)) return false;
		if (!Objects.equals(chosenCloudTileIndex, that.chosenCloudTileIndex)) return false;
		if (!Objects.equals(chosenCharacterIndex, that.chosenCharacterIndex)) return false;
		if (!Objects.equals(nickname, that.nickname)) return false;
		if (playerActionType != that.playerActionType) return false;
		if (movedStudent != that.movedStudent) return false;
		return Objects.equals(characterCardParameters, that.characterCardParameters);
	}
	
	/**
	 * An enum containing a list of possible action types
	 */
	public enum ActionType {
		DidPlayAssistantCard,
		DidMoveStudent,
		DidMoveMNBySteps,
		DidChooseCloudIsland,
		DidPurchaseCharacterCard,
		DidPlayCharacterCard;
		
		/**
		 * Finds out whether an action type is considered as valid for a given match phase
		 * @param matchPhase The match phase used to check the action type
		 * @return Whether an action type is considered as valid for a given match phase
		 */
		public boolean isValidForMatchPhase(MatchPhase matchPhase) {
			switch (matchPhase) {
				case PlanPhaseStepOne -> {
					// Performed automagically
					return false;
				}
				case PlanPhaseStepTwo -> {
					return this == DidPlayAssistantCard;
				}
				case ActionPhaseStepOne -> {
					//TODO: Should we restrict character card usage only in this phase?
					return this == DidMoveStudent || this == DidPurchaseCharacterCard || this == DidPlayCharacterCard;
				}
				case ActionPhaseStepTwo -> {
					return this == DidMoveMNBySteps;
				}
				case ActionPhaseStepThree -> {
					return this == DidChooseCloudIsland;
				}
			}
			return false;
		}
	}
}
