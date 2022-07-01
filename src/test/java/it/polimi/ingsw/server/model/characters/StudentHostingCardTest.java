package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.exceptions.model.CharacterCardIncorrectParametersException;
import it.polimi.ingsw.server.exceptions.model.CharacterCardNoMoreUsesAvailableException;
import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.TableManager;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.server.model.characters.StudentHostingCard;
import it.polimi.ingsw.server.model.student.Island;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class StudentHostingCardTest tests StudentHostingCard.
 * Coverage 100% (Class, Methods), 74% (Lines, some exceptions are being handled but since they will never be executed (neither after deployment) they will not be covered)
 *
 * @author Alessandro Sassi
 * @see StudentHostingCard
 */
class StudentHostingCardTest {
    
    private StudentHostingCard card;
    private TableManager tableManager;
    private Player player;
    private List<Player> players;

    private void initCardWithCharacter(Character character) {
        card = new StudentHostingCard(character);
    }

    /**
     * Tests that the price increment is 0 when the card has not been purchased
     */
    @Test
    void testZeroIncrementWithoutPurchase() {
        initCardWithCharacter(Character.Abbot);
        assertEquals(0, card.getPriceIncrement());
    }

    /**
     * Tests that a call to the purchase() method increments the price by 1 but doesn't modify the times used
     */
    @Test
    void testPurchaseModifiesPriceIncrementAndResetsUsageCount() {
        initCardWithCharacter(Character.Abbot);
        card.purchase();
        assertEquals(1, card.getPriceIncrement());
        assertEquals(0, card.getTimesUsedInCurrentTurn());
    }

    /**
     * Tests that multiple subsequent purchase calls correctly increment the price
     */
    @Test
    void testRepeatedPurchase() {
        initCardWithCharacter(Character.Abbot);
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
        initCardWithCharacter(Character.Abbot);
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
        initCardWithCharacter(Character.Abbot);
        assertEquals(Character.Abbot, card.getCharacter());
    }

    /**
     * Tests that the Setup s correct for all Characters taken as constructor parameters
     */
    @Test
    void verifySetup() {
        fakePlayerSetup(Character.Abbot, true, false, true);
        Character[] validCharacters = Character.values();
        for (Character character: validCharacters) {
            initCardWithCharacter(character);
            card.setupWithTable(tableManager);
            assertNotEquals(null, card.getHostedStudents());
            assertEquals(character.getHostedStudentsCount(), card.getHostedStudents().getTotalCount());
        }
    }

