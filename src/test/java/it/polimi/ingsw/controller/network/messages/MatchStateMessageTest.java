package it.polimi.ingsw.controller.network.messages;

import it.polimi.ingsw.exceptions.MessageDecodeException;
import it.polimi.ingsw.model.match.MatchPhase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchStateMessageTest {
	
	private MatchStateMessage message;
	
	@BeforeEach
	void initMessage() {
		message = new MatchStateMessage(MatchPhase.ActionPhaseStepOne);
	}
	
	@Test
	void testEncode() {
		String encoded = message.serialize();
		String expected = "{\"currentMatchPhase\":\"ActionPhaseStepOne\"}";
		assertEquals(expected, encoded);
	}
	
	@Test
	void testDecode() {
		String encoded = "{\"currentMatchPhase\":\"ActionPhaseStepOne\"}";
		assertDoesNotThrow(() -> {
			MatchStateMessage messageDecoded = new MatchStateMessage(encoded);
			assertEquals(message, messageDecoded);
		});
	}
	
	@Test
	void testWrongDecode() {
		String encoded = "{\"wrongKey\":2}";
		assertThrows(MessageDecodeException.class, () -> {
			MatchStateMessage messageDecoded = new MatchStateMessage(encoded);
		});
	}
	
	@Test
	void testGetters() {
		assertEquals(MatchPhase.ActionPhaseStepOne, message.getCurrentMatchPhase());
	}
}