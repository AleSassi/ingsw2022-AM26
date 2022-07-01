package it.polimi.ingsw.server.model.student;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.student.Island;
import it.polimi.ingsw.server.model.student.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class {@code IslandTest } tests {@link it.polimi.ingsw.server.model.student.Island Island}
 * Coverage 100%
 *
 * @author Federico Albertini
 * @see Island
 */
class IslandTest {

    private Island island;
    
    /**
     * Test initialization
     */
    @BeforeEach
    void initIsland() {
        island = new Island();
    }

    /**
     * Tests that {@code itHasStopCard} returns true if StopCard is present on an {@link it.polimi.ingsw.server.model.student.Island Island}
     */
    @Test
    void itHasStopCardTest() {
        island.setStopCard(true);
        assertTrue(island.itHasStopCard());
    }

    /**
     * Tests that {@code getActiveTowerType} returns the active {@link it.polimi.ingsw.server.model.Tower Tower} on {@link it.polimi.ingsw.server.model.student.Island Island}
     */
    @Test
    void getActiveTowerTypeTest() {
        for (Tower t : Tower.values()) {
            island.setTower(t);
            assertEquals(t, island.getActiveTowerType());
        }
    }

    /**
     * Tests that {@code getTowerCount} returns the total count of the {@link it.polimi.ingsw.server.model.Tower Tower} on {@link it.polimi.ingsw.server.model.student.Island Island}
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
     * Tests that {@code isMotherNaturePresent} returns true if MotherNature is present on the {@link it.polimi.ingsw.server.model.student.Island Island}
     */
    @Test
    void isMotherNaturePresentTest() {
        island.setMotherNaturePresent(true);
        assertTrue(island.isMotherNaturePresent());
    }

    /**
     * Tests that {@code setStopCard} sets properly the stopCard on the {@link it.polimi.ingsw.server.model.student.Island Island}
     */
    @Test
    void setStopCardTest() {
        island.setStopCard(true);
        assertTrue(island.itHasStopCard());
    }

    /**
     * Tests that {@code getInfluence} returns the correct influence of a {@link it.polimi.ingsw.server.model.Player Player} on this {@link it.polimi.ingsw.server.model.student.Island Island}
     */
    @Test
    void getInfluenceTest() {
        assertDoesNotThrow(() -> {
            Player p1 = new Player("Ale", Wizard.Wizard1, Tower.Black, 6, 1);
            Player p2 = new Player("Leo", Wizard.Wizard2, Tower.Gray, 6, 1);
            island.placeStudents(Student.YellowElf, 2);
            island.placeStudents(Student.BlueUnicorn, 3);
            island.placeStudents(Student.GreenFrog, 2);
            island.setTower(Tower.Black);
            p1.addProfessor(Professor.YellowElf);
            p1.addProfessor(Professor.BlueUnicorn);
            p2.addProfessor(Professor.GreenFrog);
    
            assertEquals(6, island.getInfluence(p1));
            assertEquals(2, island.getInfluence(p2));
        });

    }

    /**
     * Tests that {@code isUnifiableWith} returns true if two {@link it.polimi.ingsw.server.model.student.Island Islands} can be unified
     */
    @Test
    void isUnifiableWithTest() {
        Island island2 = new Island();
        island.setTower(Tower.Black);
        island2.setTower(Tower.Black);
        assertTrue(island.isUnifiableWith(island2));
    }

    /**
     * Tests that {@code acquireIsland} unifies two {@link it.polimi.ingsw.server.model.student.Island Islands} correctly
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
     * Tests that {@code getNumberOfSameStudents} returns the number of the {@link it.polimi.ingsw.server.model.student.Student Students} type asked
     */
    @Test
    void getNumberOfSameStudentsTest() {
        for (Student s : Student.values()) {
            island.placeStudents(s, 10);
            assertEquals(island.getNumberOfSameStudents(s), 10);
        }
    }

    /**
     * Tests that {@code setTower} sets the {@link it.polimi.ingsw.server.model.Tower Tower} type correctly
     */
    @Test
    void setTowerTest() {
        island.setTower(Tower.Black);
        assertEquals(island.getActiveTowerType(), Tower.Black);
    }

    /**
     * Tests that {@code setMotherNaturePresent} sets MotherNature presence correctly
     */
    @Test
    void setMotherNaturePresentTest() {
        island.setMotherNaturePresent(true);
        assertTrue(island.isMotherNaturePresent());
    }

    /**
     * Tests that {@code mergeHostedStudentWith} merges two {@link it.polimi.ingsw.server.model.student.Island Island} together correctly
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
    
    /**
     * Tests methods with null parameters
     */
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