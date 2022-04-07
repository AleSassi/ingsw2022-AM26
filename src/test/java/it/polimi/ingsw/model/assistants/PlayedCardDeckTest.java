package it.polimi.ingsw.model.assistants;

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