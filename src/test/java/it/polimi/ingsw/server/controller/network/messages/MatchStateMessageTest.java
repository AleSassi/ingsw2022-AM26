package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.controller.network.messages.MatchStateMessage;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.model.match.MatchPhase;
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
	void testEncodeDecode() {
		String encoded = message.serialize();
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