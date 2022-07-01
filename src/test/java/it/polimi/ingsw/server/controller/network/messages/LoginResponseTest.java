package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.controller.network.messages.LoginResponse;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to the LoginResponse class
 * @see LoginResponse
 */
class LoginResponseTest {
	
	private LoginResponse message;
	
	@BeforeEach
	void initMessage() {
		message = new LoginResponse("Ale", true, 2, null);
	}
	
	/**
	 * Tests the encoding and decoding process
	 */
	@Test
	void testEncodeDecode() {
		String encoded = message.serialize();
		assertDoesNotThrow(() -> {
			LoginResponse messageDecoded = new LoginResponse(encoded);
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
			LoginResponse messageDecoded = new LoginResponse(encoded);
		});
	}
	
	/**
	 * Test getters
	 */
	@Test
	void testGetters() {
		assertEquals("Ale", message.getNickname());
		assertEquals(2, message.getNumberOfPlayersRemainingToFillLobby());
		assertTrue(message.isLoginAccepted());
	}
	
}