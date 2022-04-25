package it.polimi.ingsw.controller.network.messages;

import it.polimi.ingsw.exceptions.model.MessageDecodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchTerminationMessageTest {
	
	private MatchTerminationMessage message;
	
	@BeforeEach
	void initMessage() {
		message = new MatchTerminationMessage("A random termination reason, visible to the user", false);
	}
	
	@Test
	void testEncode() {
		String encoded = message.serialize();
		String expected = "{\"terminationReason\":\"A random termination reason, visible to the user\"}";
		assertEquals(expected, encoded);
	}
	
	@Test
	void testDecode() {
		String encoded = "{\"terminationReason\":\"A random termination reason, visible to the user\"}";
		assertDoesNotThrow(() -> {
			MatchTerminationMessage messageDecoded = new MatchTerminationMessage(encoded);
			assertEquals(message, messageDecoded);
		});
	}
	
	@Test
	void testWrongDecode() {
		String encoded = "{\"wrongKey\":2}";
		assertThrows(MessageDecodeException.class, () -> {
			MatchTerminationMessage messageDecoded = new MatchTerminationMessage(encoded);
		});
	}
	
	@Test
	void testGetters() {
		assertEquals("A random termination reason, visible to the user", message.getTerminationReason());
	}
}