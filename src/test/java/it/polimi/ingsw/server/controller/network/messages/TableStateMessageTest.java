package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.model.TableManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TableStateMessageTest {
	
	private TableManager dummyTable;
	private TableStateMessage message;
	
	@BeforeEach
	void initMessage() {
		assertDoesNotThrow(() -> {
			dummyTable = new TableManager(2, true);
			message = dummyTable.getStateMessage();
		});
	}
	
	@Test
	void testEncodeDecode() {
		String encoded = message.serialize();
		assertDoesNotThrow(() -> {
			TableStateMessage messageDecoded = new TableStateMessage(encoded);
			assertEquals(message, messageDecoded);
		});
	}
	
	@Test
	void testWrongDecode() {
		String encoded = "{\"wrongKey\":2}";
		assertThrows(MessageDecodeException.class, () -> {
			TableStateMessage messageDecoded = new TableStateMessage(encoded);
		});
	}
	
}