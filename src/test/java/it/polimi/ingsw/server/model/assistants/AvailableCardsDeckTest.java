package it.polimi.ingsw.server.model.assistants;

import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.assistants.AvailableCardsDeck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the available cards deck
 * @see AvailableCardsDeck
 */
class AvailableCardsDeckTest {
    
    AvailableCardsDeck deck;
	
	/**
	 * construct the deck for the test
	 */
    @BeforeEach
    void initDeck() {
        deck = new AvailableCardsDeck();
    }

	/**
	 * try to remove a card {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard} from the deck{@link it.polimi.ingsw.server.model.assistants.AvailableCardsDeck AvailableCardsDeck} and verify, after operation, that (@code size) is smaller than older (@code size)
	 */
	@Test
	void removeCard() {
		int initialSize = deck.getCount();
		AssistantCard card = deck.getCard(0);
		assertDoesNotThrow(() -> deck.removeCard(0));
		assertEquals(initialSize - 1, deck.getCount());
		assertNotEquals(card, deck.getCard(0));
	}

	/**
	 * try to remove a card {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard} from an empty deck{@link it.polimi.ingsw.server.model.assistants.AvailableCardsDeck AvailableCardsDeck} and verify that it throws the exception
	 */
    @Test
    void removeWithUnderflow() {
        int initialDeckSize = deck.getCount();
        for (int i = 0; i < initialDeckSize; i++) {
            assertDoesNotThrow(() -> deck.removeCard(0));
        }
        assertThrows(CollectionUnderflowError.class, () -> deck.removeCard(0));
    }
	/**
	 * create 2 new decks {@link it.polimi.ingsw.server.model.assistants.AvailableCardsDeck AvailableCardsDeck Deck}, for both (@code Deck) created, for 3 time remove the card with index 0, check doen't throw exception and than check if are equal
	 */
	@Test
	void testEquals() {
		AvailableCardsDeck deckA = new AvailableCardsDeck();
		assertDoesNotThrow(() -> {
			deckA.removeCard(0);
			deckA.removeCard(0);
			deckA.removeCard(0);
		});
		AvailableCardsDeck deckB = new AvailableCardsDeck();
		assertDoesNotThrow(() -> {
			deckB.removeCard(0);
			deckB.removeCard(0);
			deckB.removeCard(0);
		});
		assertEquals(deckA, deckB);
		assertEquals(deckA.hashCode(), deckB.hashCode());
	}
}