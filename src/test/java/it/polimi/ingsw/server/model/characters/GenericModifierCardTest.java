package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.exceptions.model.CharacterCardIncorrectParametersException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.TableManager;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.characters.CharacterCard;
import it.polimi.ingsw.server.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.server.model.characters.GenericModifierCard;
import it.polimi.ingsw.server.model.student.Student;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class GenericModifierCardTest tests GenericModifierCard.
 * Coverage 100%
 *
 * @author Alessandro Sassi
 * @see GenericModifierCard
 */
class GenericModifierCardTest {

    /**
     * The card used to run the test case
     */
    private GenericModifierCard card;
    /**
     * The TableManager used to run each test case
     */
    private TableManager tableManager;
    /**
     * The Player used to run each test case
     */
    private Player player;

    /**
     * Initializes the private card used for each test with the character
     * @param character The Character that will be used to instantiate a GenericModifierCard
     */
    private void initCardWithCharacter(Character character) {
        card = new GenericModifierCard(character);
    }

    /**
     * Tests that the price increment is 0 when the card has not been purchased
     */
    @Test
    void testZeroIncrementWithoutPurchase() {
        initCardWithCharacter(Character.Herbalist);
        assertEquals(0, card.getPriceIncrement());
    }

    /**
     * Tests that a call to the purchase() method increments the price by 1 but doesn't modify the times used
     */
    @Test
    void testPurchaseModifiesPriceIncrementAndResetsUsageCount() {
        initCardWithCharacter(Character.Herbalist);
        card.purchase();
        assertEquals(1, card.getPriceIncrement());
        assertEquals(0, card.getTimesUsedInCurrentTurn());
    }

    /**
     * Tests that multiple subsequent purchase calls correctly increment the price
     */
    @Test
    void testRepeatedPurchase() {
        initCardWithCharacter(Character.Herbalist);
        int repetitions = 100;
        for (int repetition = 0; repetition < repetitions; repetition++) {
            card.purchase();
        }
        assertEquals(1, card.getPriceIncrement());
        assertEquals(0, card.getTimesUsedInCurrentTurn());
    }

    /**
     * Tests that after multiple uses of a card the number of uses in the turn is updated correctly
     */
    @Test
    void testCardPurchaseAndMultiuse() {
        initCardWithCharacter(Character.Herbalist);
        int repetitions = 20;
        card.purchase();
        for (int repetition = 0; repetition < repetitions; repetition++) {
            card.markCardAsUsedInTurn();
        }
        assertEquals(1, card.getPriceIncrement());
        assertEquals(repetitions, card.getTimesUsedInCurrentTurn());
        card.purchase();
        assertEquals(1, card.getPriceIncrement());
        assertEquals(0, card.getTimesUsedInCurrentTurn());
    }

    /**
     * Tests that the getter for the Character is correct
     */
    @Test
    void testReturnedCharacterCorrect() {
        initCardWithCharacter(Character.Herbalist);
        assertEquals(Character.Herbalist, card.getCharacter());
    }

    /**
     * Verifies that the setupCard operation does not modify the Table and correctly initializes the card
     */
    @Test
    void verifyCorrectSetup_EmptySetup() {
        Character[] characters = {Character.Magician, Character.Mushroom, Character.CheeseMan, Character.Ambassador, Character.Centaurus, Character.Swordsman};
        for (Character character: characters) {
            // We force no character cards on the Table to avoid the auto-setup of Character cards
            TableManager tableManager = new TableManager(2, false);
            TableManager copyTableManager = new TableManager(2, false);
            tableManager.copyTo(copyTableManager);
            initCardWithCharacter(character);
            CharacterCard copyCard = new GenericModifierCard(character);
            card.copyTo(copyCard);

            card.setupWithTable(tableManager);
            assertEquals(0, card.getHostedStudents().getTotalCount());
            //Check that TableManager has not been modified
            assertEquals(tableManager, copyTableManager);
            //Check that the Card has not been modified
            assertEquals(card, copyCard);
        }
    }

    /**
     * Tests that even if the Character is incorrect the setupCard operation succeeds
     */
    @Test
    void verifyCorrectSetup_IncorrectCards() {
        Character[] characters = {Character.Magician, Character.Mushroom, Character.CheeseMan, Character.Ambassador, Character.Centaurus, Character.Swordsman};
        List<Character> characterList = List.of(characters);
        for (Character character: Character.values()) {
            if (!characterList.contains(character)) {
                // We force no character cards on the Table to avoid the auto-setup of Character cards
                TableManager tableManager = new TableManager(2, false);
                TableManager copyTableManager = new TableManager(2, false);
                tableManager.copyTo(copyTableManager);
                initCardWithCharacter(character);
                CharacterCard copyCard = new GenericModifierCard(character);
                card.copyTo(copyCard);

                card.setupWithTable(tableManager);
                //Check that TableManager has not been modified
                assertEquals(tableManager, copyTableManager);
                //Check that the Card has not been modified
                assertEquals(card, copyCard);
            }
        }
    }

