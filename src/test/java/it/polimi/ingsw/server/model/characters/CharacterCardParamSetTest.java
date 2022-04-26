package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentHost;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class CharacterCardParamSetTest tests CharacterCardParamSet.
 * Coverage 100%
 *
 * @author Alessandro Sassi
 * @see CharacterCardParamSet
 */
class CharacterCardParamSetTest {

    /**
     * Tests that all Getters (immutable class) report the correct value
     */
    @Test
    void testGetters() {
        Random randomizer = new Random();
        int randomStudColor = randomizer.nextInt(0, Student.values().length);
        Student randomStudent = Student.values()[randomStudColor];
        int randomStudColorDst = randomizer.nextInt(0, Student.values().length);
        Student randomStudentDst = Student.values()[randomStudColorDst];
        StudentHost srcStudentHost = new StudentHost();
        StudentHost dstStudentHost = new StudentHost();
        boolean studDestIsSelf = false;
        int MNAdditionalSteps = randomizer.nextInt(0, 3);
        int srcIslandIdx = randomizer.nextInt(0, 12);
        int dstIslandIdx = randomizer.nextInt(0, 12);
        CharacterCardParamSet.StopCardMovementMode movementMode = CharacterCardParamSet.StopCardMovementMode.ToCard;

        CharacterCardParamSet testParamSet = new CharacterCardParamSet(randomStudent, randomStudentDst, srcStudentHost, dstStudentHost, studDestIsSelf, MNAdditionalSteps, srcIslandIdx, dstIslandIdx, movementMode);
        assertEquals(randomStudent, testParamSet.getSrcStudentColor());
        assertEquals(randomStudentDst, testParamSet.getDstStudentColor());
        assertEquals(srcStudentHost, testParamSet.getSourceStudentHost());
        assertEquals(dstStudentHost, testParamSet.getDestinationStudentHost());
        assertEquals(studDestIsSelf, testParamSet.isStudentDestinationIsSelf());
        assertEquals(MNAdditionalSteps, testParamSet.getChosenMotherNatureAdditionalSteps());
        assertEquals(srcIslandIdx, testParamSet.getSourceIslandIndex());
        assertEquals(dstIslandIdx, testParamSet.getTargetIslandIndex());
        assertEquals(movementMode, testParamSet.getStopCardMovementMode());
    }
}