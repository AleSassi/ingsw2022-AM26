package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.controller.network.messages.MatchStateMessage;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.model.match.MatchPhase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to the MatchStateMessage class
 * @see MatchStateMessage
 */
class MatchStateMessageTest {
	
	private MatchStateMessage message;
	
	/**
	 * Common test initialization
	 */
	@BeforeEach
	void initMessage() {
		message = new MatchStateMessage(MatchPhase.ActionPhaseStepOne);
	}
	/**
	 * Tests the encoding and decoding process
	 */
	@Test
	void testEncodeDecode() {
		String encoded = message.serialize();
		assertDoesNotThrow(() -> {
			MatchStateMessage messageDecoded = new MatchStateMessage(encoded);
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
			MatchStateMessage messageDecoded = new MatchStateMessage(encoded);
		});
	}
	
	/**
	 * Test getters
	 */
	@Test
	void testGetters() {
		assertEquals(MatchPhase.ActionPhaseStepOne, message.getCurrentMatchPhase());
	}
}