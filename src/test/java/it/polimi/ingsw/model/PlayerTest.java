package it.polimi.ingsw.model;

import it.polimi.ingsw.model.InsufficientTowersException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Professor;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.model.assistants.AssistantCard;
import it.polimi.ingsw.model.assistants.AvailableCardsDeck;
import it.polimi.ingsw.model.assistants.Wizard;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.CharacterCard;
import it.polimi.ingsw.model.characters.StopCardActivatorCard;
import it.polimi.ingsw.model.student.EmptyCollectionException;
import it.polimi.ingsw.model.student.Student;
import it.polimi.ingsw.model.student.StudentCollection;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void getNickname() {
        Player test = new Player("giovanni", Wizard.Wizard1, Tower.Black, 8 );
        String name = test.getNickname();
        assertEquals(name, "giovanni");
    }

    @Test
    void getAvailableAssistantCards() {
        Player test = new Player("giovanni", Wizard.Wizard1, Tower.Black, 8 );
        ArrayList<AssistantCard> myCards = test.getAvailableAssistantCards();
        AvailableCardsDeck testDeck = new AvailableCardsDeck();
        int size = myCards.size();
        for (int i = 0; i < size; i++){
            assertEquals(myCards.get(i), testDeck.getCard(i) );
        }
    }

    @Test
    void getLastPlayedAssistantCard() {
        Player test = new Player("giovanni", Wizard.Wizard1, Tower.Black, 8 );
        AvailableCardsDeck testDeck = new AvailableCardsDeck();
        AssistantCard card = testDeck.getCard(3);
        test.playAssistantCardAtIndex(3);
        assertEquals(test.getLastPlayedAssistantCard(), card);
        ArrayList<AssistantCard> newDeck = test.getAvailableAssistantCards();
        assertFalse(newDeck.contains(card));
    }

    @Test
    void getControlledProfessors() {
        Player test=new Player("giovanni", Wizard.Wizard1, Tower.Black, 8 );
        test.addProfessor(Professor.GreenFrog);
        test.addProfessor(Professor.GreenFrog);
        test.addProfessor(Professor.BlueUnicorn);
        test.removeProfessor(Professor.BlueUnicorn);
        ArrayList<Professor> prof=test.getControlledProfessors();
        assertTrue(prof.contains(Professor.GreenFrog));
        assertNotEquals(true, prof.contains(Professor.BlueUnicorn));
    }

    @Test

    void getCountAtTableAndPlaceStudent() {
        Player test=new Player("giovanni", Wizard.Wizard1, Tower.Black, 8 );
        test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn);
        test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn);
        test.placeStudentAtTableAndGetCoin(Student.BlueUnicorn);
        assertEquals(test.getCountAtTable(Student.BlueUnicorn), 3);
        test.placeStudentAtTableAndGetCoin(Student.GreenFrog);
        assertEquals(test.getCountAtTable(Student.GreenFrog), 1);

    }

    @Test
    void CharacterCardmethod() {
        Player test=new Player("giovanni", Wizard.Wizard1, Tower.Black, 8 );
        CharacterCard testcard=new StopCardActivatorCard(Character.Herbalist);
        test.playCharacterCard(testcard);
        CharacterCard testedcard=test.getActiveCharacterCard();
        assertEquals(testcard, testedcard);
        test.deactivateCard();
        testedcard=test.getActiveCharacterCard();
        assertNotEquals(testedcard,testcard);

    }



    @Test
    void addAndRemoveStudentToEntrance() {
        Player test=new Player("giovanni", Wizard.Wizard1, Tower.Black, 8 );
        test.addStudentToEntrance(Student.BlueUnicorn);
        test.addStudentToEntrance(Student.BlueUnicorn);
        assertDoesNotThrow(()->{
            test.removeStudentFromEntrance(Student.BlueUnicorn);
            test.removeStudentFromEntrance(Student.BlueUnicorn);
        });
        assertThrows(EmptyCollectionException.class, ()->{
            test.removeStudentFromEntrance(Student.BlueUnicorn);
        });


    }






    @Test
    void addandRemoveProfessor() {
        Player test=new Player("giovanni", Wizard.Wizard1, Tower.Black, 8 );
        test.addProfessor(Professor.GreenFrog);
        test.addProfessor(Professor.GreenFrog);
        assertDoesNotThrow(()->{
            test.removeProfessor(Professor.GreenFrog);
            test.removeProfessor(Professor.GreenFrog);
        });
        assertThrows(EmptyCollectionException.class, ()->{
            test.removeProfessor(Professor.GreenFrog);
        });

    }



    @Test
    void getTowerType() {
        Player test=new Player("giovanni", Wizard.Wizard1, Tower.Black, 8 );
        assertEquals(test.getTowerType(), Tower.Black);
    }

    @Test
    void gainTower() {
        Player test=new Player("giovanni", Wizard.Wizard1, Tower.Black, 8 );
        test.gainTower();
        test.gainTower();
        test.gainTower();
        test.gainTower();

    }

    @Test
    void pickAndRemoveTower() {
        Player test=new Player("giovanni", Wizard.Wizard1, Tower.Black, 1 );
        assertDoesNotThrow(()->{
            Tower Towe=test.pickAndRemoveTower();
            assertEquals(Towe,Tower.Black );
        });
        assertThrows(InsufficientTowersException.class, ()->{
            Tower Towe=test.pickAndRemoveTower();
            assertEquals(Towe,Tower.Black );
        });
    }

    @Test
    void addAllStudentsToEntrance() {
        Player test=new Player("giovanni", Wizard.Wizard1, Tower.Black, 1 );
        StudentCollection sc=new StudentCollection();
        sc.addStudents(Student.BlueUnicorn, 1);
        sc.addStudents(Student.GreenFrog, 1);
        test.addAllStudentsToEntrance(sc);
        assertDoesNotThrow(()->{
            test.removeStudentFromEntrance(Student.BlueUnicorn);
            test.removeStudentFromEntrance(Student.GreenFrog);
        });
        assertThrows(EmptyCollectionException.class, ()->{
            test.removeStudentFromEntrance(Student.BlueUnicorn);
        });
    }



    @Test
    void notifyVictory() {
        Player test=new Player("giovanni", Wizard.Wizard1, Tower.Black, 1 );
        test.notifyVictory();
    }
}