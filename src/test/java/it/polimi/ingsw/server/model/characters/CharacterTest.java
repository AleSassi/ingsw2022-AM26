package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.model.characters.Character;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class CharacterTest tests Character.
 * Coverage 100% (Class, Methods), 81% (Lines, although all possible branches should be covered)
 *
 * @author Alessandro Sassi
 * @see Character
 */
class CharacterTest {

    /**
     * Tests that the getter for the initial price os correct for all enums
     */
    @Test
    void testPriceCorrect() {
        for (Character character: Character.values()) {
            assertTrue(character.getInitialPrice() >= 0);
            switch (character) {
                case Abbot, Magician, Circus, Musician -> assertEquals(1, character.getInitialPrice());
                case CheeseMan, Herbalist, Swordsman, Queen -> assertEquals(2, character.getInitialPrice());
                case Ambassador, Centaurus, Mushroom, Thief -> assertEquals(3, character.getInitialPrice());
            }
        }
    }

    /**
     * Tests that the getter for the number of hosted students is correct for all enums
     */
    @Test
    void testHostedStudentsCountCorrect() {
        for (Character character: Character.values()) {
            assertTrue(character.getHostedStudentsCount() >= 0);
            switch (character) {
                case Abbot, Queen -> assertEquals(4, character.getHostedStudentsCount());
                case Circus -> assertEquals(6, character.getHostedStudentsCount());
                default -> assertEquals(0, character.getHostedStudentsCount());
            }
        }
    }

    /**
     * Tests that the getter for  the max number of uses in a single turn is correct for all enums
     */
    @Test
    void testMaxUsesInTurnCorrect() {
        for (Character character: Character.values()) {
            assertTrue(character.getMaxNumberOfUsesInTurn() >= 1);
            switch (character) {
                case Circus -> assertEquals(3, character.getMaxNumberOfUsesInTurn());
                case Musician -> assertEquals(2, character.getMaxNumberOfUsesInTurn());
                default -> assertEquals(1, character.getMaxNumberOfUsesInTurn());
            }
        }
    }

    /**
     * Tests that the getter for whether it changes influence is correct for all enums
     */
    @Test
    void testChangesInfluenceCorrect() {
        for (Character character: Character.values()) {
            switch (character) {
                case Centaurus, Swordsman, Mushroom -> assertTrue(character.getChangesInfluence());
                default -> assertFalse(character.getChangesInfluence());
            }
        }
    }

    /**
     * Tests that the getter for whether it chamges the MN step count is correct for all enums
     */
    @Test
    void testChangesMNStepsCorrect() {
        for (Character character: Character.values()) {
            if (character == Character.Magician) {
                assertTrue(character.getChangesMNSteps());
            } else {
                assertFalse(character.getChangesMNSteps());
            }
        }
    }
}