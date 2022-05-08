package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.Wizard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerStateMessageTest {
	
	private Player dummyPlayer;
	private PlayerStateMessage message;
	
	@BeforeEach
	void initMessage() {
		assertDoesNotThrow(() -> {
			dummyPlayer = new Player("Ale", Wizard.Wizard1, Tower.Black, 8);
			message = new PlayerStateMessage(dummyPlayer.getNickname(), null, dummyPlayer.getAvailableAssistantCards(), null, dummyPlayer.getBoard(), dummyPlayer.getAvailableCoins(), dummyPlayer.getWizard());
		});
	}
	
	@Test
	void testEncodeDecode() {
		String encoded = message.serialize();
		assertDoesNotThrow(() -> {
			PlayerStateMessage messageDecoded = new PlayerStateMessage(encoded);
			assertEquals(message, messageDecoded);
		});
	}
	
	@Test
	void testWrongDecode() {
		String encoded = "{\"wrongKey\":2}";
		assertThrows(MessageDecodeException.class, () -> {
			PlayerStateMessage messageDecoded = new PlayerStateMessage(encoded);
		});
	}
	
	@Test
	void testGetters() {
		assertEquals("Ale", message.getNickname());
		assertNull(message.getActiveCharacterCardIdx());
		assertArrayEquals(dummyPlayer.getAvailableAssistantCards().toArray(), message.getAvailableCardsDeck());
		assertNull(message.getLastPlayedAssistantCard());
		assertEquals(dummyPlayer.getBoard(), message.getBoard());
		assertEquals(dummyPlayer.getAvailableCoins(), message.getAvailableCoins());
		assertEquals(dummyPlayer.getWizard(), message.getWizard());
	}
}