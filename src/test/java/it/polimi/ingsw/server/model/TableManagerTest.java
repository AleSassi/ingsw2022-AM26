package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.server.exceptions.model.IslandSkippedControlAssignmentForStopCardException;
import it.polimi.ingsw.server.exceptions.model.IslandSkippedInfluenceForStopCardException;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.characters.CharacterCard;
import it.polimi.ingsw.server.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class TableManagerTest tests TableManager.
 * Coverage 100% (Class), 100% (Methods, but every public method is being called by the Test class), 94% (Lines)
 *
 * @author Alessandro Sassi
 * @see TableManager
 */
class TableManagerTest {

    /**
     * The TableManager object that will be used to run the tests
     */
    private TableManager tableManager;

    /**
     * Initializes the tableManager attribute to run every other test
     */
    @BeforeEach
    void initTable() {
        tableManager = new TableManager(2, true);
    }

    /**
     * Tests that the Table is correctly initialized
     */
    @Test
    void testTableInit() {
        assertEquals(2, tableManager.getNumberOfClouds());
        assertEquals(12, tableManager.getNumberOfIslands());
        for (int islandIdx = 0; islandIdx < 12; islandIdx++) {
            assertFalse(tableManager.getIslandAtIndex(islandIdx).itHasStopCard());
            if (tableManager.getIslandAtIndex(islandIdx).isMotherNaturePresent()) {
                for (Student s: Student.values()) {
                    assertEquals(0, tableManager.getIslandAtIndex(islandIdx).getCount(s));
                }
            } else {
                Student placedStudent = null;
                for (Student s: Student.values()) {
                    if (tableManager.getIslandAtIndex(islandIdx).getCount(s) > 0) {
                        placedStudent = s;
                        break;
                    }
                }
                for (Student s: Student.values()) {
                    if (s == placedStudent) {
                        assertEquals(1, tableManager.getIslandAtIndex(islandIdx).getCount(s));
                    } else {
                        assertEquals(0, tableManager.getIslandAtIndex(islandIdx).getCount(s));
                    }
                }
                if (placedStudent == null) {
                    assertTrue(tableManager.getIslandAtIndex(TableManager.circularWrap(islandIdx + 6, 12)).isMotherNaturePresent());
                }
            }
            assertEquals(0, tableManager.getIslandAtIndex(islandIdx).getTowerCount());
            assertNull(tableManager.getIslandAtIndex(islandIdx).getActiveTowerType());
        }
        assertDoesNotThrow(() -> {
            assertNotNull(tableManager.getCardAtIndex(0));
            assertNotNull(tableManager.getCardAtIndex(1));
            assertNotNull(tableManager.getCardAtIndex(2));
        });
    }

    /**
     * Checks that the current Island has the Mother Nature on it
     */
    @Test
    void verifyCurrentIsland() {
        assertTrue(tableManager.getCurrentIsland().isMotherNaturePresent());
    }

    /**
     * Checks that the movement of the Mother Nature pawn is correct
     */
    @Test
    void verifyMotherNatureMovement() {
        int prevIndex = 0;
        int newIndex = 0;
        for (int islandIdx = 0; islandIdx < 12; islandIdx++) {
            if (tableManager.getIslandAtIndex(islandIdx).isMotherNaturePresent()) {
                break;
            }
            prevIndex += 1;
        }
        tableManager.moveMotherNature(3);
        assertTrue(tableManager.getCurrentIsland().isMotherNaturePresent());
        for (int islandIdx = 0; islandIdx < 12; islandIdx++) {
            if (tableManager.getIslandAtIndex(islandIdx).isMotherNaturePresent()) {
                break;
            }
            newIndex += 1;
        }
        assertEquals(TableManager.circularWrap(prevIndex + 3, 12), newIndex);
        int numberOfIslandsWithMN = 0;
        for (int islandIdx = 0; islandIdx < 12; islandIdx++) {
            if (tableManager.getIslandAtIndex(islandIdx).isMotherNaturePresent()) {
                numberOfIslandsWithMN += 1;
            }
        }
        assertEquals(1, numberOfIslandsWithMN);
    }

