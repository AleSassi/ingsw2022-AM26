package it.polimi.ingsw.server.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfessorTest {
	
	@Test
	void testGetRawValue() {
		assertEquals(0, Professor.getRawValueOf(Professor.YellowElf));
		assertEquals(1, Professor.getRawValueOf(Professor.BlueUnicorn));
		assertEquals(2, Professor.getRawValueOf(Professor.GreenFrog));
		assertEquals(3, Professor.getRawValueOf(Professor.RedDragon));
		assertEquals(4, Professor.getRawValueOf(Professor.PinkFair));
	}
	
	@Test
	void testGetProgessorColor() {
	
	}
	
}