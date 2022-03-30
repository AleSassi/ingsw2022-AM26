package it.polimi.ingsw.model.assistants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AvailableCardsDeckTest {

    @Test
    void removeCard() {
        AvailableCardsDeck deck=new AvailableCardsDeck();
        int dimstart=deck.getCount();
        AssistantCard card=deck.getCard(0);
        deck.removeCard(0);
        assertNotEquals(deck.getCount(), dimstart);
        assertNotEquals(deck.getCard(0),card);


    }
}