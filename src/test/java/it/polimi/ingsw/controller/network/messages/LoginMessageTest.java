package it.polimi.ingsw.controller.network.messages;

import it.polimi.ingsw.exceptions.MessageDecodeException;
import it.polimi.ingsw.model.assistants.Wizard;
import it.polimi.ingsw.model.match.MatchVariant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginMessageTest {
	
	private LoginMessage message;
	
	@BeforeEach
	void initMessage() {
		message = new LoginMessage("Ale", 3, MatchVariant.BasicRuleSet, Wizard.Wizard1);
	}
	
	@Test
	void testEncode() {
		String encoded = message.serialize();
		String expected = "{\"nickname\":\"Ale\",\"desiredNumberOfPlayers\":3,\"matchVariant\":\"BasicRuleSet\"}";
		assertEquals(expected, encoded);
	}
	
	@Test
	void testDecode() {
		String encoded = "{\"nickname\":\"Ale\",\"desiredNumberOfPlayers\":3,\"matchVariant\":\"BasicRuleSet\"}";
		assertDoesNotThrow(() -> {
			LoginMessage messageDecoded = new LoginMessage(encoded);
			assertEquals(message, messageDecoded);
		});
	}
	
	@Test
	void testWrongDecode() {
		String encoded = "{\"wrongKey\":2}";
		assertThrows(MessageDecodeException.class, () -> {
			LoginMessage messageDecoded = new LoginMessage(encoded);
		});
	}
	
	@Test
	void testGetters() {
		assertEquals("Ale", message.getNickname());
		assertEquals(3, message.getDesiredNumberOfPlayers());
		assertEquals(MatchVariant.BasicRuleSet, message.getMatchVariant());
	}
	
}