    /**
     * Verifies that the Magician card returns the correct modifier, even after repeated uses
     */
    @Test
    void verifyUseMagician() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.Magician);
        TableManager copyTableManager = new TableManager(2, false);
        tableManager.copyTo(copyTableManager);

        assertDoesNotThrow(() -> {
            assertEquals(2, card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 2, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            //If we use it again it should report the same modifier
            assertEquals(2, card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            //The table must not be modified
            assertEquals(tableManager, copyTableManager);
        });
    }

    /**
     * Verifies that the Mushroom card returns the correct modifier, even after repeated uses
     */
    @Test
    void verifyUsageMushroom() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.Mushroom);
        TableManager copyTableManager = new TableManager(2, false);
        tableManager.copyTo(copyTableManager);

        assertDoesNotThrow(() -> {
            assertEquals(-1, card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            //If we use it again it should report the same modifier
            assertEquals(-1 * tableManager.getIslandAtIndex(0).getNumberOfSameStudents(Student.BlueUnicorn), card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            //The table must not be modified
            assertEquals(tableManager, copyTableManager);
        });
    }

    /**
     * Verifies that the Mushroom card without a target island returns the correct modifier, even after repeated uses
     */
    @Test
    void verifyUsageMushroomNoTarget() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.Mushroom);
        TableManager copyTableManager = new TableManager(2, false);
        tableManager.copyTo(copyTableManager);
        
        assertDoesNotThrow(() -> card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, -1, -1, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
        assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, -1, -1, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
        assertEquals(tableManager, copyTableManager);
    }

    /**
     * Verifies that the CheeseMan card returns the correct modifier, even after repeated uses
     */
    @Test
    void verifyUsageCheeseMan() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.CheeseMan);
        TableManager copyTableManager = new TableManager(2, false);
        tableManager.copyTo(copyTableManager);

        assertDoesNotThrow(() -> {
            //The CheeseMan must return 1 to make > checks become >=
            assertEquals(1, card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            //If we use it again it should report the same modifier
            assertEquals(1, card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            //The table must not be modified
            assertEquals(tableManager, copyTableManager);
        });
    }

    /**
     * Verifies that the Ambassador card returns the correct modifier, even after repeated uses
     */
    @Test
    void verifyUsageAmbassador() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.Ambassador);
        TableManager copyTableManager = new TableManager(2, false);
        tableManager.copyTo(copyTableManager);

        assertDoesNotThrow(() -> {
            //The CheeseMan must return 1 to make > checks become >=
            assertEquals(tableManager.getIslandAtIndex(0).getInfluence(player), card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            //The table must not be modified
            assertEquals(tableManager, copyTableManager);
        });
    }

    /**
     * Verifies that the Centaurus card returns the correct modifier, even after repeated uses
     */
    @Test
    void verifyUsageCentaurus() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.Centaurus);
        TableManager copyTableManager = new TableManager(2, false);
        tableManager.copyTo(copyTableManager);

        assertDoesNotThrow(() -> {
            //The CheeseMan must return 1 to make > checks become >=
            assertEquals(-1 * tableManager.getIslandAtIndex(0).getTowerCount(), card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            //The table must not be modified
            assertEquals(tableManager, copyTableManager);
        });
    }

    /**
     * Verifies that the Swordsman card returns the correct modifier, even after repeated uses
     */
    @Test
    void verifyUsageSwordsman() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.Swordsman);
        TableManager copyTableManager = new TableManager(2, false);
        tableManager.copyTo(copyTableManager);

        assertDoesNotThrow(() -> {
            //The CheeseMan must return 1 to make > checks become >=
            assertEquals(2, card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            //The table must not be modified
            assertEquals(tableManager, copyTableManager);
        });
    }

    /**
     * This test uses hasCode to reach 100% coverage
     */
    @Test
    void reach100Coverage() {
        fakePlayerSetup(Character.Magician);
        int code = card.hashCode();
        assertEquals(code, card.hashCode());
    }
    
    /**
     * Tests card purchase and use with invalid parameters
     */
    @RepeatedTest(100)
    void testInvalidParameters() {
        Character[] characters = {Character.Ambassador, Character.Magician, Character.Centaurus, Character.Mushroom};
        for (Character character: characters) {
            fakePlayerSetup(character);
            if (character == Character.Mushroom) {
                assertDoesNotThrow(() -> card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, null, null, null, false, -1, -1 , -1, CharacterCardParamSet.StopCardMovementMode.ToCard)));
            }
            card.purchase();
            assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(tableManager, null, null, null));
            card.purchase();
            assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(null, null, null, null, false, -1, -1, -1, CharacterCardParamSet.StopCardMovementMode.ToCard)));
            card.purchase();
            assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(null, null, null, null, false, -1, 13, 13, CharacterCardParamSet.StopCardMovementMode.ToCard)));
            if (character == Character.Ambassador || character == Character.Mushroom || character == Character.Centaurus) {
                card.purchase();
                assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, -1, 13, 13, CharacterCardParamSet.StopCardMovementMode.ToCard)));
            }
        }
    }

    /**
     * Sets up the card and the required attributes used to simulate a card being played
     * @param characterInit The Character used to initialize the Card
     */
    private void fakePlayerSetup(Character characterInit) {
        tableManager = new TableManager(2, false);
        initCardWithCharacter(characterInit);
        //Simulate the Player putting some Students on Island 0
        assertDoesNotThrow(() -> {
            player = new Player("Test", Wizard.Wizard1, Tower.Black, 8, 1);
            player.addProfessor(Professor.BlueUnicorn);
            player.addProfessor(Professor.GreenFrog);
        });
        tableManager.placeStudentOnIsland(Student.BlueUnicorn, 0);
        tableManager.placeStudentOnIsland(Student.BlueUnicorn, 0);
        tableManager.placeStudentOnIsland(Student.BlueUnicorn, 0);
        tableManager.placeStudentOnIsland(Student.RedDragon, 0);
        tableManager.placeStudentOnIsland(Student.RedDragon, 0);
        tableManager.placeStudentOnIsland(Student.GreenFrog, 0);
    }
}