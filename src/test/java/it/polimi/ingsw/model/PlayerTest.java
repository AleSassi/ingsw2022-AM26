package it.polimi.ingsw.model;

import it.polimi.ingsw.model.assistants.*;
import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.student.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void getNicknameTest() {
        Player test = new Player("giovanni", Wizard.Wizard1, Tower.Black, 8);
        assertEquals("giovanni", test.getNickname());
    }

    @Test
    void testAvailableAssistantCards() {
        Player test = new Player("giovanni", Wizard.Wizard1, Tower.Black, 8);
        ArrayList<AssistantCard> myCards = test.getAvailableAssistantCards();
        AvailableCardsDeck testDeck = new AvailableCardsDeck();
        int size = myCards.size();
        for (int i = 0; i < size; i++) {
            assertEquals(myCards.get(i), testDeck.getCard(i));
        }
    }

    @Test
    void getLastPlayedAssistantCardTest() {
        Player test = new Player("giovanni", Wizard.Wizard1, Tower.Black, 8);
        AvailableCardsDeck testDeck = new AvailableCardsDeck();
        AssistantCard card = testDeck.getCard(3);
        test.playAssistantCardAtIndex(3);
        assertEquals(test.getLastPlayedAssistantCard(), card);
        ArrayList<AssistantCard> newDeck = test.getAvailableAssistantCards();
        assertFalse(newDeck.contains(card));
    }

    @Test
    void getControlledProfessorsTest() {
        Player test = new Player("giovanni", Wizard.Wizard1, Tower.Black, 8);
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

    @Test
    void getCountAtTableAndPlaceStudent() {
        Player test = new Player("giovanni", Wizard.Wizard1, Tower.Black, 8);
        test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn);
        test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn);
        test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn);
        assertEquals(test.getCountAtTable(Student.BlueUnicorn), 3);
        test.placeStudentAtTableAndGetCoin(Student.GreenFrog);
        assertEquals(test.getCountAtTable(Student.GreenFrog), 1);
        assertEquals(test.getCountAtTable(Student.BlueUnicorn), 3);
    }

    @Test
    void testCharacterCard() {
        Player test = new Player("giovanni", Wizard.Wizard1, Tower.Black, 8);
        test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn);
        test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn);
        test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn);
        test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn);
        test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn);
        test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn);
        CharacterCard testcard = new StopCardActivatorCard(Character.Herbalist);
        test.playCharacterCard(testcard);
        CharacterCard testedcard = test.getActiveCharacterCard();
        assertEquals(testcard, testedcard);
        test.deactivateCard();
        assertNull(test.getActiveCharacterCard());
    }


    @Test
    void addAndRemoveStudentToEntrance() {
        Player test = new Player("giovanni", Wizard.Wizard1, Tower.Black, 8);
        test.addStudentToEntrance(Student.BlueUnicorn);
        test.addStudentToEntrance(Student.BlueUnicorn);
        assertDoesNotThrow(() -> {
            test.removeStudentFromEntrance(Student.BlueUnicorn);
            test.removeStudentFromEntrance(Student.BlueUnicorn);
        });
        assertThrows(EmptyCollectionException.class, () -> test.removeStudentFromEntrance(Student.BlueUnicorn));
    }


    @Test
    void addRemoveProfessor() {
        Player test = new Player("giovanni", Wizard.Wizard1, Tower.Black, 8);
        test.addProfessor(Professor.GreenFrog);
        test.addProfessor(Professor.GreenFrog);
        test.removeProfessor(Professor.GreenFrog);
        assertFalse(test.getControlledProfessors().contains(Professor.GreenFrog));
    }


    @Test
    void getTowerType() {
        Player test = new Player("giovanni", Wizard.Wizard1, Tower.Black, 8);
        assertEquals(test.getTowerType(), Tower.Black);
    }

    @Test
    void testTowerPickAndRemove() {
        Player test = new Player("giovanni", Wizard.Wizard1, Tower.Black, 8);
        assertThrows(TooManyTowersException.class, test::gainTower);
        assertDoesNotThrow(() -> {
            assertEquals(Tower.Black, test.pickAndRemoveTower());
            assertEquals(Tower.Black, test.pickAndRemoveTower());
        });
        assertEquals(6, test.getAvailableTowerCount());
    }

    @Test
    void addAllStudentsToEntrance() {
        Player test = new Player("giovanni", Wizard.Wizard1, Tower.Black, 1);
        StudentCollection sc = new StudentCollection();
        sc.addStudents(Student.BlueUnicorn, 1);
        sc.addStudents(Student.GreenFrog, 1);
        test.addAllStudentsToEntrance(sc);
        assertThrows(EmptyCollectionException.class, () -> test.removeStudentFromEntrance(Student.RedDragon));
        assertDoesNotThrow(() -> {
            test.removeStudentFromEntrance(Student.BlueUnicorn);
            test.removeStudentFromEntrance(Student.GreenFrog);
        });
        assertThrows(EmptyCollectionException.class, () -> test.removeStudentFromEntrance(Student.BlueUnicorn));
    }
}