package it.polimi.ingsw.model.assistants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CardDeckBuilderTest {

    @Test
    void buildDeck() {
        CardDeckBuilder deckbuild=CardDeckBuilder.getInstance();
        AvailableCardsDeck deck=deckbuild.buildDeck();
        AssistantCard class1=AssistantCard.TURTLE;
        AssistantCard class2=AssistantCard.LION;
        assertEquals(deck.getCard(0), class1 );
        assertEquals(deck.getCard(9), class2 );
    }
}