    /**
     * Checks that the movement of Mother Nature is correct even with Stop Cards
     */
    @Test
    void verifyMotherNatureMovementWithStops() {
        int prevIndex = 0;
        int newIndex = 0;
        for (int islandIdx = 0; islandIdx < 12; islandIdx++) {
            if (tableManager.getIslandAtIndex(islandIdx).isMotherNaturePresent()) {
                break;
            }
            prevIndex += 1;
        }
        tableManager.getIslandAtIndex(TableManager.circularWrap(prevIndex + 3, 12)).setStopCard(true);
        tableManager.moveMotherNature(3);
        assertTrue(tableManager.getCurrentIsland().isMotherNaturePresent());
        for (int islandIdx = 0; islandIdx < 12; islandIdx++) {
            if (tableManager.getIslandAtIndex(islandIdx).isMotherNaturePresent()) {
                break;
            }
            newIndex += 1;
        }
        assertEquals(TableManager.circularWrap(prevIndex + 3, 12), newIndex);
        int numberOfIslandsWithMN = 0;
        for (int islandIdx = 0; islandIdx < 12; islandIdx++) {
            if (tableManager.getIslandAtIndex(islandIdx).isMotherNaturePresent()) {
                numberOfIslandsWithMN += 1;
            }
        }
        assertEquals(1, numberOfIslandsWithMN);
    }

    /**
     * Tests that there are the expected number of students in the Bag at the start of each game
     */
    @Test
    void verifyBagContainsEnoughStudents() {
        StudentCollection pickedStudents = new StudentCollection();
        StudentCollection studentsInCards = new StudentCollection();
        int baseStudents = 120;
        for (int cardIdx = 0; cardIdx < 3; cardIdx++) {
            baseStudents -= tableManager.getCardAtIndex(cardIdx).getHostedStudents().getTotalCount();
            studentsInCards.mergeWithCollection(tableManager.getCardAtIndex(cardIdx).getHostedStudents());
        }
        int finalBaseStudents = baseStudents;
        assertDoesNotThrow(() -> {
            for (int extractIdx = 0; extractIdx < finalBaseStudents; extractIdx++) {
                pickedStudents.mergeWithCollection(tableManager.pickStudentsFromBag(1));
            }
        });
        assertThrows(CollectionUnderflowError.class, () -> tableManager.pickStudentsFromBag(1));
        for (Student s: Student.values()) {
            assertEquals(24 - studentsInCards.getCount(s), pickedStudents.getCount(s));
        }
    }

    /**
     * Checks that multiple extractions and readditions to the Bag are correct
     */
    @Test
    void verifyPickAndPutInBag() {
        StudentCollection pickedStudents = new StudentCollection();
        assertDoesNotThrow(() -> {
            for (int extractIdx = 0; extractIdx < 4; extractIdx++) {
                pickedStudents.mergeWithCollection(tableManager.pickStudentsFromBag(1));
            }
        });
        for (Student s: Student.values()) {
            for (int studIdx = 0; studIdx < pickedStudents.getCount(s); studIdx++) {
                tableManager.putStudentInBag(s);
            }
        }
        //The Bag should be in the same state
        verifyBagContainsEnoughStudents();
    }

    /**
     * Tests picking students from an empty island
     */
    @Test
    void verifyPickFromCloudEmptyDefault() {
        for (int cloudIdx = 0; cloudIdx < tableManager.getNumberOfClouds(); cloudIdx++) {
            int finalCloudIdx = cloudIdx;
            assertThrows(CollectionUnderflowError.class, () -> tableManager.pickStudentsFromCloud(finalCloudIdx));
        }
    }

    /**
     * Tests setting students on a Cloud
     */
    @Test
    void verifySetToCloud() {
        assertDoesNotThrow(() -> {
            for (int cloudIdx = 0; cloudIdx < tableManager.getNumberOfClouds(); cloudIdx++) {
                tableManager.placeStudentOnCloud(Student.BlueUnicorn, cloudIdx, 1);
                tableManager.placeStudentOnCloud(Student.BlueUnicorn, cloudIdx, 1);
                tableManager.placeStudentOnCloud(Student.BlueUnicorn, cloudIdx, 1);
                assertEquals(3, tableManager.pickStudentsFromCloud(cloudIdx).getCount(Student.BlueUnicorn));
            }
        });
    }

