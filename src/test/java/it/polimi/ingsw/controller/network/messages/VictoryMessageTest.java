package it.polimi.ingsw.controller.network.messages;

import it.polimi.ingsw.exceptions.model.MessageDecodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VictoryMessageTest {
	
	private VictoryMessage message;
	
	@BeforeEach
	void initMessage() {
		message = new VictoryMessage(new String[]{"Ale"});
	}
	
	@Test
	void testEncode() {
		String encoded = message.serialize();
		String expected = "{\"winners\":[\"Ale\"]}";
		assertEquals(expected, encoded);
	}
	
	@Test
	void testDecode() {
		String encoded = "{\"winners\":[\"Ale\"]}";
		assertDoesNotThrow(() -> {
			VictoryMessage messageDecoded = new VictoryMessage(encoded);
			assertEquals(message, messageDecoded);
		});
	}
	
	@Test
	void testWrongDecode() {
		String encoded = "{\"wrongKey\":2}";
		assertThrows(MessageDecodeException.class, () -> {
			VictoryMessage messageDecoded = new VictoryMessage(encoded);
		});
	}
	
	@Test
	void testGetters() {
		assertArrayEquals(new String[]{"Ale"}, message.getWinners());
	}
}