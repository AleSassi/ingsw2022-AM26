package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.server.model.student.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacterCardNetworkParamSetTest {
	
	private CharacterCardNetworkParamSet paramSet;
	
	@BeforeEach
	void setup() {
		paramSet = new CharacterCardNetworkParamSet(Student.BlueUnicorn, Student.RedDragon, true, 2, 10, 8, CharacterCardParamSet.StopCardMovementMode.ToIsland);
	}
	
	@Test
	void getSrcStudentColor() {
		assertEquals(Student.BlueUnicorn, paramSet.getSrcStudentColor());
	}
	
	@Test
	void getDstStudentColor() {
		assertEquals(Student.RedDragon, paramSet.getDstStudentColor());
	}
	
	@Test
	void isStudentDestinationIsSelf() {
		assertTrue(paramSet.isStudentDestinationIsSelf());
	}
	
	@Test
	void getChosenMotherNatureAdditionalSteps() {
		assertEquals(2, paramSet.getChosenMotherNatureAdditionalSteps());
	}
	
	@Test
	void getSourceIslandIndex() {
		assertEquals(10, paramSet.getSourceIslandIndex());
	}
	
	@Test
	void getTargetIslandIndex() {
		assertEquals(8, paramSet.getTargetIslandIndex());
	}
	
	@Test
	void getStopCardMovementMode() {
		assertEquals(CharacterCardParamSet.StopCardMovementMode.ToIsland, paramSet.getStopCardMovementMode());
	}
}