    /**
     * Tests placing some Students on an Island
     */
    @Test
    void verifyStudentPlacementOnIsland() {
        //We use the Island at index 0 for our tests
        int blueCount = tableManager.getIslandAtIndex(0).getCount(Student.BlueUnicorn);
        int redCount = tableManager.getIslandAtIndex(0).getCount(Student.RedDragon);
        int greenCount = tableManager.getIslandAtIndex(0).getCount(Student.GreenFrog);
        int yellowCount = tableManager.getIslandAtIndex(0).getCount(Student.YellowElf);
        int pinkCount = tableManager.getIslandAtIndex(0).getCount(Student.PinkFair);

        tableManager.placeStudentOnIsland(Student.BlueUnicorn, 0);
        tableManager.placeStudentOnIsland(Student.BlueUnicorn, 0);
        tableManager.placeStudentOnIsland(Student.BlueUnicorn, 0);
        tableManager.placeStudentOnIsland(Student.RedDragon, 0);
        tableManager.placeStudentOnIsland(Student.GreenFrog, 0);
        assertEquals(3 + blueCount, tableManager.getIslandAtIndex(0).getCount(Student.BlueUnicorn));
        assertEquals(1 + redCount, tableManager.getIslandAtIndex(0).getCount(Student.RedDragon));
        assertEquals(1 + greenCount, tableManager.getIslandAtIndex(0).getCount(Student.GreenFrog));
        assertEquals(yellowCount, tableManager.getIslandAtIndex(0).getCount(Student.YellowElf));
        assertEquals(pinkCount, tableManager.getIslandAtIndex(0).getCount(Student.PinkFair));
    }

    /**
     * Tests that the Influence count is OK without stop cards but with Character Cards
     */
    @RepeatedTest(100)
    void verifyInfluenceCount_NoStopCards() {
        verifyInfluenceCount(true, false, false, true, 0);
    }

    /**
     * Tests that the Influence count is OK with stop cards but without Character Cards
     */
    @RepeatedTest(100)
    void verifyInfluenceCount_StopCards() {
        verifyInfluenceCount(false, false, true, false, 0);
    }

    /**
     * Tests that the Influence count is OK with stop cards and Character Cards
     */
    @RepeatedTest(100)
    void verifyInfluenceCount_Multi() {
        verifyInfluenceCount(false, true, false, true, 1);
    }

    /**
     * Tests the control change operation (Tower swap)
     */
    @Test
    void testChangeControl() {
        assertDoesNotThrow(() -> {
            Player testPlayer = new Player("Ale", Wizard.Wizard1, Tower.Black, 8, 1);
            Player testPlayer2 = new Player("Fede", Wizard.Wizard1, Tower.White, 8, 1);
    
            assertDoesNotThrow(() -> tableManager.getCurrentIsland().setTower(testPlayer.pickAndRemoveTower()));
            assertDoesNotThrow(() -> tableManager.changeControlOfCurrentIsland(testPlayer, testPlayer2));
            assertEquals(Tower.White, tableManager.getCurrentIsland().getActiveTowerType());
        });
    }

    /**
     * Tests the control change operation (Tower swap) with incorrect parameters
     */
    @Test
    void testChangeControlWrongParams() {
        assertDoesNotThrow(() -> {
            Player testPlayer = new Player("Ale", Wizard.Wizard1, Tower.Black, 8, 1);
            Player testPlayer2 = new Player("Fede", Wizard.Wizard1, Tower.White, 8, 1);
    
            tableManager.getCurrentIsland().setTower(Tower.White);
            assertThrows(IllegalArgumentException.class, () -> tableManager.changeControlOfCurrentIsland(testPlayer, testPlayer2));
            assertEquals(Tower.White, tableManager.getCurrentIsland().getActiveTowerType());
        });
    }

    /**
     * Tests the control change operation (Tower swap) with a Stop card placed on the Island
     */
    @Test
    void testChangeControl_WithStopCard() {
        assertDoesNotThrow(() -> {
            Player testPlayer = new Player("Ale", Wizard.Wizard1, Tower.Black, 8, 1);
            Player testPlayer2 = new Player("Fede", Wizard.Wizard1, Tower.White, 8, 1);
    
            tableManager.getCurrentIsland().setStopCard(true);
            tableManager.moveMotherNature(12);
            tableManager.getCurrentIsland().setTower(Tower.Black);
            assertThrows(IslandSkippedControlAssignmentForStopCardException.class, () -> tableManager.changeControlOfCurrentIsland(testPlayer, testPlayer2));
            assertEquals(Tower.Black, tableManager.getCurrentIsland().getActiveTowerType());
        });
    }

