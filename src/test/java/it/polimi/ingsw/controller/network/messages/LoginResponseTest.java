package it.polimi.ingsw.controller.network.messages;

import it.polimi.ingsw.exceptions.MessageDecodeException;
import it.polimi.ingsw.model.match.MatchVariant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {
	
	private LoginResponse message;
	
	@BeforeEach
	void initMessage() {
		message = new LoginResponse("Ale", true, 2);
	}
	
	@Test
	void testEncode() {
		String encoded = message.serialize();
		String expected = "{\"nickname\":\"Ale\",\"loginAccepted\":true,\"numberOfPlayersRemainingToFillLobby\":2}";
		assertEquals(expected, encoded);
	}
	
	@Test
	void testDecode() {
		String encoded = "{\"nickname\":\"Ale\",\"loginAccepted\":true,\"numberOfPlayersRemainingToFillLobby\":2}";
		assertDoesNotThrow(() -> {
			LoginResponse messageDecoded = new LoginResponse(encoded);
			assertEquals(message, messageDecoded);
		});
	}
	
	@Test
	void testWrongDecode() {
		String encoded = "{\"wrongKey\":2}";
		assertThrows(MessageDecodeException.class, () -> {
			LoginResponse messageDecoded = new LoginResponse(encoded);
		});
	}
	
	@Test
	void testGetters() {
		assertEquals("Ale", message.getNickname());
		assertEquals(2, message.getNumberOfPlayersRemainingToFillLobby());
		assertTrue(message.isLoginAccepted());
	}
	
}