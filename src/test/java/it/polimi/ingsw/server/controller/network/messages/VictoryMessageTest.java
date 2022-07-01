package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.controller.network.messages.VictoryMessage;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to the VictoryMessage class
 * @see VictoryMessage
 */
class VictoryMessageTest {
	
	private VictoryMessage message;
	
	/**
	 * Common test initialization
	 */
	@BeforeEach
	void initMessage() {
		message = new VictoryMessage(new String[]{"Ale"});
	}
	/**
	 * Tests the encoding and decoding process
	 */
	@Test
	void testEncodeDecode() {
		String encoded = message.serialize();
		assertDoesNotThrow(() -> {
			VictoryMessage messageDecoded = new VictoryMessage(encoded);
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
			VictoryMessage messageDecoded = new VictoryMessage(encoded);
		});
	}
	
	/**
	 * Tests all getters
	 */
	@Test
	void testGetters() {
		assertArrayEquals(new String[]{"Ale"}, message.getWinners());
	}
}