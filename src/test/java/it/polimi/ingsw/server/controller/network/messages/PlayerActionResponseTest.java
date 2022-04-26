package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.controller.network.messages.PlayerActionMessage;
import it.polimi.ingsw.server.controller.network.messages.PlayerActionResponse;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerActionResponseTest {
	
	private PlayerActionResponse message;
	
	@BeforeEach
	void initMessage() {
		message = new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPlayAssistantCard, false, "A random error message");
	}
	
	@Test
	void testEncode() {
		String encoded = message.serialize();
		String expected = "{\"nickname\":\"Ale\",\"actionType\":\"DidPlayAssistantCard\",\"actionSuccess\":false,\"descriptiveErrorMessage\":\"A random error message\"}";
		assertEquals(expected, encoded);
	}
	
	@Test
	void testDecode() {
		String encoded = "{\"nickname\":\"Ale\",\"actionType\":\"DidPlayAssistantCard\",\"actionSuccess\":false,\"descriptiveErrorMessage\":\"A random error message\"}";
		assertDoesNotThrow(() -> {
			PlayerActionResponse messageDecoded = new PlayerActionResponse(encoded);
			assertEquals(message, messageDecoded);
		});
	}
	
	@Test
	void testWrongDecode() {
		String encoded = "{\"wrongKey\":2}";
		assertThrows(MessageDecodeException.class, () -> {
			PlayerActionResponse messageDecoded = new PlayerActionResponse(encoded);
		});
	}
	
	@Test
	void testGetters() {
		assertEquals("Ale", message.getNickname());
		assertEquals(PlayerActionMessage.ActionType.DidPlayAssistantCard, message.getActionType());
		assertFalse(message.isActionSuccess());
		assertEquals("A random error message", message.getDescriptiveErrorMessage());
	}
	
}