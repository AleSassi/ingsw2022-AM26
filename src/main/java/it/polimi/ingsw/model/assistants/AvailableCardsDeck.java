package it.polimi.ingsw.model.assistants;

import java.util.ArrayList;

public class AvailableCardsDeck {

    private final ArrayList<AssistantCard> cardDeck = new ArrayList<>();

    /**
     *return the number of card in the deck
     */
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

