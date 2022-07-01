package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.controller.network.messages.PlayerActionMessage;
import it.polimi.ingsw.server.controller.network.messages.PlayerActionResponse;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to the PlayerActionResponse class
 * @see PlayerActionResponse
 */
class PlayerActionResponseTest {
	
	private PlayerActionResponse message;
	
	/**
	 * Common test initialization
	 */
	@BeforeEach
	void initMessage() {
		message = new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPlayAssistantCard, false, "A random error message");
	}
	/**
	 * Tests the encoding and decoding process
	 */
	@Test
	void testEncodeDecode() {
		String encoded = message.serialize();
		assertDoesNotThrow(() -> {
			PlayerActionResponse messageDecoded = new PlayerActionResponse(encoded);
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
			PlayerActionResponse messageDecoded = new PlayerActionResponse(encoded);
		});
	}
	
	/**
	 * Tests all getters
	 */
	@Test
	void testGetters() {
		assertEquals("Ale", message.getNickname());
		assertEquals(PlayerActionMessage.ActionType.DidPlayAssistantCard, message.getActionType());
		assertFalse(message.isActionSuccess());
		assertEquals("A random error message", message.getDescriptiveErrorMessage());
	}
	
}