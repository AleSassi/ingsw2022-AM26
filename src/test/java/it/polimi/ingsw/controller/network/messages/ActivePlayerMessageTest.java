package it.polimi.ingsw.controller.network.messages;

import it.polimi.ingsw.exceptions.MessageDecodeException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.model.assistants.Wizard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActivePlayerMessageTest {
	
	private ActivePlayerMessage message;
	
	@BeforeEach
	void initMessage() {
		assertDoesNotThrow(() -> {
			Player testPlayer = new Player("Ale", Wizard.Wizard1, Tower.White, 8);
			message = new ActivePlayerMessage(testPlayer);
		});
	}
	
	@Test
	void testEncode() {
		String encoded = message.serialize();
		String expected = "{\"activeNickname\":\"Ale\"}";
		assertEquals(expected, encoded);
	}
	
	@Test
	void testDecode() {
		String encoded = "{\"activeNickname\":\"Ale\"}";
		assertDoesNotThrow(() -> {
			ActivePlayerMessage messageDecoded = new ActivePlayerMessage(encoded);
			assertEquals(message, messageDecoded);
		});
	}
	
	@Test
	void testWrongDecode() {
		String encoded = "{\"wrongKey\":2}";
		assertThrows(MessageDecodeException.class, () -> {
			ActivePlayerMessage messageDecoded = new ActivePlayerMessage(encoded);
		});
	}
	
	@Test
	void testGetter() {
		assertEquals("Ale", message.getActiveNickname());
	}
	
}