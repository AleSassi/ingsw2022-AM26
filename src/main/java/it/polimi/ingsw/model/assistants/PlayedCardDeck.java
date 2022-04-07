package it.polimi.ingsw.model.assistants;

import java.util.ArrayList;

public class PlayedCardDeck {
	
	private final ArrayList<AssistantCard> assistantCardsDeck = new ArrayList<>();
	
	/**
	 * Gets and returns the last <code>AssistantCard</code> that has been played by the Player that owns this deck.
	 *
	 * @return The last <code>AssistantCard</code> that the Player owning this Deck has played. This card sits on top of the currently visible Deck.
	 */
	public AssistantCard topCard() {
		return assistantCardsDeck.get(assistantCardsDeck.size() - 1);
	}
	
	/**
	 * Adds a newly played card on top of the card deck. This card is the one which will be used to compute the turn order for the player.
	 * <p>
	 * The card is placed at the end of the card deck, and will be returned by all subsequent calls to <code>topCard()</code>.
	 *
	 * @param card The card that has been played by the Player that owns this deck and that will be added on top of the Deck
	 */
	public void addCardOnTop(AssistantCard card) {
		assistantCardsDeck.add(card);
	}
	
}
