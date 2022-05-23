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
	
	@Test
	void testEquals() {
		PlayedCardDeck deckA = new PlayedCardDeck();
		deckA.addCardOnTop(AssistantCard.EAGLE);
		deckA.addCardOnTop(AssistantCard.ELEPHANT);
		PlayedCardDeck deckB = new PlayedCardDeck();
		deckB.addCardOnTop(AssistantCard.EAGLE);
		deckB.addCardOnTop(AssistantCard.ELEPHANT);
		assertEquals(deckA, deckB);
		assertEquals(deckA.hashCode(), deckB.hashCode());
	}
}