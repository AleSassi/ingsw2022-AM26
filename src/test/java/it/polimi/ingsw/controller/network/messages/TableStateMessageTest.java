package it.polimi.ingsw.controller.network.messages;

import it.polimi.ingsw.exceptions.MessageDecodeException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TableManager;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.model.assistants.Wizard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TableStateMessageTest {
	
	private TableManager dummyTable;
	private TableStateMessage message;
	
	@BeforeEach
	void initMessage() {
		assertDoesNotThrow(() -> {
			dummyTable = new TableManager(2, true);
			message = dummyTable.getStateMessage();
		});
	}
	
	//TODO: Find a way to address the randomizer setting random Students to islands
	/*@Test
	void testEncode() {
		String encoded = message.serialize();
		String expected = "{\"availableProfessors\":[\"YellowElf\",\"BlueUnicorn\",\"GreenFrog\",\"RedDragon\",\"PinkFair\"],\"islands\":[{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,0,0,1,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[1,0,0,0,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":true,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,0,0,0,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,0,0,0,1]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,0,0,0,1]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,0,1,0,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,1,0,0,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,1,0,0,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,0,0,0,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[1,0,0,0,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,0,1,0,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,0,0,1,0]}}],\"studentBag\":{\"hostedStudents\":{\"students\":[24,24,24,24,24]}},\"managedClouds\":[{\"hostedStudents\":{\"students\":[0,0,0,0,0]}},{\"hostedStudents\":{\"students\":[0,0,0,0,0]}}],\"playableCharacterCards\":[{\"savedModifier\":0,\"character\":\"Mushroom\",\"priceIncrement\":0,\"timesUsedInCurrentTurn\":0},{\"savedModifier\":0,\"character\":\"Swordsman\",\"priceIncrement\":0,\"timesUsedInCurrentTurn\":0},{\"availableStopCards\":4,\"character\":\"Herbalist\",\"priceIncrement\":0,\"timesUsedInCurrentTurn\":0}]}";
		assertEquals(expected, encoded);
	}
	
	@Test
	void testDecode() {
		String encoded = "{\"availableProfessors\":[\"YellowElf\",\"BlueUnicorn\",\"GreenFrog\",\"RedDragon\",\"PinkFair\"],\"islands\":[{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,0,0,1,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[1,0,0,0,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":true,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,0,0,0,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,0,0,0,1]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,0,0,0,1]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,0,1,0,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,1,0,0,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,1,0,0,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,0,0,0,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[1,0,0,0,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,0,1,0,0]}},{\"towerCount\":0,\"isMotherNaturePresent\":false,\"hasStopCard\":false,\"hostedStudents\":{\"students\":[0,0,0,1,0]}}],\"studentBag\":{\"hostedStudents\":{\"students\":[24,24,24,24,24]}},\"managedClouds\":[{\"hostedStudents\":{\"students\":[0,0,0,0,0]}},{\"hostedStudents\":{\"students\":[0,0,0,0,0]}}],\"playableCharacterCards\":[{\"savedModifier\":0,\"character\":\"Mushroom\",\"priceIncrement\":0,\"timesUsedInCurrentTurn\":0},{\"savedModifier\":0,\"character\":\"Swordsman\",\"priceIncrement\":0,\"timesUsedInCurrentTurn\":0},{\"availableStopCards\":4,\"character\":\"Herbalist\",\"priceIncrement\":0,\"timesUsedInCurrentTurn\":0}]}";
		assertDoesNotThrow(() -> {
			TableStateMessage messageDecoded = new TableStateMessage(encoded);
			assertEquals(message, messageDecoded);
		});
	}*/
	
	@Test
	void testWrongDecode() {
		String encoded = "{\"wrongKey\":2}";
		assertThrows(MessageDecodeException.class, () -> {
			TableStateMessage messageDecoded = new TableStateMessage(encoded);
		});
	}
	
}