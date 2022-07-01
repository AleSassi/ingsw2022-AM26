package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.server.model.student.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to the Character Card Network Parameters
 * @see CharacterCardNetworkParamSet
 */
class CharacterCardNetworkParamSetTest {
	
	private CharacterCardNetworkParamSet paramSet;
	
	/**
	 * Common test initialization
	 */
	@BeforeEach
	void setup() {
		paramSet = new CharacterCardNetworkParamSet(Student.BlueUnicorn, Student.RedDragon, true, 2, 10, 8, CharacterCardParamSet.StopCardMovementMode.ToIsland);
	}
	
	/**
	 * Tests the getter for the source student color
	 */
	@Test
	void getSrcStudentColor() {
		assertEquals(Student.BlueUnicorn, paramSet.getSrcStudentColor());
	}
	
	/**
	 * Tests the getter for the destination student color
	 */
	@Test
	void getDstStudentColor() {
		assertEquals(Student.RedDragon, paramSet.getDstStudentColor());
	}
	
	/**
	 * Tests the getter for the student destination is self
	 */
	@Test
	void isStudentDestinationIsSelf() {
		assertTrue(paramSet.isStudentDestinationIsSelf());
	}
	
	/**
	 * Tests the getter for the mother nature additional steps
	 */
	@Test
	void getChosenMotherNatureAdditionalSteps() {
		assertEquals(2, paramSet.getChosenMotherNatureAdditionalSteps());
	}
	
	/**
	 * Tests the getter for the source island index
	 */
	@Test
	void getSourceIslandIndex() {
		assertEquals(10, paramSet.getSourceIslandIndex());
	}
	
	/**
	 * Tests the getter for the target island index
	 */
	@Test
	void getTargetIslandIndex() {
		assertEquals(8, paramSet.getTargetIslandIndex());
	}
	
	/**
	 * Tests the getter for the stop card movement mode
	 */
	@Test
	void getStopCardMovementMode() {
		assertEquals(CharacterCardParamSet.StopCardMovementMode.ToIsland, paramSet.getStopCardMovementMode());
	}
}