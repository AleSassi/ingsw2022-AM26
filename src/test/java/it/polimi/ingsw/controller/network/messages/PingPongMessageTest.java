package it.polimi.ingsw.controller.network.messages;

import it.polimi.ingsw.exceptions.model.MessageDecodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PingPongMessageTest {
	
	private PingPongMessage message;
	
	@BeforeEach
	void initMessage() {
		message = new PingPongMessage(true);
	}
	
	@Test
	void testEncode() {
		String encoded = message.serialize();
		String expected = "{\"isPing\":true}";
		assertEquals(expected, encoded);
	}
	
	@Test
	void testDecode() {
		String encoded = "{\"isPing\":true}";
		assertDoesNotThrow(() -> {
			PingPongMessage messageDecoded = new PingPongMessage(encoded);
			assertEquals(message, messageDecoded);
		});
	}
	
	@Test
	void testWrongDecode() {
		String encoded = "{\"wrongKey\":2}";
		assertThrows(MessageDecodeException.class, () -> {
			PingPongMessage messageDecoded = new PingPongMessage(encoded);
		});
	}
	
	@Test
	void testGetters() {
		assertTrue(message.isPing());
	}
}