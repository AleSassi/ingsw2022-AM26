package it.polimi.ingsw.model.assistants;

import java.util.ArrayList;

public class AvailableCardsDeck {

    private final ArrayList<AssistantCard> cardDeck = new ArrayList<>();

    /**
     *return the number of card in the deck
     */

    public AvailableCardsDeck(){
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

    public int getCount(){
        return cardDeck.size();
    }

    public AssistantCard getCard(int index){
        return cardDeck.get(index);

    }

    public AssistantCard removeCard(int index){
        return cardDeck.remove(index);
    }

}

