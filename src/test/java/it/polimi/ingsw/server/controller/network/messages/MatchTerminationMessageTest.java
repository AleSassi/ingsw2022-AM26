package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.controller.network.messages.MatchTerminationMessage;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to the MatchTerminationMessage class
 * @see MatchTerminationMessage
 */
class MatchTerminationMessageTest {
	
	private MatchTerminationMessage message;
	
	/**
	 * Common test initialization
	 */
	@BeforeEach
	void initMessage() {
		message = new MatchTerminationMessage("A random termination reason, visible to the user", false);
	}
	/**
	 * Tests the encoding and decoding process
	 */
	@Test
	void testEncodeDecode() {
		String encoded = message.serialize();
		assertDoesNotThrow(() -> {
			MatchTerminationMessage messageDecoded = new MatchTerminationMessage(encoded);
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
			MatchTerminationMessage messageDecoded = new MatchTerminationMessage(encoded);
		});
	}
	
	/**
	 * Test all getters
	 */
	@Test
	void testGetters() {
		assertEquals("A random termination reason, visible to the user", message.getTerminationReason());
	}
}