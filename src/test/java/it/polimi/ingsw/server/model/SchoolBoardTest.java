package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.server.exceptions.model.InsufficientTowersException;
import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.SchoolBoard;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.student.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the School Board
 * @see SchoolBoard
 */
class SchoolBoardTest {
    
    private SchoolBoard board;
    
    /**
     * Common test initialization
     */
    @BeforeEach
    void initBoard() {
        assertDoesNotThrow(() -> board = new SchoolBoard(Tower.Black, 8));
    }
    
    /**
     * Tests adding a student and removing them from the board
     */
    @Test
    void testStudentAddAndRemove() {
        board.addStudentToTable(Student.BlueUnicorn);
        board.addStudentToTable(Student.BlueUnicorn);
        assertEquals(board.getCountAtTable(Student.BlueUnicorn), 2);
        assertDoesNotThrow(() -> board.removeStudentFromTable(Student.BlueUnicorn));
        assertDoesNotThrow(() -> board.removeStudentFromTable(Student.BlueUnicorn));
        assertThrows(CollectionUnderflowError.class, () -> board.removeStudentFromTable(Student.BlueUnicorn));
    }
    
    /**
     * Tests removing towers from the board and then readding them
     */
    @Test
    void testTowerRemoveAndAdd() {
        int count = board.getAvailableTowerCount();
        assertDoesNotThrow(() -> {
            board.pickAndRemoveTower();
            assertEquals(7, board.getAvailableTowerCount());
            board.pickAndRemoveTower();
            assertEquals(6, board.getAvailableTowerCount());
            board.pickAndRemoveTower();
            assertEquals(5, board.getAvailableTowerCount());
            board.pickAndRemoveTower();
            assertEquals(4, board.getAvailableTowerCount());
            board.pickAndRemoveTower();
            assertEquals(3, board.getAvailableTowerCount());
            board.pickAndRemoveTower();
            assertEquals(2, board.getAvailableTowerCount());
            board.pickAndRemoveTower();
            assertEquals(1, board.getAvailableTowerCount());
        });
        assertThrows(InsufficientTowersException.class, board::pickAndRemoveTower);
        assertDoesNotThrow(board::gainTower);
        assertEquals(2, board.getAvailableTowerCount());
    }
    
    /**
     * Tests the controlled professor getter
     */
    @Test
    void getControlledProfessorTest() {
        assertTrue(board.getControlledProfessors().isEmpty());
        board.setControlledProfessor(Professor.BlueUnicorn);
        assertEquals(1, board.getControlledProfessors().size());
        assertTrue(board.getControlledProfessors().contains(Professor.BlueUnicorn));
    }
    
    /**
     * tests the removal of a student from the entrance
     */
    @Test
    void removeStudentFromEntranceTest() {
        board.addStudentToEntrance(Student.BlueUnicorn);
        assertDoesNotThrow(() -> board.removeStudentFromEntrance(Student.BlueUnicorn));
        assertThrows(CollectionUnderflowError.class, () -> board.removeStudentFromEntrance(Student.BlueUnicorn));
    }
    
    /**
     * Tests the removal pof a student from th edining room
     */
    @Test
    void removeStudentFromDiningRoomTest() {
        board.addStudentToTable(Student.BlueUnicorn);
        assertDoesNotThrow(() -> board.removeStudentFromTable(Student.BlueUnicorn));
        assertThrows(CollectionUnderflowError.class, () -> board.removeStudentFromTable(Student.BlueUnicorn));
    }
    
    /**
     * Tests the tower type getter
     */
    @Test
    void GetTowerType() {
        assertEquals(board.getTowerType(), Tower.Black );
    }
    
    /**
     * tests the methods with null parameters
     */
    @Test
    void testNullParams() {
        assertDoesNotThrow(() -> {
            assertEquals(0, board.getCountAtTable(null));
            board.setControlledProfessor(null);
            assertTrue(board.getControlledProfessors().isEmpty());
            board.removeStudentFromTable(null);
            board.removeStudentFromEntrance(null);
            board.addStudentToEntrance(null);
            board.addStudentToTable(null);
        });
    }
}


