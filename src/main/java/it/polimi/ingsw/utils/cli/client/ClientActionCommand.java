package it.polimi.ingsw.utils.cli.client;

import it.polimi.ingsw.server.model.match.MatchPhase;
import it.polimi.ingsw.server.model.match.MatchVariant;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;

public enum ClientActionCommand {
	
	ChooseAssistant("pickAssistant", "Picks and plays an Assistant card. Must be followed by the index of the card to play"),
	MoveStudentToIsland("studToIsland", "Picks a Student from your Entrance space and moves it to an Island. Must be followed by (in order) the COLOR of the Student to move and the INDEX of the target island"),
	MoveStudentToRoom("studToTable", "Picks a Student from your Entrance space and moves it to the Dining Room. Must be followed by the COLOR of the Student to move"),
	MoveMotherNature("moveMN", "Moves Mother Nature. Followed by the number of steps"),
	PickCloud("pickCloud", "Picks all Students from a Cloud card. Followed by the Cloud card index"),
	PlayCharacterCard("playChar", "Plays a Character card. Followed by the Index of the Character card to play");
	
	private final String rawValue;
	private final String description;
	
	ClientActionCommand(String rawValue, String description) {
		this.rawValue = rawValue;
		this.description = description;
	}
	
	public String getRawValue() {
		return rawValue;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isValidForPhaseAndVariant(MatchPhase phase, MatchVariant variant) {
		switch (this) {
			case ChooseAssistant -> {
				return phase == MatchPhase.PlanPhaseStepTwo;
			}
			case MoveStudentToIsland, MoveStudentToRoom -> {
				return phase == MatchPhase.ActionPhaseStepOne;
			}
			case MoveMotherNature -> {
				return phase == MatchPhase.ActionPhaseStepTwo;
			}
			case PickCloud -> {
				return phase == MatchPhase.ActionPhaseStepThree;
			}
			case PlayCharacterCard -> {
				return variant == MatchVariant.ExpertRuleSet && (phase == MatchPhase.ActionPhaseStepOne);
			}
		}
		return false;
	}
	
	public void printHelp() {
		System.out.println(StringFormatter.formatWithColor(getRawValue(), ANSIColors.Yellow) + "\t" + StringFormatter.formatWithColor(getDescription(), ANSIColors.Green));
	}
}
