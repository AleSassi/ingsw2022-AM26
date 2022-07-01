package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.server.model.student.Student;

/**
 * Class {@code CharacterCardNetworkParamSet} represent the set of Character Card parameters which will be sent over the network
 */
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
	
	/**
	 * Creates a parameter set
	 * @param srcStudentColor The student color that needs to be moved, or the student that will be ignored when computing the Influence
	 * @param dstStudentColor The student color that needs to be swapped with srcStudentColor
	 * @param studentDestinationIsSelf Whether the student destination is the card itself
	 * @param chosenMotherNatureAdditionalSteps The number of additional stems MN has to move (set when the player first chooses it). Set it to -1 to signal a nil stepCount
	 * @param sourceIslandIndex Used to get the StopCard back. Set it to -1 to signal a nil island
	 * @param targetIslandIndex Used to set the StopCard on an Island. Set it to -1 to signal a nil island
	 * @param stopCardMovementMode The direction the StopCard has to move
	 */
	public CharacterCardNetworkParamSet(Student srcStudentColor, Student dstStudentColor, boolean studentDestinationIsSelf, int chosenMotherNatureAdditionalSteps, int sourceIslandIndex, int targetIslandIndex, CharacterCardParamSet.StopCardMovementMode stopCardMovementMode) {
		this.srcStudentColor = srcStudentColor;
		this.dstStudentColor = dstStudentColor;
		this.studentDestinationIsSelf = studentDestinationIsSelf;
		this.chosenMotherNatureAdditionalSteps = chosenMotherNatureAdditionalSteps;
		this.sourceIslandIndex = sourceIslandIndex;
		this.targetIslandIndex = targetIslandIndex;
		this.stopCardMovementMode = stopCardMovementMode;
	}
	
	/**
	 * Finds the source student color
	 * @return The source student color
	 */
	public Student getSrcStudentColor() {
		return srcStudentColor;
	}
	
	/**
	 * Finds the destination student color
	 * @return The destination student color
	 */
	public Student getDstStudentColor() {
		return dstStudentColor;
	}
	
	/**
	 * Finds whether the student destination is the card itself
	 * @return Whether the student destination is the card itself
	 */
	public boolean isStudentDestinationIsSelf() {
		return studentDestinationIsSelf;
	}
	
	/**
	 * Finds the number of additional stems MN has to move
	 * @return The number of additional stems MN has to move
	 */
	public int getChosenMotherNatureAdditionalSteps() {
		return chosenMotherNatureAdditionalSteps;
	}
	
	/**
	 * Finds the source island index
	 * @return The source island index
	 */
	public int getSourceIslandIndex() {
		return sourceIslandIndex;
	}
	
	/**
	 * Finds the target island index
	 * @return The target island index
	 */
	public int getTargetIslandIndex() {
		return targetIslandIndex;
	}
	
	/**
	 * Finds the StopCard movement mode
	 * @return The StopCard movement mode
	 */
	public CharacterCardParamSet.StopCardMovementMode getStopCardMovementMode() {
		return stopCardMovementMode;
	}
}
