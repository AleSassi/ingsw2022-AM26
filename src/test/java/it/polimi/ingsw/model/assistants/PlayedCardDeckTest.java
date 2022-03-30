package it.polimi.ingsw.model.assistants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayedCardDeckTest {



    @Test
    void addCardOnTop() {
      AssistantCard test=AssistantCard.EAGLE;
      PlayedCardDeck testdeck=new PlayedCardDeck();
      testdeck.AddCardOnTop(test);
      assertEquals(testdeck.topCard(), test);


    }
}