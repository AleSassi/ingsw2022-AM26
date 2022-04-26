package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.controller.network.messages.PlayerActionMessage;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerActionMessageTest {
	
	private PlayerActionMessage message;
	
	@BeforeEach
	void initMessage() {
		message = new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidPlayAssistantCard, 0, null, false, 0, 0, 0, 0, null);
	}
	
	@Test
	void testEncode() {
		String encoded = message.serialize();
		String expected = "{\"nickname\":\"Ale\",\"playerActionType\":\"DidPlayAssistantCard\",\"assistantIndex\":0,\"movesToIsland\":false,\"destinationIslandIndex\":0,\"chosenMNBaseSteps\":0,\"chosenCloudTileIndex\":0,\"chosenCharacterIndex\":0}";
		assertEquals(expected, encoded);
	}
	
	@Test
	void testDecode() {
		String encoded = "{\"nickname\":\"Ale\",\"playerActionType\":\"DidPlayAssistantCard\",\"assistantIndex\":0,\"movesToIsland\":false,\"destinationIslandIndex\":0,\"chosenMNBaseSteps\":0,\"chosenCloudTileIndex\":0,\"chosenCharacterIndex\":0}";
		assertDoesNotThrow(() -> {
			PlayerActionMessage messageDecoded = new PlayerActionMessage(encoded);
			assertEquals(message, messageDecoded);
		});
	}
	
	@Test
	void testWrongDecode() {
		String encoded = "{\"wrongKey\":2}";
		assertThrows(MessageDecodeException.class, () -> {
			PlayerActionMessage messageDecoded = new PlayerActionMessage(encoded);
		});
	}
	
	@Test
	void testGetters() {
		assertEquals("Ale", message.getNickname());
		assertEquals(PlayerActionMessage.ActionType.DidPlayAssistantCard, message.getPlayerActionType());
		assertEquals(0, message.getAssistantIndex());
		assertNull(message.getMovedStudent());
		assertFalse(message.isMovesToIsland());
		assertEquals(0, message.getDestinationIslandIndex());
		assertEquals(0, message.getChosenMNBaseSteps());
		assertEquals(0, message.getChosenCloudTileIndex());
		assertEquals(0, message.getChosenCharacterIndex());
		assertNull(message.getCharacterCardParameters());
	}
	
}