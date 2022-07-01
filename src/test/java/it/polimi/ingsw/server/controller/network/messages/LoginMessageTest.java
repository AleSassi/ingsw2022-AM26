package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.controller.network.messages.LoginMessage;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.match.MatchVariant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to the LoginMessage class
 * @see LoginMessage
 */
class LoginMessageTest {
	
	private LoginMessage message;
	
	/**
	 * Common test initialization
	 */
	@BeforeEach
	void initMessage() {
		message = new LoginMessage("Ale", 3, MatchVariant.BasicRuleSet, Wizard.Wizard1);
	}

	/**
	 * Tests the encoding and decoding process
	 */
	@Test
	void testEncodeDecode() {
		String encoded = message.serialize();
		assertDoesNotThrow(() -> {
			LoginMessage messageDecoded = new LoginMessage(encoded);
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
			LoginMessage messageDecoded = new LoginMessage(encoded);
		});
	}
	
	/**
	 * Test getters
	 */
	@Test
	void testGetters() {
		assertEquals("Ale", message.getNickname());
		assertEquals(3, message.getDesiredNumberOfPlayers());
		assertEquals(MatchVariant.BasicRuleSet, message.getMatchVariant());
	}
	
}