package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.exceptions.model.UnavailableCardException;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.characters.CharacterCard;
import it.polimi.ingsw.server.model.characters.CharacterCardExtractor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class CharacterCardExtractorTest tests CharacterCardExtractor.
 * Coverage 100% (1 line will never be executed by design)
 *
 * @author Alessandro Sassi
 * @see CharacterCardExtractor
 */
class CharacterCardExtractorTest {

    /**
     * Method pickRandom tests that the Card deck can correctly extract a single Card
     */
    @Test
    void pickRandom() {
        CharacterCardExtractor extractor = new CharacterCardExtractor();
        assertDoesNotThrow(() -> {
            CharacterCard randomCard = extractor.pickRandom();
            assertNotEquals(null, randomCard);
        });
    }

    /**
     * Method pickRandomUntilEmpty tests that the Card deck can correctly extract all contained Cards, and checks that the first out-of-bounds pick throws the correct exception.
     */
    @Test
    void pickRandomUntilEmpty() {
        int totalNumberOfCards = Character.values().length;
        CharacterCardExtractor extractor = new CharacterCardExtractor();
        List<Character> pickedChars = new ArrayList<>();
        for (int repetition = 0; repetition < totalNumberOfCards; repetition++) {
            assertDoesNotThrow(() -> {
                CharacterCard randomCard = extractor.pickRandom();
                assertNotEquals(null, randomCard);
                assertFalse(pickedChars.contains(randomCard.getCharacter()));
                pickedChars.add(randomCard.getCharacter());
            });
        }
        //We check that now the array is empty and that it throws an error
        assertThrows(UnavailableCardException.class, extractor::pickRandom);
    }
}