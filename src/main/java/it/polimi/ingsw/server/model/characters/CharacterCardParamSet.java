package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.controller.network.messages.CharacterCardNetworkParamSet;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentHost;
/**
 * This Class represent the {@code CharacterCardParamSet}
 * @author Alessandro Sassi
 */
/**
 * An object that encapsulates all optional parameters required by CharacterCard to perform its action
 *
 * This class lets <code>CharacterCard</code>'s <code>useCard</code> method know what the user has selected (e.g.: the Student color that has to be moved, the additional Mother Nature steps) in order to perform the card action.
 * Although it is recommended to initialize every field of the set, most of the cards do not need every parameter to perform their work. Therefore, the caller can initialize only the parameters required by the invoked card.
 * This set is an abstraction over a more general <code>HashMap<String, Object></code>, specializing its limiting it to the necessary values.
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
     * constructor, set the parameter
     * @param srcStudentColor  (type Student) {@link it.polimi.ingsw.server.model.student.Student Student} the source color student where the movement start
     * @param dstStudentColor  (type Student) {@code student} the destionation student color where the movement end
     * @param sourceStudentHost  (type host){@link it.polimi.ingsw.server.model.student.StudentHost host}  the source destination where the movement start
     * @param destinationStudentHost  (type host) the end destination where the movement end
     * @param studentDestinationIsSelf (type boolean) true if source and the end destination are the same
     * @param chosenMotherNatureAdditionalSteps    (type int) number of more step MN can do
     * @param sourceIslandIndex  (type int) {@link it.polimi.ingsw.server.model.student.Island Island's} idx of island source
     * @param targetIslandIndex (type int) {@code island} idx of island target
     * @param stopCardMovementMode  (type StopCardMovementMode) type of movement
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
     * getter
     * @return ((type Student) {@link it.polimi.ingsw.server.model.student.Student Student} the source color student where the switch start
      */
    protected Student getSrcStudentColor() {
        return srcStudentColor;
    }
    /**
     * getter
     * @return ((type Student) {@link it.polimi.ingsw.server.model.student.Student Student} the destionation color student where the switch start
     */
    protected Student getDstStudentColor() {
        return dstStudentColor;
    }
    /**
     * getter
     * (type host){@link it.polimi.ingsw.server.model.student.StudentHost host}  the source destination where the switch start
     */
    protected StudentHost getSourceStudentHost() {
        return sourceStudentHost;
    }
    /**
     * getter
     * (type host){@link it.polimi.ingsw.server.model.student.StudentHost host}  the end destination where the switch end
     */
    protected StudentHost getDestinationStudentHost() {
        return destinationStudentHost;
    }
    /**
     * getter
     * (type boolean) true if source and the end destination are the same
     */
    protected boolean isStudentDestinationIsSelf() {
        return studentDestinationIsSelf;
    }
    /**
     * getter
     * (type host)(type int) number of more step MN can do
     */
    protected int getChosenMotherNatureAdditionalSteps() {
        return chosenMotherNatureAdditionalSteps;
    }
    /**
     * getter
     * (type int) {@link it.polimi.ingsw.server.model.student.Island Island's} idx of island source
     */
    protected int getSourceIslandIndex() {
        return sourceIslandIndex;
    }
    /**
     * getter
     * (type int) {@link it.polimi.ingsw.server.model.student.Island Island's} idx of island destination
     */
    protected int getTargetIslandIndex() {
        return targetIslandIndex;
    }
    /**
     * getter
     * (type StopCardMovementMode) type of stop card movement
     * */
    public StopCardMovementMode getStopCardMovementMode() {
        return stopCardMovementMode;
    }

    /**
     * This Class represent the {@code StopCardMovementMode}
     */
    public enum StopCardMovementMode {
        ToCard,
        ToIsland
    }
}
