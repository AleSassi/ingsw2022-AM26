package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.controller.network.messages.ActivePlayerMessage;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.Wizard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to the ActivePlayerMessage
 * @see ActivePlayerMessage
 */
class ActivePlayerMessageTest {
	
	private ActivePlayerMessage message;
	
	/**
	 * Common initialization of the test message
	 */
	@BeforeEach
	void initMessage() {
		assertDoesNotThrow(() -> {
			Player testPlayer = new Player("Ale", Wizard.Wizard1, Tower.White, 8, 1);
			message = new ActivePlayerMessage(testPlayer);
		});
	}

	/**
	 * Tests the encoding and decoding process
	 */
	@Test
	void testEncodeDecode() {
		String encoded = message.serialize();
		assertDoesNotThrow(() -> {
			ActivePlayerMessage messageDecoded = new ActivePlayerMessage(encoded);
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
			ActivePlayerMessage messageDecoded = new ActivePlayerMessage(encoded);
		});
	}
	
	/**
	 * Tests that the active nickname is returned correctly
	 */
	@Test
	void testGetter() {
		assertEquals("Ale", message.getActiveNickname());
	}
	
}