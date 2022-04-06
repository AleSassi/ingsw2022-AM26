package it.polimi.ingsw.model.student;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Professor;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.model.assistants.Wizard;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class IslandTest test Island
 * Coverage 100%
 *
 * @author Federico Albertini
 * @see Island
 */
class IslandTest {

    private Island island;

    @BeforeEach
    void initIsland() {
        island = new Island();
    }

    /**
     * Method itHasStopCardTest tests that can return true if StopCard is present on an island
     */
    @Test
    void itHasStopCardTest() {
        island.setStopCard(true);
        assertTrue(island.itHasStopCard());
    }

    /**
     * Method getActiveTowerTypeTest tests that can return the active tower on island
     */
    @Test
    void getActiveTowerTypeTest() {
        for (Tower t : Tower.values()) {
            island.setTower(t);
            assertEquals(t, island.getActiveTowerType());
        }
    }

    /**
     * Method getTowerCountTest tests that can return the total count of the Tower on an island
     */
    @Test
    void getTowerCountTest() {
        Island island2 = new Island();
        island.setTower(Tower.Black);
        assertEquals(1, island.getTowerCount());
        island2.setTower(Tower.Black);
        island.acquireIsland(island2);
        assertEquals(2, island.getTowerCount());
    }

    /**
     * Method isMotherNaturePresentTest tests that can return true if MotherNature is present on the island
     */
    @Test
    void isMotherNaturePresentTest() {
        island.setMotherNaturePresent(true);
        assertTrue(island.isMotherNaturePresent());
    }

    /**
     * Method setStopCardTest tests that can set properly the stopCard on the island
     */
    @Test
    void setStopCardTest() {
        island.setStopCard(true);
        assertTrue(island.itHasStopCard());
    }

    /**
     * Method getInfluenceTest tests that can return the correct influence of a player on this island
     */
    @Test
    void getInfluenceTest() {
        Player p1 = new Player("Ale", Wizard.Wizard1, Tower.Black, 6);
        Player p2 = new Player("Leo", Wizard.Wizard2, Tower.Gray, 6);
        island.placeStudents(Student.YellowElf, 2);
        island.placeStudents(Student.BlueUnicorn, 3);
        island.placeStudents(Student.GreenFrog, 2);
        island.setTower(Tower.Black);
        p1.addProfessor(Professor.YellowElf);
        p1.addProfessor(Professor.BlueUnicorn);
        p2.addProfessor(Professor.GreenFrog);

        assertEquals(6, island.getInfluence(p1));
        assertEquals(2, island.getInfluence(p2));

    }

    /**
     * Method isUnifiableWithTest tests that can return true if two islands can be unified
     */
    @Test
    void isUnifiableWithTest() {
        Island island2 = new Island();
        island.setTower(Tower.Black);
        island2.setTower(Tower.Black);
        assertTrue(island.isUnifiableWith(island2));
    }

    /**
     * Method acquireIslandTest tests that can unifies two island correctly
     */
    @Test
    void acquireIslandTest() {
        Island island2 = new Island();
        for (Student s : Student.values()) {
            island.placeStudents(s, 10);
            island2.placeStudents(s, 5);
        }
        island.setMotherNaturePresent(true);
        island.setTower(Tower.Black);
        island2.setTower(Tower.Black);
        island.acquireIsland(island2);
        assertTrue(island.isMotherNaturePresent());
        assertEquals(island.getTowerCount(), 2);
        assertEquals(island.getActiveTowerType(), Tower.Black);
        for (Student s : Student.values()) {
            assertEquals(island.getNumberOfSameStudents(s), 15);
        }
    }

    /**
     * Method getNumberOfSameStudentsTest test that can return the number of the student type asked
     */
    @Test
    void getNumberOfSameStudentsTest() {
        for (Student s : Student.values()) {
            island.placeStudents(s, 10);
            assertEquals(island.getNumberOfSameStudents(s), 10);
        }
    }

    /**
     * Method setTowerTest test that can set the tower type correctly
     */
    @Test
    void setTowerTest() {
        island.setTower(Tower.Black);
        assertEquals(island.getActiveTowerType(), Tower.Black);
    }

    /**
     * Method setMotherNaturePresentTest test that can set MotherNature presence correctly
     */
    @Test
    void setMotherNaturePresentTest() {
        island.setMotherNaturePresent(true);
        assertTrue(island.isMotherNaturePresent());
    }

    /**
     * Method mergeHostedStudentWithTest test that can merge two HostedStudent together correctly
     */
    @Test
    void mergeHostedStudentWithTest() {
        Island island1 = new Island();
        for(Student s: Student.values()){
            island.placeStudents(s, 5);
            island1.placeStudents(s, 10);
        }
        island.mergeHostedStudentWith(island1);

        for(Student s: Student.values()){
            assertEquals(island.getCount(s), 15);
        }
    }

    @Test
    void nullTest() {
        assertEquals(0, island.getInfluence(null));
        assertFalse(island.isUnifiableWith(null));
        island.acquireIsland(null);
        assertEquals(0, island.getNumberOfSameStudents(null));
        island.mergeHostedStudentWith(null);
        island.setTower(null);
    }
}