    /**
     * Tests the control change operation (Tower swap) with the Victory condition met at the end of the operation
     */
    @Test
    void testChangeControl_WithVictoryCondition() {
        assertDoesNotThrow(() -> {
            Player testPlayer = new Player("Ale", Wizard.Wizard1, Tower.Black, 8, 1);
            Player testPlayer2 = new Player("Fede", Wizard.Wizard1, Tower.White, 0, 1);
    
            tableManager.getCurrentIsland().setTower(testPlayer.pickAndRemoveTower());
            assertDoesNotThrow(() -> tableManager.changeControlOfCurrentIsland(testPlayer, testPlayer2));
            assertEquals(Tower.White, tableManager.getCurrentIsland().getActiveTowerType());
        });
    }

    /**
     * Tests Island unification
     */
    @Test
    void testUnificationPossible() {
        assertDoesNotThrow(() -> {
            Player testPlayer = new Player("Ale", Wizard.Wizard1, Tower.Black, 8, 1);
            Player testPlayer2 = new Player("Fede", Wizard.Wizard1, Tower.White, 8, 1);
    
            assertDoesNotThrow(() -> tableManager.getCurrentIsland().setTower(testPlayer.pickAndRemoveTower()));
            tableManager.getIslandAtIndex(TableManager.circularWrap(tableManager.getCurrentIslandIndex() + 1, 12)).setTower(Tower.White);
            assertDoesNotThrow(() -> tableManager.changeControlOfCurrentIsland(testPlayer, testPlayer2));
            assertEquals(Tower.White, tableManager.getCurrentIsland().getActiveTowerType());
            assertEquals(2, tableManager.getCurrentIsland().getTowerCount());
            assertEquals(11, tableManager.getNumberOfIslands());
        });
    }

    /**
     * Tests the Island unification which produces an end of game notification
     */
    @Test
    void testMatchEnd_IslandsCondition() {
        assertDoesNotThrow(() -> {
            Player testPlayer = new Player("Ale", Wizard.Wizard1, Tower.Black, 6, 1);
            Player testPlayer2 = new Player("Fede", Wizard.Wizard1, Tower.White, 6, 1);
            Player testPlayer3 = new Player("Leo", Wizard.Wizard1, Tower.Gray, 6, 1);
    
            assertDoesNotThrow(() -> {
                //Force the Islands to merge
                for (int i = 0; i < 4; i++) {
                    tableManager.changeControlOfCurrentIsland(null, testPlayer);
                    tableManager.moveMotherNature(1);
                }
        
                for (int i = 4; i < 8; i++) {
                    tableManager.changeControlOfCurrentIsland(null, testPlayer2);
                    tableManager.moveMotherNature(1);
                }
        
                for (int i = 8; i < 12; i++) {
                    tableManager.changeControlOfCurrentIsland(null, testPlayer3);
                    tableManager.moveMotherNature(1);
                }
            });
            assertEquals(3, tableManager.getNumberOfIslands());
        });
    }

    /**
     * Tests the match end condition caused by the Empty Bag
     */
    @Test
    void checkMatchEndEmptyBag() {
        int baseStudents = 120;
        for (int cardIdx = 0; cardIdx < 3; cardIdx++) {
            baseStudents -= tableManager.getCardAtIndex(cardIdx).getHostedStudents().getTotalCount();
        }
        int finalBaseStudents = baseStudents;
        assertDoesNotThrow(() -> {
            for (int extractIdx = 0; extractIdx < finalBaseStudents; extractIdx++) {
                tableManager.pickStudentsFromBag(1);
            }
        });
        assertTrue(tableManager.checkAndNotifyMatchEnd());
    }

    /**
     * Tests that there is no match end if the Bag is not empty
     */
    @Test
    void checkNoMatchEndFullBag() {
        assertFalse(tableManager.checkAndNotifyMatchEnd());
    }

    /**
     * Tests the CopyTo and Equals mehods
     */
    @Test
    void checkCopyAndEquals() {
        TableManager copyTable = new TableManager(3, false);
        tableManager.copyTo(copyTable);
        assertEquals(tableManager, copyTable);
        assertEquals(tableManager.hashCode(), copyTable.hashCode());
    }

