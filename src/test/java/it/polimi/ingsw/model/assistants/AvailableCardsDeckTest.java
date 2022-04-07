package it.polimi.ingsw.model.assistants;

import it.polimi.ingsw.exceptions.CollectionUnderflowError;
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