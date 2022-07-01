package it.polimi.ingsw.utils.cli.client;

import it.polimi.ingsw.server.model.match.MatchPhase;
import it.polimi.ingsw.server.model.match.MatchVariant;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;

/**
 * The enum holds a list of constants representing possible CLI commands
 */
public enum ClientActionCommand {
	
	ChooseAssistant("pickAssistant", "Picks and plays an Assistant card. Must be followed by the index of the card to play"),
	MoveStudentToIsland("studToIsland", "Picks a Student from your Entrance space and moves it to an Island. Must be followed by (in order) the COLOR of the Student to move and the INDEX of the target island"),
	MoveStudentToRoom("studToTable", "Picks a Student from your Entrance space and moves it to the Dining Room. Must be followed by the COLOR of the Student to move"),
	MoveMotherNature("moveMN", "Moves Mother Nature. Followed by the number of steps"),
	PickCloud("pickCloud", "Picks all Students from a Cloud card. Followed by the Cloud card index"),
	PurchaseCharacterCard("purchaseCard", "Purchases a Character card. Followed by the Index of the Character card to purchase"),
	PlayCharacterCard("playChar", "Plays the purchased character card");
	
	private final String rawValue;
	private final String description;
	
	/**
	 * Creates a constant for a CLI command
	 * @param rawValue The command ID
	 * @param description The command description
	 */
	ClientActionCommand(String rawValue, String description) {
		this.rawValue = rawValue;
		this.description = description;
	}
	
	/**
	 * Gets the command ID
	 * @return The command ID
	 */
	public String getRawValue() {
		return rawValue;
	}
	
	/**
	 * Gets the command description
	 * @return The command description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Checks if the command is valid for the current match phase and variant
	 * @param phase The match phase
	 * @param variant The match variant
	 * @return Whether the command is valid for the current match phase and variant
	 */
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
			case PlayCharacterCard, PurchaseCharacterCard -> {
				return variant == MatchVariant.ExpertRuleSet && (phase == MatchPhase.ActionPhaseStepOne);
			}
		}
		return false;
	}
	
	/**
	 * Prints the command
	 * @param usingPrint Whether it should use print or println
	 */
	public void printHelp(boolean usingPrint) {
		String helpStr = StringFormatter.formatWithColor(getRawValue(), ANSIColors.Yellow) + "\t" + StringFormatter.formatWithColor(getDescription(), ANSIColors.Green);
		if (usingPrint) {
			System.out.print(helpStr);
		} else {
			System.out.println(helpStr);
		}
	}
}