    /**
     * Performs the Influence test in a variety of situations
     * @param placeTowerOfSelf Whether it should place a Tower of the same color as the Player's
     * @param placeTowerOfOtherPlayer Whether it should place a Tower of the same color as the second Player's
     * @param placeStopCard Whether it should place a StopCard on the target Island
     * @param checkCards Whether it should take into account Character cards
     * @param activatedCardIndex If it has to check the card, the index of the chosen card (which will be tested)
     */
    private void verifyInfluenceCount(boolean placeTowerOfSelf, boolean placeTowerOfOtherPlayer, boolean placeStopCard, boolean checkCards, int activatedCardIndex) {
        if (!checkCards) {
            tableManager = new TableManager(2, false);
        }

        //Test on current island
        int blueCount = tableManager.getCurrentIsland().getCount(Student.BlueUnicorn);
        int pinkCount = tableManager.getCurrentIsland().getCount(Student.PinkFair);

        tableManager.getCurrentIsland().placeStudents(Student.BlueUnicorn, 3);
        tableManager.getCurrentIsland().placeStudents(Student.RedDragon, 4);
        tableManager.getCurrentIsland().placeStudents(Student.GreenFrog, 2);
        tableManager.getCurrentIsland().placeStudents(Student.YellowElf, 1);
        tableManager.getCurrentIsland().placeStudents(Student.PinkFair, 1);

        int currentIslandIdx = tableManager.getCurrentIslandIndex();

        assertDoesNotThrow(() -> {
            Player testPlayer = new Player("Ale", Wizard.Wizard1, Tower.Black, 8, 1);
            testPlayer.addProfessor(Professor.BlueUnicorn);
            testPlayer.addProfessor(Professor.PinkFair);
    
            if (placeTowerOfSelf) {
                tableManager.getCurrentIsland().setTower(Tower.Black);
            } else if (placeTowerOfOtherPlayer) {
                tableManager.getCurrentIsland().setTower(Tower.White);
            }
            if (placeStopCard) {
                tableManager.getCurrentIsland().setStopCard(true);
            }
            if (checkCards) {
                CharacterCard pickedCard = tableManager.getCardAtIndex(activatedCardIndex);
                assertDoesNotThrow(() -> {
                    for (int i = 0; i < pickedCard.getPrice() * 3; i++) {
                        testPlayer.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
                    }
                });
                testPlayer.playCharacterCard(tableManager.getCardAtIndex(activatedCardIndex));
            }
            //Simulate MN movement here
            tableManager.moveMotherNature(12);
            if (placeStopCard) {
                assertThrows(IslandSkippedInfluenceForStopCardException.class, () -> tableManager.getInfluenceOnCurrentIsland(testPlayer));
            } else {
                assertDoesNotThrow(() -> {
                    //To set up cards that require an additional call before the influence count (usually performed by the MatchManager after a command from the Controller)
                    if (testPlayer.getActiveCharacterCard().getCharacter().getChangesInfluence()) {
                        testPlayer.getActiveCharacterCard().useCard(tableManager, List.of(testPlayer), testPlayer, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, currentIslandIdx, currentIslandIdx, CharacterCardParamSet.StopCardMovementMode.ToCard));
                    }
                    int computedInfluence = tableManager.getInfluenceOnCurrentIsland(testPlayer);
                    if (!checkCards) {
                        if (placeTowerOfSelf) {
                            assertEquals(5 + blueCount + pinkCount, computedInfluence);
                        } else {
                            assertEquals(4 + blueCount + pinkCount, computedInfluence);
                        }
                    } else {
                        //Reset the card purchase
                        testPlayer.getActiveCharacterCard().purchase();
                        //Get the card modifier if necessary
                        int modifier = 0;
                        if (testPlayer.getActiveCharacterCard().getCharacter().getChangesInfluence()) {
                            modifier = testPlayer.getActiveCharacterCard().useCard(tableManager, List.of(testPlayer), testPlayer, new CharacterCardParamSet(Student.BlueUnicorn, Student.BlueUnicorn, null, null, false, 0, currentIslandIdx, currentIslandIdx, CharacterCardParamSet.StopCardMovementMode.ToCard));
                        }
                        if (placeTowerOfSelf) {
                            assertEquals(5 + blueCount + pinkCount + modifier, computedInfluence);
                        } else {
                            assertEquals(4 + blueCount + pinkCount + modifier, computedInfluence);
                        }
                    }
                });
            }
        });
    }
}