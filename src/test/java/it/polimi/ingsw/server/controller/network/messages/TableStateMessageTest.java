package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;
import it.polimi.ingsw.server.exceptions.model.MessageDecodeException;
import it.polimi.ingsw.server.model.TableManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to the TableStateMessage class
 * @see TableStateMessage
 */
class TableStateMessageTest {
	
	private TableManager dummyTable;
	private TableStateMessage message;
	
	/**
	 * Common test initialization
	 */
	@BeforeEach
	void initMessage() {
		assertDoesNotThrow(() -> {
			dummyTable = new TableManager(2, true);
			message = dummyTable.getStateMessage();
		});
	}
	/**
	 * Tests the encoding and decoding process
	 */
	@Test
	void testEncodeDecode() {
		String encoded = message.serialize();
		assertDoesNotThrow(() -> {
			TableStateMessage messageDecoded = new TableStateMessage(encoded);
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
			TableStateMessage messageDecoded = new TableStateMessage(encoded);
		});
	}
	
}