    /**
     * Tests the single use of the Abbot card
     */
    @Test
    void verifyUseAbbot() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.Abbot, false);
        TableManager copyTableManager = new TableManager(2, false);
        tableManager.copyTo(copyTableManager);

        assertDoesNotThrow(() -> {
            //Get the first non-empty Student color
            StudentCollection collectionInCard = card.getHostedStudents();
            Student pickedStudent = collectionInCard.pickRandom();
            int studentsOnIsland = tableManager.getIslandAtIndex(0).getNumberOfSameStudents(pickedStudent);
            assertEquals(0, card.useCard(tableManager, null, player, new CharacterCardParamSet(pickedStudent, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            //Check that the Student was moved to the Island
            Island zeroIsland = tableManager.getIslandAtIndex(0);
            assertEquals(studentsOnIsland + 1, zeroIsland.getNumberOfSameStudents(pickedStudent));
        });
    }

    /**
     * Tests that the abbot card can only be called once per turn
     * @throws CollectionUnderflowError Thrown when the card has no students on it (should never happen)
     */
    @Test
    void verifyUseCountAbbot() throws CollectionUnderflowError {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.Abbot, false);

        assertDoesNotThrow(() -> {
            //Get the first non-empty Student color
            StudentCollection collectionInCard = card.getHostedStudents();
            Student pickedStudent = collectionInCard.pickRandom();
            int studentsOnIsland = tableManager.getIslandAtIndex(0).getNumberOfSameStudents(pickedStudent);
            assertEquals(0, card.useCard(tableManager, null, player, new CharacterCardParamSet(pickedStudent, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            //Check that the Student was moved to the Island
            Island zeroIsland = tableManager.getIslandAtIndex(0);
            assertEquals(studentsOnIsland + 1, zeroIsland.getNumberOfSameStudents(pickedStudent));
        });

        StudentCollection collectionInCard = card.getHostedStudents();
        Student pickedStudent = collectionInCard.pickRandom();
        assertThrows(CharacterCardNoMoreUsesAvailableException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(pickedStudent, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
    }

    /**
     * Tests the Abbot card with wrong parameters (null Table)
     */
    @Test
    void verifyIncorrectStudentUseAbbot() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.Abbot, false);

        assertDoesNotThrow(() -> {
            //Get the first non-empty Student color
            StudentCollection collectionInCard = card.getHostedStudents();
            Student pickedStudent = collectionInCard.pickRandom();
            int studentsOnIsland = tableManager.getIslandAtIndex(0).getNumberOfSameStudents(pickedStudent);
            assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(null, null, player, new CharacterCardParamSet(pickedStudent, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            //Check that the Student was moved to the Island
            Island zeroIsland = tableManager.getIslandAtIndex(0);
            assertEquals(studentsOnIsland, zeroIsland.getNumberOfSameStudents(pickedStudent));
        });
    }

    /**
     * Tests the single use of the Circus card
     */
    @Test
    void verifyUsageCircus() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.Circus, false);

        assertDoesNotThrow(() -> {
            StudentCollection collectionInCard = card.getHostedStudents();
            int blueUnicornCount = collectionInCard.getCount(Student.BlueUnicorn);
            Student pickedStudent = collectionInCard.pickRandom();
            int pickedCount = collectionInCard.getCount(pickedStudent);
            assertEquals(0, card.useCard(tableManager, null, player, new CharacterCardParamSet(pickedStudent, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            if (pickedStudent == Student.BlueUnicorn) {
                assertDoesNotThrow(() -> player.removeStudentFromEntrance(Student.BlueUnicorn));
                assertEquals(blueUnicornCount, card.getHostedStudents().getCount(Student.BlueUnicorn));
            } else {
                assertDoesNotThrow(() -> player.removeStudentFromEntrance(pickedStudent));
                assertEquals(blueUnicornCount + 1, card.getHostedStudents().getCount(Student.BlueUnicorn));
                assertEquals(pickedCount, card.getHostedStudents().getCount(pickedStudent));
            }
        });
    }

    /**
     * Tests that the Circus card can only be called once per turn
     * @throws CollectionUnderflowError Thrown when the card has no students on it (should never happen)
     */
    @Test
    void verifyMultiUsageCircus() throws CollectionUnderflowError {
        // We force no character cards on the Table to avoid the auto-setup of Character card
        fakePlayerSetup(Character.Circus, false);
        assertDoesNotThrow(() -> {
            for (int repetition = 0; repetition < Character.Circus.getMaxNumberOfUsesInTurn(); repetition++) {
                fakePlayerSetup(Character.Circus, true, false, true);
                StudentCollection collectionInCard = card.getHostedStudents();
                int blueUnicornCount = collectionInCard.getCount(Student.BlueUnicorn);
                Student pickedStudent = collectionInCard.pickRandom();
                int pickedCount = collectionInCard.getCount(pickedStudent);
                assertEquals(0, card.useCard(tableManager, null, player, new CharacterCardParamSet(pickedStudent, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
                if (pickedStudent == Student.BlueUnicorn) {
                    assertDoesNotThrow(() -> player.removeStudentFromEntrance(Student.BlueUnicorn));
                    assertEquals(blueUnicornCount, card.getHostedStudents().getCount(Student.BlueUnicorn));
                } else {
                    assertDoesNotThrow(() -> player.removeStudentFromEntrance(pickedStudent));
                    assertEquals(blueUnicornCount + 1, card.getHostedStudents().getCount(Student.BlueUnicorn));
                    assertEquals(pickedCount, card.getHostedStudents().getCount(pickedStudent));
                }
            }
        });
        fakePlayerSetup(Character.Circus, false, false);
        StudentCollection collectionInCard = card.getHostedStudents();
        Student pickedStudent = collectionInCard.pickRandom();
        assertThrows(CharacterCardNoMoreUsesAvailableException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(pickedStudent, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
    }

    /**
     * Tests the Abbot card with wrong parameters (null Table)
     */
    @Test
    void verifyIncorrectUsageCircus() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.Circus, false);

        assertDoesNotThrow(() -> {
            StudentCollection collectionInCard = card.getHostedStudents();
            StudentCollection collectionInCardCopy = card.getHostedStudents();
            Student pickedStudent = collectionInCardCopy.pickRandom();
            assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(null, null, player, new CharacterCardParamSet(pickedStudent, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            assertEquals(collectionInCard, card.getHostedStudents());
        });
    }

    /**
     * Tests the use of the Musician card and its usage limit
     */
    @Test
    void verifyUsageMusician() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.Musician, false);

        assertDoesNotThrow(() -> {
            int redDragonsAtTable = player.getCountAtTable(Student.RedDragon);
            int blueUnicornsAtTable = player.getCountAtTable(Student.BlueUnicorn);
            assertEquals(0, card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.RedDragon, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            assertEquals(redDragonsAtTable + 1, player.getCountAtTable(Student.RedDragon));
            assertEquals(blueUnicornsAtTable - 1, player.getCountAtTable(Student.BlueUnicorn));

            int pinkFairsAtTable = player.getCountAtTable(Student.PinkFair);
            int blueUnicornsAtTable2 = player.getCountAtTable(Student.BlueUnicorn);
            assertEquals(0, card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.PinkFair, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            assertEquals(blueUnicornsAtTable2 + 1, player.getCountAtTable(Student.BlueUnicorn));
            assertEquals(pinkFairsAtTable - 1, player.getCountAtTable(Student.PinkFair));
        });
        assertThrows(CharacterCardNoMoreUsesAvailableException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.RedDragon, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
    }

    /**
     * Tests the Musician card with wrong parameters (null Table)
     */
    @Test
    void verifyIncorrectUsageMusician() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.Musician, false);
    
        StudentCollection collectionInCard = card.getHostedStudents();
        assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(null, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
        assertEquals(card.getHostedStudents(), collectionInCard);
    }

    /**
     * Tests the use of the Queen card and its usage limit
     */
    @Test
    void verifyUsageQueen() throws CollectionUnderflowError {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.Queen, false, true, false);

        assertDoesNotThrow(() -> {
            StudentCollection collectionInCard = card.getHostedStudents();
            Student pickedStudent = collectionInCard.pickRandom();
            assertEquals(0, card.useCard(tableManager, null, player, new CharacterCardParamSet(pickedStudent, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            //Test that the Student was moved to the Entrance
            assertDoesNotThrow(() -> player.removeStudentFromTable(pickedStudent));
        });

        StudentCollection collectionInCard = card.getHostedStudents();
        Student pickedStudent = collectionInCard.pickRandom();
        assertThrows(CharacterCardNoMoreUsesAvailableException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(pickedStudent, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
    }

    /**
     * Tests the Queen card with wrong parameters (null Table)
     */
    @Test
    void verifyIncorrectUsageQueen() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.Queen, false);

        assertDoesNotThrow(() -> {
            StudentCollection collectionInCard = card.getHostedStudents();
            StudentCollection collectionInCardCopy = card.getHostedStudents();
            Student pickedStudent = collectionInCardCopy.pickRandom();
            assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(null, null, player, new CharacterCardParamSet(pickedStudent, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            assertEquals(card.getHostedStudents(), collectionInCard);
        });
    }

    /**
     * Tests the use of the Thief card and its usage limit
     */
    @Test
    void verifyUsageThief() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.Thief, true, true, true);

        assertDoesNotThrow(() -> {
            Student chosenStolenStudent = Student.BlueUnicorn;
            assertEquals(0, card.useCard(tableManager, players, player, new CharacterCardParamSet(chosenStolenStudent, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            assertEquals(1, player.getCountAtTable(chosenStolenStudent)); // 4 Blue students
            assertEquals(0, players.get(players.size() - 1).getCountAtTable(chosenStolenStudent)); // 1 Blue Student
            assertEquals(1, player.getCountAtTable(Student.RedDragon));
            assertEquals(1, player.getCountAtTable(Student.GreenFrog));
            assertEquals(1, player.getCountAtTable(Student.PinkFair));
            assertEquals(1, player.getCountAtTable(Student.YellowElf));
            assertEquals(1, players.get(players.size() - 1).getCountAtTable(Student.RedDragon));
            assertEquals(3, players.get(players.size() - 1).getCountAtTable(Student.GreenFrog));
            assertEquals(1, players.get(players.size() - 1).getCountAtTable(Student.PinkFair));
            assertEquals(1, players.get(players.size() - 1).getCountAtTable(Student.YellowElf));
        });

        assertThrows(CharacterCardNoMoreUsesAvailableException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
    }

    /**
     * Tests the Thief card with wrong parameters (null Table)
     */
    @Test
    void verifyIncorrectUsageThief() {
        // We force no character cards on the Table to avoid the auto-setup of Character cards
        fakePlayerSetup(Character.Thief, true, true, true);

        assertDoesNotThrow(() -> {
            StudentCollection collectionInCard = card.getHostedStudents();
            assertEquals(0, card.useCard(tableManager, players, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
            assertEquals(card.getHostedStudents(), collectionInCard);
        });
    }

    /**
     * Test to cover hashCode and other overridden methods
     */
    @Test
    void reach100Coverage() {
        fakePlayerSetup(Character.Abbot, true, true, true);
        StudentHostingCard card2 = new StudentHostingCard(Character.Abbot);
        card.copyTo(card2);
        assertEquals(card, card2);
        assertEquals(card.hashCode(), card2.hashCode());
    }
    
    @RepeatedTest(100)
    void testInvalidParameters() {
        Character[] characters = {Character.Abbot, Character.Circus, Character.Musician, Character.Queen, Character.Thief};
        for (Character character: characters) {
            fakePlayerSetup(character, false);
            card.purchase();
            assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(tableManager, null, null, null));
            card.purchase();
            assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(null, null, null, null, false, -1, -1, -1, CharacterCardParamSet.StopCardMovementMode.ToCard)));
            card.purchase();
            assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(null, null, null, null, false, -1, 13, 13, CharacterCardParamSet.StopCardMovementMode.ToCard)));
            if (character == Character.Abbot) {
                card.purchase();
                assertThrows(CharacterCardIncorrectParametersException.class, () -> card.useCard(tableManager, null, player, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, -1, 13, 13, CharacterCardParamSet.StopCardMovementMode.ToCard)));
            }
        }
    }

    /**
     * Sets up the fake Players that will be used to test the class
     * @param character The Character used to initialize the card
     * @param autoplaceTestStudents Whether the Students should be auto-placed on the test island (idx 0)
     */
    private void fakePlayerSetup(Character character, boolean autoplaceTestStudents) {
        fakePlayerSetup(character, autoplaceTestStudents, true);
    }

    /**
     * Sets up the fake Players that will be used to test the class
     * @param character The Character used to initialize the card
     * @param autoplaceTestStudents Whether the Students should be auto-placed on the test island (idx 0)
     * @param initCard Whether the Card should be initialized
     */
    private void fakePlayerSetup(Character character, boolean autoplaceTestStudents, boolean initCard) {
        fakePlayerSetup(character, autoplaceTestStudents, initCard, true);
    }

    /**
     * Sets up the fake Players that will be used to test the class
     * @param character The Character used to initialize the card
     * @param autoplaceTestStudents Whether the Students should be auto-placed on the test island (idx 0)
     * @param initCard Whether the Card should be initialized
     * @param initEntrance Whether the Entrance space of the Player should be initialized with test values
     */
    private void fakePlayerSetup(Character character, boolean autoplaceTestStudents, boolean initCard, boolean initEntrance) {
        tableManager = new TableManager(2, false);
        if (initCard) {
            initCardWithCharacter(character);
            card.setupWithTable(tableManager);
        }
        //Simulate the Player putting some Students on Island 0
        assertDoesNotThrow(() -> {
            player = new Player("Ale", Wizard.Wizard1, Tower.Black, 8, 1);
            if (initEntrance) {
                player.addStudentToEntrance(Student.BlueUnicorn);
                player.addStudentToEntrance(Student.BlueUnicorn);
                player.addStudentToEntrance(Student.BlueUnicorn);
                player.addStudentToEntrance(Student.RedDragon);
                player.addStudentToEntrance(Student.GreenFrog);
                player.addStudentToEntrance(Student.PinkFair);
                player.addStudentToEntrance(Student.YellowElf);
                assertDoesNotThrow(() -> {
                    player.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
                    player.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
                    player.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
                    player.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
                    player.placeStudentAtTableAndGetCoin(Student.RedDragon, tableManager);
                    player.placeStudentAtTableAndGetCoin(Student.GreenFrog, tableManager);
                    player.placeStudentAtTableAndGetCoin(Student.PinkFair, tableManager);
                    player.placeStudentAtTableAndGetCoin(Student.YellowElf, tableManager);
                });
            }
            if (autoplaceTestStudents) {
                player.addProfessor(Professor.BlueUnicorn);
                player.addProfessor(Professor.GreenFrog);
                tableManager.placeStudentOnIsland(Student.BlueUnicorn, 0);
                tableManager.placeStudentOnIsland(Student.BlueUnicorn, 0);
                tableManager.placeStudentOnIsland(Student.BlueUnicorn, 0);
                tableManager.placeStudentOnIsland(Student.RedDragon, 0);
                tableManager.placeStudentOnIsland(Student.RedDragon, 0);
                tableManager.placeStudentOnIsland(Student.GreenFrog, 0);
            }
            players = new ArrayList<>();
            players.add(player);
    
            Player secondPlayer = new Player("Fede", Wizard.Wizard2, Tower.White, 8, 1);
            secondPlayer.addStudentToEntrance(Student.RedDragon);
            secondPlayer.addStudentToEntrance(Student.RedDragon);
            secondPlayer.addStudentToEntrance(Student.RedDragon);
            secondPlayer.addStudentToEntrance(Student.BlueUnicorn);
            secondPlayer.addStudentToEntrance(Student.GreenFrog);
            secondPlayer.addStudentToEntrance(Student.PinkFair);
            secondPlayer.addStudentToEntrance(Student.YellowElf);
            assertDoesNotThrow(() -> {
                secondPlayer.placeStudentAtTableAndGetCoin(Student.GreenFrog, tableManager);
                secondPlayer.placeStudentAtTableAndGetCoin(Student.GreenFrog, tableManager);
                secondPlayer.placeStudentAtTableAndGetCoin(Student.GreenFrog, tableManager);
                secondPlayer.placeStudentAtTableAndGetCoin(Student.RedDragon, tableManager);
                secondPlayer.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
                secondPlayer.placeStudentAtTableAndGetCoin(Student.PinkFair, tableManager);
                secondPlayer.placeStudentAtTableAndGetCoin(Student.YellowElf, tableManager);
            });
            players.add(secondPlayer);
        });
    }

}