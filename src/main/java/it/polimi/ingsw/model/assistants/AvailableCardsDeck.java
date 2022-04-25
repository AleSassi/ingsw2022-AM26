package it.polimi.ingsw.model.assistants;

import it.polimi.ingsw.exceptions.model.CollectionUnderflowError;

import java.util.ArrayList;

public class AvailableCardsDeck {
	
	private final ArrayList<AssistantCard> cardDeck = new ArrayList<>();
	
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
     * Returns the number of cards in the deck
     * @return The number of cards that are still available in the deck
     */
	public int getCount() {
		return cardDeck.size();
	}
	
	public AssistantCard getCard(int index) {
		return cardDeck.get(index);
	}
	
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

