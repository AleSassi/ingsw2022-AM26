package it.polimi.ingsw.server.model.assistants;

import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.assistants.AvailableCardsDeck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AvailableCardsDeckTest {
    
    AvailableCardsDeck deck;
    
    @BeforeEach
    void initDeck() {
        deck = new AvailableCardsDeck();
    }
	
	@Test
	void removeCard() {
		int initialSize = deck.getCount();
		AssistantCard card = deck.getCard(0);
		assertDoesNotThrow(() -> deck.removeCard(0));
		assertEquals(initialSize - 1, deck.getCount());
		assertNotEquals(card, deck.getCard(0));
	}
    
    @Test
    void removeWithUnderflow() {
        int initialDeckSize = deck.getCount();
        for (int i = 0; i < initialDeckSize; i++) {
            assertDoesNotThrow(() -> deck.removeCard(0));
        }
        assertThrows(CollectionUnderflowError.class, () -> deck.removeCard(0));
    }
}