package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.exceptions.model.CharacterCardIncorrectParametersException;
import it.polimi.ingsw.server.exceptions.model.CharacterCardNoMoreUsesAvailableException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.TableManager;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.server.model.characters.StopCardActivatorCard;
import it.polimi.ingsw.server.model.student.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class StopCardActivatorCardTest tests StopCardActivatorCard.
 * Coverage 100% (Class), 80% (Methods, Lines, even though all methods are being tested)
 *
 * @author Alessandro Sassi
 * @see StopCardActivatorCard
 */
class StopCardActivatorCardTest {

    /**
     * The card used to run the test case
     */
    private StopCardActivatorCard card;
    /**
     * The TableManager used to run each test case
     */
    private TableManager tableManager;
    /**
     * The Player used to run each test case
     */
    private Player player;

    /**
     * Initializes the private card used for each test
     */
    private void initCard() {
        card = new StopCardActivatorCard(Character.Herbalist);
        card.setupWithTable(tableManager);
    }

    /**
     * Tests that the price increment is 0 when the card has not been purchased
     */
    @Test
    void testZeroIncrementWithoutPurchase() {
        initCard();
        assertEquals(0, card.getPriceIncrement());
    }

    /**
     * Tests that a call to the purchase() method increments the price by 1 but doesn't modify the times used
     */
    @Test
    void testPurchaseModifiesPriceIncrementAndResetsUsageCount() {
        initCard();
        card.purchase();
        assertEquals(1, card.getPriceIncrement());
        assertEquals(0, card.getTimesUsedInCurrentTurn());
    }

    /**
     * Tests that multiple subsequent purchase calls correctly increment the price
     */
    @Test
    void testRepeatedPurchase() {
        initCard();
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
        initCard();
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
        initCard();
        assertEquals(Character.Herbalist, card.getCharacter());
    }

    /**
     * Verifies that the Setup operation for the Card succeeds and runs as expected
     */
    @Test
    void verifyCorrectSetup() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        TableManager tableManager = new TableManager(2, false);
        TableManager copyTableManager = new TableManager(2, false);
        tableManager.copyTo(copyTableManager);
        initCard();

        card.setupWithTable(tableManager);
        assertEquals(0, card.getHostedStudents().getTotalCount());
        //Check that TableManager has not been modified
        assertEquals(tableManager, copyTableManager);
    }

    /**
     * Tests the card in a normal use condition
     */
    @Test
    void verifyNormalUse() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup();

        assertDoesNotThrow(() -> {
            assertEquals(3, card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            //If we use it again the StopCard should return to the Card itself
            assertEquals(4, card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToCard)));
        });
    }

    /**
     * Tests that when the card is empty another attempt at removing a StopCard throws an error
     */
    @Test
    void verifyEmptyThrows() {
        fakePlayerSetup();

        assertThrows(CharacterCardNoMoreUsesAvailableException.class, () -> {
            assertEquals(3, card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            assertEquals(2, card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 1, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            assertEquals(1, card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 2, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            assertEquals(0, card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 3, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            //This should throw an error
            card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 4, CharacterCardParamSet.StopCardMovementMode.ToIsland));
        });
    }

    /**
     * Tests that adding more than 4 StopCards throws an Error
     */
    @Test
    void verifyOverfilledThrows() {
        fakePlayerSetup();
        tableManager.getIslandAtIndex(0).setStopCard(true); //To force the Exception
        assertThrows(CharacterCardNoMoreUsesAvailableException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToCard)));
    }

    /**
     * Tests that when input parameters are not correct the card behaves as expected
     */
    @Test
    void verifyIncorrectParametersThrows() {
        fakePlayerSetup();
        assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, null)));
        assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, -1, 0, CharacterCardParamSet.StopCardMovementMode.ToCard)));
        assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, -1, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
        assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 13, 0, CharacterCardParamSet.StopCardMovementMode.ToCard)));
        assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 13, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
        assertDoesNotThrow(() -> {
            tableManager.getIslandAtIndex(0).setStopCard(true);
            assertEquals(4, card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            tableManager.getIslandAtIndex(0).setStopCard(false);
            assertEquals(4, card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToCard)));

        });
    }

    /**
     * Test to cover hashCode and other overridden methods
     */
    @Test
    void reach100Coverage() {
        initCard();
        StopCardActivatorCard card2 = new StopCardActivatorCard(Character.Herbalist);
        card.copyTo(card2);
        assertEquals(card, card2);
        assertEquals(card.hashCode(), card2.hashCode());
    }

    /**
     * Sets up the card and the required attributes used to simulate a card being played
     */
    private void fakePlayerSetup() {
        tableManager = new TableManager(2, false);
        initCard();
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