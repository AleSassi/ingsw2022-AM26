package it.polimi.ingsw.server.model.assistants;

import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.assistants.PlayedCardDeck;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayedCardDeckTest {
 
	@Test
	void addCardOnTop() {
		AssistantCard test = AssistantCard.EAGLE;
		PlayedCardDeck testDeck = new PlayedCardDeck();
		testDeck.addCardOnTop(test);
		assertEquals(testDeck.topCard(), test);
	}
}