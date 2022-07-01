package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.server.exceptions.model.TableFullException;
import it.polimi.ingsw.server.exceptions.model.TooManyTowersException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.assistants.AvailableCardsDeck;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.characters.CharacterCard;
import it.polimi.ingsw.server.model.characters.StopCardActivatorCard;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to the Player
 * @see Player
 */
class PlayerTest {
    
    private Player test;
    /**
     * try the constructor
     */
    @BeforeEach
    void initPlayer() {
        assertDoesNotThrow(() -> {
            test = new Player("giovanni", Wizard.Wizard1, Tower.Black, 8, 1);
        });
    }

    /**
     * try the getter of nickname
     */
    @Test
    void getNicknameTest() {
        assertEquals("giovanni", test.getNickname());
    }

    /**
     * verify tha all the card {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard} are in the {@link it.polimi.ingsw.server.model.assistants.AvailableCardsDeck AvailableCardsDeck}
     */
    @Test
    void testAvailableAssistantCards() {
        ArrayList<AssistantCard> myCards = test.getAvailableAssistantCards();
        AvailableCardsDeck testDeck = new AvailableCardsDeck();
        int size = myCards.size();
        for (int i = 0; i < size; i++) {
            assertEquals(myCards.get(i), testDeck.getCard(i));
        }
    }
    /**
     * verify that the last played card {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard} of {@link it.polimi.ingsw.server.model.assistants.AvailableCardsDeck AvailableCardsDeck} is correct
     */
    @Test
    void getLastPlayedAssistantCardTest() {
        AvailableCardsDeck testDeck = new AvailableCardsDeck();
        AssistantCard card = testDeck.getCard(3);
        assertDoesNotThrow(() -> test.playAssistantCardAtIndex(3));
        assertEquals(test.getLastPlayedAssistantCard(), card);
        ArrayList<AssistantCard> newDeck = test.getAvailableAssistantCards();
        assertFalse(newDeck.contains(card));
    }
    /**
     * try to add some professor {@link it.polimi.ingsw.server.model.Professor Professor} and than verify that (@Code professor)are effectively in the list
     */
    @Test
    void getControlledProfessorsTest() {
        test.addProfessor(Professor.GreenFrog);
        test.addProfessor(Professor.GreenFrog);
        test.addProfessor(Professor.BlueUnicorn);
        test.removeProfessor(Professor.BlueUnicorn);
        ArrayList<Professor> prof = test.getControlledProfessors();
        assertTrue(prof.contains(Professor.GreenFrog));
        assertFalse(prof.contains(Professor.BlueUnicorn));
        assertFalse(prof.contains(Professor.PinkFair));
        assertFalse(prof.contains(Professor.RedDragon));
        assertFalse(prof.contains(Professor.YellowElf));
    }
    
    /**
     * try to add some student  {@link it.polimi.ingsw.server.model.student.Student Student} and then verify that method (@Code getCountAtTable) return the correct number of (@code Student) inserted
     *
     */
    @Test
    void getCountAtTableAndPlaceStudent() {
        TableManager tableManager = new TableManager(2, false);
        assertDoesNotThrow(() -> {
            test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
            test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
            test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
            assertEquals(test.getCountAtTable(Student.BlueUnicorn), 3);
            test.placeStudentAtTableAndGetCoin(Student.GreenFrog, tableManager);
            assertEquals(test.getCountAtTable(Student.GreenFrog), 1);
            assertEquals(test.getCountAtTable(Student.BlueUnicorn), 3);
        });
    }
    /**
     * try to create, play and then deactivate a character card  {@link it.polimi.ingsw.server.model.characters.CharacterCard Character} and  verify that method does not throw exception
     */
    @Test
    void testCharacterCard() {
        TableManager tableManager = new TableManager(2, false);
        assertDoesNotThrow(() -> {
            test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
            test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
            test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
            test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
            test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
            test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
        });
        CharacterCard testcard = new StopCardActivatorCard(Character.Herbalist);
        test.playCharacterCard(testcard);
        CharacterCard testedcard = test.getActiveCharacterCard();
        assertEquals(testcard, testedcard);
        test.deactivateCard();
        assertNull(test.getActiveCharacterCard());
    }

    /**
     * try to add and remove some student  {@link it.polimi.ingsw.server.model.student.Student Student}from entrance of {@link it.polimi.ingsw.server.model.student.Student Student} and verify does not throw exception
     */
    @Test
    void addAndRemoveStudentToEntrance() {
        test.addStudentToEntrance(Student.BlueUnicorn);
        test.addStudentToEntrance(Student.BlueUnicorn);
        assertDoesNotThrow(() -> {
            test.removeStudentFromEntrance(Student.BlueUnicorn);
            test.removeStudentFromEntrance(Student.BlueUnicorn);
        });
        assertThrows(CollectionUnderflowError.class, () -> test.removeStudentFromEntrance(Student.BlueUnicorn));
    }

    /**
     * try to add and remove some Professors {@link it.polimi.ingsw.server.model.Professor Professor} the controlled
     */
    @Test
    void addRemoveProfessor() {
        test.addProfessor(Professor.GreenFrog);
        test.addProfessor(Professor.GreenFrog);
        test.removeProfessor(Professor.GreenFrog);
        assertFalse(test.getControlledProfessors().contains(Professor.GreenFrog));
    }

