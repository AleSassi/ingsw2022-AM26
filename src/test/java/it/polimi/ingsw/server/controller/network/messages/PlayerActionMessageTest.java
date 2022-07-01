package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.controller.network.messages.PlayerActionMessage;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to the PlayerActionMessage class
 * @see PlayerActionMessage
 */
class PlayerActionMessageTest {
	
	private PlayerActionMessage message;
	
	/**
	 * Common test initialization
	 */
	@BeforeEach
	void initMessage() {
		message = new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidPlayAssistantCard, 0, null, false, 0, 0, 0, 0, null);
	}
	/**
	 * Tests the encoding and decoding process
	 */
	@Test
	void testEncodeDecode() {
		String encoded = message.serialize();
		assertDoesNotThrow(() -> {
			PlayerActionMessage messageDecoded = new PlayerActionMessage(encoded);
			assertEquals(message, messageDecoded);
		});
	}
	/**
	 * Tests the case of a wrong decode
	 */
	@Test
	void testWrongDecode() {
		String encoded = "{\"wrongKey\":2}";
		assertThrows(MessageDecodeException.class, () -> {
			PlayerActionMessage messageDecoded = new PlayerActionMessage(encoded);
		});
	}
	
	/**
	 * Tests all getters
	 */
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