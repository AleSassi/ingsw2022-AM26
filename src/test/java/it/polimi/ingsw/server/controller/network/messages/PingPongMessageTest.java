package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.controller.network.messages.PingPongMessage;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to the PingPongMessage class
 * @see PingPongMessage
 */
class PingPongMessageTest {
	
	private PingPongMessage message;
	
	/**
	 * Common test initialization
	 */
	@BeforeEach
	void initMessage() {
		message = new PingPongMessage(true);
	}
	/**
	 * Tests the encoding and decoding process
	 */
	@Test
	void testEncodeDecode() {
		String encoded = message.serialize();
		assertDoesNotThrow(() -> {
			PingPongMessage messageDecoded = new PingPongMessage(encoded);
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
			PingPongMessage messageDecoded = new PingPongMessage(encoded);
		});
	}
	
	/**
	 * Test getters
	 */
	@Test
	void testGetters() {
		assertTrue(message.isPing());
	}
}