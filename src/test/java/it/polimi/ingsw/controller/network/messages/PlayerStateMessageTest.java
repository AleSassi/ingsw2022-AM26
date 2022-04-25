package it.polimi.ingsw.controller.network.messages;

import it.polimi.ingsw.exceptions.MessageDecodeException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.model.assistants.Wizard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PlayerStateMessageTest {
	
	private Player dummyPlayer;
	private PlayerStateMessage message;
	
	@BeforeEach
	void initMessage() {
		assertDoesNotThrow(() -> {
			dummyPlayer = new Player("Ale", Wizard.Wizard1, Tower.Black, 8);
			message = new PlayerStateMessage(dummyPlayer.getNickname(), null, dummyPlayer.getAvailableAssistantCards(), null, dummyPlayer.getBoard(), dummyPlayer.getAvailableCoins(), dummyPlayer.getWizard());
		});
	}
	
	@Test
	void testEncode() {
		String encoded = message.serialize();
		String expected = "{\"nickname\":\"Ale\",\"availableCardsDeck\":[\"TURTLE\",\"ELEPHANT\",\"DOG\",\"OCTOPUS\",\"SNAKE\",\"FOX\",\"EAGLE\",\"CAT\",\"PEAFOWL\",\"LION\"],\"board\":{\"maxTowerCount\":8,\"availableTowerCount\":8,\"towerType\":\"Black\",\"diningRoom\":{\"hostedStudents\":{\"students\":[0,0,0,0,0]}},\"entrance\":{\"hostedStudents\":{\"students\":[0,0,0,0,0]}},\"controlledProfessors\":[false,false,false,false,false]},\"availableCoins\":0,\"wizard\":\"Wizard1\"}";
		assertEquals(expected, encoded);
	}
	
	@Test
	void testDecode() {
		String encoded = "{\"nickname\":\"Ale\",\"availableCardsDeck\":[\"TURTLE\",\"ELEPHANT\",\"DOG\",\"OCTOPUS\",\"SNAKE\",\"FOX\",\"EAGLE\",\"CAT\",\"PEAFOWL\",\"LION\"],\"board\":{\"maxTowerCount\":8,\"availableTowerCount\":8,\"towerType\":\"Black\",\"diningRoom\":{\"hostedStudents\":{\"students\":[0,0,0,0,0]}},\"entrance\":{\"hostedStudents\":{\"students\":[0,0,0,0,0]}},\"controlledProfessors\":[false,false,false,false,false]},\"availableCoins\":0,\"wizard\":\"Wizard1\"}";
		assertDoesNotThrow(() -> {
			PlayerStateMessage messageDecoded = new PlayerStateMessage(encoded);
			assertEquals(message, messageDecoded);
		});
	}
	
	@Test
	void testWrongDecode() {
		String encoded = "{\"wrongKey\":2}";
		assertThrows(MessageDecodeException.class, () -> {
			PlayerStateMessage messageDecoded = new PlayerStateMessage(encoded);
		});
	}
	
	@Test
	void testGetters() {
		assertEquals("Ale", message.getNickname());
		assertNull(message.getActiveCharacterCardIdx());
		assertArrayEquals(dummyPlayer.getAvailableAssistantCards().toArray(), message.getAvailableCardsDeck());
		assertNull(message.getLastPlayedAssistantCard());
		assertEquals(dummyPlayer.getBoard(), message.getBoard());
		assertEquals(dummyPlayer.getAvailableCoins(), message.getAvailableCoins());
		assertEquals(dummyPlayer.getWizard(), message.getWizard());
	}
}