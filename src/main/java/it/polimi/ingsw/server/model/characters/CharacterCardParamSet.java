package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.controller.network.messages.CharacterCardNetworkParamSet;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentHost;
/**
 * An object that encapsulates all optional parameters required by CharacterCard to perform its action
 *
 * This class lets <code>CharacterCard</code>'s <code>useCard</code> method know what the user has selected (e.g.: the Student color that has to be moved, the additional Mother Nature steps) in order to perform the card action.
 * Although it is recommended to initialize every field of the set, most of the cards do not need every parameter to perform their work. Therefore, the caller can initialize only the parameters required by the invoked card.
 * This set is an abstraction over a more general <code>HashMap String, Object</code>, specializing its limiting it to the necessary values.
 */
public class CharacterCardParamSet {
    /**
     * The student color that needs to be moved, or the student that will be ignored when computing the Influence
     */
    private final Student srcStudentColor;
    /**
     * The student color that needs to be swapped with srcStudentColor
     */
    private final Student dstStudentColor;
    /**
     * Used (if not null) as a source for student movements
     */
    private final StudentHost sourceStudentHost;
    /**
     * Used (if not null) as a destination for student movements
     */
    private final StudentHost destinationStudentHost;
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
    private final StopCardMovementMode stopCardMovementMode;

    /**
     * Constructor, intializes the parameter set with the correct values
     * @param srcStudentColor  (type {@link it.polimi.ingsw.server.model.student.Student Student}) The student color that needs to be moved, or the student that will be ignored when computing the Influence
     * @param dstStudentColor  (type Student) The student color that needs to be swapped with srcStudentColor
     * @param sourceStudentHost  (type StudentHost) the source for student movements
     * @param destinationStudentHost  (type StudentHost) the destination for student movements
     * @param studentDestinationIsSelf (type boolean) Whether the student destination is the card itself
     * @param chosenMotherNatureAdditionalSteps    (type int) The number of additional stems MN has to move (set when the player first chooses it). Set it to -1 to signal a nil stepCount
     * @param sourceIslandIndex  (type int) The index of the Island where we want to get the StopCard back. Set it to -1 to signal a nil island
     * @param targetIslandIndex (type int) The index of the Island where we want to set the StopCard. Set it to -1 to signal a nil island
     * @param stopCardMovementMode  (type StopCardMovementMode) The direction the StopCard has to move
     */
    public CharacterCardParamSet(Student srcStudentColor, Student dstStudentColor, StudentHost sourceStudentHost, StudentHost destinationStudentHost, boolean studentDestinationIsSelf, int chosenMotherNatureAdditionalSteps, int sourceIslandIndex, int targetIslandIndex, StopCardMovementMode stopCardMovementMode) {
        this.srcStudentColor = srcStudentColor;
        this.dstStudentColor = dstStudentColor;
        this.sourceStudentHost = sourceStudentHost;
        this.destinationStudentHost = destinationStudentHost;
        this.studentDestinationIsSelf = studentDestinationIsSelf;
        this.chosenMotherNatureAdditionalSteps = chosenMotherNatureAdditionalSteps;
        this.sourceIslandIndex = sourceIslandIndex;
        this.targetIslandIndex = targetIslandIndex;
        this.stopCardMovementMode = stopCardMovementMode;
    }
    
    /**
     * Gets the student color that needs to be moved, or the student that will be ignored when computing the Influence
     * @return The student color that needs to be moved, or the student that will be ignored when computing the Influence
      */
    protected Student getSrcStudentColor() {
        return srcStudentColor;
    }
    
    /**
     * Gets the student color that needs to be swapped with srcStudentColor
     * @return The student color that needs to be swapped with srcStudentColor
     */
    protected Student getDstStudentColor() {
        return dstStudentColor;
    }
    
    /**
     * Gets the source for student movements
     * @return The source for student movements
     */
    protected StudentHost getSourceStudentHost() {
        return sourceStudentHost;
    }
    
    /**
     * Gets the destination for student movements
     * @return the destination for student movements
     */
    protected StudentHost getDestinationStudentHost() {
        return destinationStudentHost;
    }
    
    /**
     * Gets whether the student destination is the card itself
     * @return Whether the student destination is the card itself
     */
    protected boolean isStudentDestinationIsSelf() {
        return studentDestinationIsSelf;
    }
    
    /**
     * Gets the number of additional stems MN has to move (set when the player first chooses it).
     * @return The number of additional stems MN has to move (set when the player first chooses it).
     */
    protected int getChosenMotherNatureAdditionalSteps() {
        return chosenMotherNatureAdditionalSteps;
    }
    
    /**
     * Gets the index of the Island where we want to get the StopCard back.
     * @return The index of the Island where we want to get the StopCard back.
     */
    protected int getSourceIslandIndex() {
        return sourceIslandIndex;
    }
    
    /**
     * Gets the index of the Island where we want to set the StopCard
     * @return The index of the Island where we want to set the StopCard
     */
    protected int getTargetIslandIndex() {
        return targetIslandIndex;
    }
    
    /**
     * Gets the direction the StopCard has to move
     * @return The direction the StopCard has to move
     * */
    public StopCardMovementMode getStopCardMovementMode() {
        return stopCardMovementMode;
    }

    /**
     * An enum with the possible Stop Card movements, either to the Card or to an Island
     */
    public enum StopCardMovementMode {
        ToCard,
        ToIsland
    }
}
