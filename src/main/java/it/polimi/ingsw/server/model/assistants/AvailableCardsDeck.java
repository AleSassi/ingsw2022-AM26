package it.polimi.ingsw.server.model.assistants;

import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;

import java.util.ArrayList;
/**
 * This Class represents the {@code AssistantCard}
 * @author Leonardo Betti
 */
public class AvailableCardsDeck {
	
	private final ArrayList<AssistantCard> cardDeck = new ArrayList<>();
	
	/**
	 * Constructor, prepares the deck with all the cards
	 */
	public AvailableCardsDeck() {
		cardDeck.add(AssistantCard.TURTLE);
		cardDeck.add(AssistantCard.ELEPHANT);
		cardDeck.add(AssistantCard.DOG);
		cardDeck.add(AssistantCard.OCTOPUS);
		cardDeck.add(AssistantCard.SNAKE);
		cardDeck.add(AssistantCard.FOX);
		cardDeck.add(AssistantCard.EAGLE);
		cardDeck.add(AssistantCard.CAT);
		cardDeck.add(AssistantCard.PEAFOWL);
		cardDeck.add(AssistantCard.LION);
	}
    
    /**
     * Extracts the total size of the deck
     * @return (type int) The number of cards that are still available in the deck
     */
	public int getCount() {
		return cardDeck.size();
	}
	
	/**
	 * Extracts a card at the given index
	 * @param index (type int) Index of card to take
	 * @return (type AssistantCard) The {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard} at the specified index
	 */
	public AssistantCard getCard(int index) {
		return cardDeck.get(index);
	}
	
	/**
	 * Removes the {@link it.polimi.ingsw.server.model.assistants.AssistantCard card} at the specified index
	 * @param index (type int) The index of the card to remove
	 * @return (type int) The number of cards that are still available in the deck
	 * @throws CollectionUnderflowError If the deck is already empty
	 */
	public AssistantCard removeCard(int index) throws CollectionUnderflowError {
		if (cardDeck.isEmpty()) throw new CollectionUnderflowError();
		
		return cardDeck.remove(index);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		AvailableCardsDeck that = (AvailableCardsDeck) o;
		
		return cardDeck.equals(that.cardDeck);
	}
	
	@Override
	public int hashCode() {
		return cardDeck.hashCode();
	}
}

