package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.server.model.student.Student;

public class CharacterCardNetworkParamSet {
	
	/**
	 * The student color that needs to be moved, or the student that will be ignored when computing the Influence
	 */
	private final Student srcStudentColor;
	/**
	 * The student color that needs to be swapped with srcStudentColor
	 */
	private final Student dstStudentColor;
	/**
	 * Whether the student destination is the card itself
	 */
	private final boolean studentDestinationIsSelf;
	/**
	 * The number of additional stems MN has to move (set when the player first chooses it). Set it to -1 to signal a nil stepCount
	 */
	private final int chosenMotherNatureAdditionalSteps;
	/**
	 * Used to get the StopCard back. Set it to -1 to signal a nil island
	 */
	private final int sourceIslandIndex;
	/**
	 * Used to set the StopCard on an Island. Set it to -1 to signal a nil island
	 */
	private final int targetIslandIndex;
	/**
	 * The direction the StopCard has to move
	 */
	private final CharacterCardParamSet.StopCardMovementMode stopCardMovementMode;
	
	public CharacterCardNetworkParamSet(Student srcStudentColor, Student dstStudentColor, boolean studentDestinationIsSelf, int chosenMotherNatureAdditionalSteps, int sourceIslandIndex, int targetIslandIndex, CharacterCardParamSet.StopCardMovementMode stopCardMovementMode) {
		this.srcStudentColor = srcStudentColor;
		this.dstStudentColor = dstStudentColor;
		this.studentDestinationIsSelf = studentDestinationIsSelf;
		this.chosenMotherNatureAdditionalSteps = chosenMotherNatureAdditionalSteps;
		this.sourceIslandIndex = sourceIslandIndex;
		this.targetIslandIndex = targetIslandIndex;
		this.stopCardMovementMode = stopCardMovementMode;
	}
	
	public Student getSrcStudentColor() {
		return srcStudentColor;
	}
	
	public Student getDstStudentColor() {
		return dstStudentColor;
	}
	
	public boolean isStudentDestinationIsSelf() {
		return studentDestinationIsSelf;
	}
	
	public int getChosenMotherNatureAdditionalSteps() {
		return chosenMotherNatureAdditionalSteps;
	}
	
	public int getSourceIslandIndex() {
		return sourceIslandIndex;
	}
	
	public int getTargetIslandIndex() {
		return targetIslandIndex;
	}
	
	public CharacterCardParamSet.StopCardMovementMode getStopCardMovementMode() {
		return stopCardMovementMode;
	}
}
