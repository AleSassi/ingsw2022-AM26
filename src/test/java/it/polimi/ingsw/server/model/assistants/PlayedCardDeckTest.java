package it.polimi.ingsw.server.model.assistants;

import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.assistants.PlayedCardDeck;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test the PlayedCardDeck
 * @see PlayedCardDeck
 */
class PlayedCardDeckTest {
	/**
	 * try to add a test card{@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard} on top of empty deck{@link it.polimi.ingsw.server.model.assistants.PlayedCardDeck PlayedCardDeck} and verify that the card on top is the (@code card) added
	 */
	@Test
	void addCardOnTop() {
		AssistantCard test = AssistantCard.EAGLE;
		PlayedCardDeck testDeck = new PlayedCardDeck();
		testDeck.addCardOnTop(test);
		assertEquals(testDeck.topCard(), test);
	}
	
	/**
	 * try to add the test card{@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard} on top of two empty deck{@link it.polimi.ingsw.server.model.assistants.PlayedCardDeck PlayedCardDeck} and verify the deck are the same
	 */
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