    /**
     * try the getter of Tower {@link it.polimi.ingsw.server.model.Tower Tower}
     */
    @Test
    void getTowerType() {
        assertEquals(test.getTowerType(), Tower.Black);
    }
    
    /**
     * try to take some Tower {@link it.polimi.ingsw.server.model.Tower Tower} and verify the remaining counter is correct
     */
    @Test
    void testTowerPickAndRemove() {
        assertThrows(TooManyTowersException.class, test::gainTower);
        assertDoesNotThrow(() -> {
            assertEquals(Tower.Black, test.pickAndRemoveTower());
            assertEquals(Tower.Black, test.pickAndRemoveTower());
        });
        assertEquals(6, test.getAvailableTowerCount());
    }
    
    /**
     * try to add and remove a studentcollection  {@link it.polimi.ingsw.server.model.student.StudentCollection Student}to the entrance of {@link it.polimi.ingsw.server.model.student.Student Student} and verify  throw exception only when entrance is empty
     */
    @Test
    void addAllStudentsToEntrance() {
        StudentCollection sc = new StudentCollection();
        sc.addStudents(Student.BlueUnicorn, 1);
        sc.addStudents(Student.GreenFrog, 1);
        test.addAllStudentsToEntrance(sc);
        assertThrows(CollectionUnderflowError.class, () -> test.removeStudentFromEntrance(Student.RedDragon));
        assertDoesNotThrow(() -> {
            test.removeStudentFromEntrance(Student.BlueUnicorn);
            test.removeStudentFromEntrance(Student.GreenFrog);
        });
        assertThrows(CollectionUnderflowError.class, () -> test.removeStudentFromEntrance(Student.BlueUnicorn));
    }
    
    /**
     * verify that if player add more than 10 Student {@link it.polimi.ingsw.server.model.student.Student Student}to entrance of {@link it.polimi.ingsw.server.model.student.Student Student} it throws TableFullException
     */
    @Test
    void testMoreThan10StudentsAtTable() {
        TableManager tableManager = new TableManager(2, false);
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
            }
        });
        assertThrows(TableFullException.class, () -> test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager));
    }

    /**
     * verify that if player try to play a (@code card) when the deck {@link it.polimi.ingsw.server.model.assistants.PlayedCardDeck Deck} is empty it throws CollectionUnderflowError
     */
    @Test
    void testEmptyAssistantCardDeck() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                test.playAssistantCardAtIndex(0);
            }
        });
        assertThrows(CollectionUnderflowError.class, () -> test.playAssistantCardAtIndex(0));
    }
    
    /**
     * try to keep add  student  {@link it.polimi.ingsw.server.model.student.Student Student}to dining of {@link it.polimi.ingsw.server.model.student.Student Student} until whe reach the max number of coin and verify than we no more obtain coin
     */
    @Test
    void testReachedMaxCollectableCoins() {
        TableManager tableManager = new TableManager(2, false);
        //Must manually pick 2 coins from the Table, as this pick is delegated to the MatchManager when initializing the Match (coins are not given out if the variant is Basic)
        tableManager.getCoinFromReserve();
        tableManager.getCoinFromReserve();
        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> {
                test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
            });
        }
        assertEquals(4, test.getAvailableCoins());
        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> {
                test.placeStudentAtTableAndGetCoin(Student.GreenFrog, tableManager);
            });
        }
        assertEquals(7, test.getAvailableCoins());
        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> {
                test.placeStudentAtTableAndGetCoin(Student.RedDragon, tableManager);
            });
        }
        assertEquals(10, test.getAvailableCoins());
        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> {
                test.placeStudentAtTableAndGetCoin(Student.PinkFair, tableManager);
            });
        }
        assertEquals(13, test.getAvailableCoins());
        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> {
                test.placeStudentAtTableAndGetCoin(Student.YellowElf, tableManager);
            });
        }
        assertEquals(16, test.getAvailableCoins());
        assertDoesNotThrow(() -> {
            Player test2 = new Player("Test2", Wizard.Wizard2, Tower.White, 8, 1);
            for (int i = 0; i < 10; i++) {
                assertDoesNotThrow(() -> {
                    test2.placeStudentAtTableAndGetCoin(Student.GreenFrog, tableManager);
                });
            }
            assertEquals(16, test.getAvailableCoins());
            assertEquals(4, test2.getAvailableCoins());
            for (int i = 0; i < 10; i++) {
                assertDoesNotThrow(() -> {
                    test2.placeStudentAtTableAndGetCoin(Student.BlueUnicorn, tableManager);
                });
            }
            //No new coins will be given
            assertEquals(16, test.getAvailableCoins());
            assertEquals(4, test2.getAvailableCoins());
        });
    }
    
    /**try use null parameter with method and verify no exception are thrown*/
    @Test
    void nullTest() {
        assertDoesNotThrow(() -> {
            test.removeStudentFromTable(null);
            test.addAllStudentsToEntrance(null);
            test.deactivateCard();
            test.addProfessor(null);
            test.removeProfessor(null);
            test.getCountAtTable(null);
            test.playCharacterCard(null);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> test.playAssistantCardAtIndex(-1));
    }
}