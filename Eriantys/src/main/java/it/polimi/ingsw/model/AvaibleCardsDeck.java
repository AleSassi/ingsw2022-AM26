package it.polimi.ingsw.model;

import java.util.ArrayList;

public class AvaibleCardsDeck {
    private ArrayList<AssistantCard> Assistantlist=new ArrayList<AssistantCard>();

    /**
     *return the number of card in the deck
     */
    public int CardCount(){
        return Assistantlist.size();

    }

    public AssistantCard getCard(int index){
        return Assistantlist.get(index);

    }

    public AssistantCard removeCard(int index){
        return Assistantlist.remove(index);

    }

}

