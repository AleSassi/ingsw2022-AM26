package it.polimi.ingsw.server.controller.network.messages;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.SchoolBoard;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.match.MatchPhase;
import it.polimi.ingsw.server.model.match.MatchVariant;
import it.polimi.ingsw.server.model.student.StudentHost;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to the NetworkMessageDecoder class
 * @see NetworkMessageDecoder
 */
class NetworkMessageDecoderTest {
	/**
	 * Tests the decoding of a message
	 */
	@Test
	void decodeMessage() {
		assertDoesNotThrow(() -> {
			//Create a message, then decode & compare
			NetworkMessageDecoder decoder = new NetworkMessageDecoder();
			NetworkMessage message = new ActivePlayerMessage(new Player("Ale", Wizard.Wizard1, Tower.White, 8, 1));
			assertEquals(message, decoder.decodeMessage(message.serialize()));
			message = new LoginMessage("Ale", 2, MatchVariant.BasicRuleSet, Wizard.Wizard1);
			assertEquals(message, decoder.decodeMessage(message.serialize()));
			message = new LoginResponse("Ale", true, 2, "");
			assertEquals(message, decoder.decodeMessage(message.serialize()));
			message = new MatchTerminationMessage("Something something", false);
			assertEquals(message, decoder.decodeMessage(message.serialize()));
			message = new MatchStateMessage(MatchPhase.ActionPhaseStepOne);
			assertEquals(message, decoder.decodeMessage(message.serialize()));
			message = new PingPongMessage(true);
			assertEquals(message, decoder.decodeMessage(message.serialize()));
			message = new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidPlayAssistantCard, 0, null, false, 0, 0, 0, 0, null);
			assertEquals(message, decoder.decodeMessage(message.serialize()));
			message = new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPlayAssistantCard, true, "");
			assertEquals(message, decoder.decodeMessage(message.serialize()));
			Player player = new Player("Ale", Wizard.Wizard1, Tower.Black, 8, 0);
			message = new PlayerStateMessage(player, null, null);
			assertEquals(message, decoder.decodeMessage(message.serialize()));
			message = new TableStateMessage(Arrays.stream(Professor.values()).toList(), new ArrayList<>(), new StudentHost(), new ArrayList<>(), new ArrayList<>());
			assertEquals(message, decoder.decodeMessage(message.serialize()));
			message = new VictoryMessage(new String[]{"Ale"});
			assertEquals(message, decoder.decodeMessage(message.serialize()));
		});
	}
}