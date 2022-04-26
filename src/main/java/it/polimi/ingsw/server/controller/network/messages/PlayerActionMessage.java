package it.polimi.ingsw.server.controller.network.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.model.match.MatchPhase;
import it.polimi.ingsw.server.model.student.Student;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
	
	public PlayerActionMessage(String serializedString) throws MessageDecodeException {
		super(serializedString);
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public ActionType getPlayerActionType() {
		return playerActionType;
	}
	
	public int getAssistantIndex() {
		return assistantIndex;
	}
	
	public Student getMovedStudent() {
		return movedStudent;
	}
	
	public boolean isMovesToIsland() {
		return movesToIsland;
	}
	
	public int getDestinationIslandIndex() {
		return destinationIslandIndex;
	}
	
	public int getChosenMNBaseSteps() {
		return chosenMNBaseSteps;
	}
	
	public int getChosenCloudTileIndex() {
		return chosenCloudTileIndex;
	}
	
	public int getChosenCharacterIndex() {
		return chosenCharacterIndex;
	}
	
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
	
	public enum ActionType {
		DidPlayAssistantCard,
		DidMoveStudent,
		DidMoveMNBySteps,
		DidChooseCloudIsland,
		DidPlayCharacterCard;
		
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
					return this == DidMoveStudent || this == DidPlayCharacterCard;
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
