package it.polimi.ingsw.server.model.assistants;

import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;

import java.util.ArrayList;
/**
 * This Class represent the {@code AssistantCard}
 * @author Leonardo Betti
 */
public class AvailableCardsDeck {
	/**
	 * initialize {@code AssistantCard}
	 */
	private final ArrayList<AssistantCard> cardDeck = new ArrayList<>();
	/**
	 * constructor, prepare the deck with all the card
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
     * getter
     * @return (type int)The number of cards that are still available in the deck
     */
	public int getCount() {
		return cardDeck.size();
	}
	/**
	 * getter
	 *  @param index (type int) index of card to take
	 * @return (type Assistant card) {@link it.polimi.ingsw.server.model.assistants.AssistantCard Assistantcard}
	 */
	public AssistantCard getCard(int index) {
		return cardDeck.get(index);
	}
	/**
	 * remove card {@link it.polimi.ingsw.server.model.assistants.AssistantCard Assistantcard} with
	 *  @param index (type int) from deck
	 * @return (type int)The number of cards that are still available in the deck
	 */
	public AssistantCard removeCard(int index) throws CollectionUnderflowError {
		if (cardDeck.isEmpty()) throw new CollectionUnderflowError();
		
		return cardDeck.remove(index);
	}
	/**verify if this class is equal to
	 * @param(type Object)
	 * @return (type bool) true if class are equal, false otherwise
	 */
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

