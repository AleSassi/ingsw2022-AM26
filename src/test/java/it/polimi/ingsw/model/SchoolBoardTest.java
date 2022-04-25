package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.exceptions.model.InsufficientTowersException;
import it.polimi.ingsw.model.student.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SchoolBoardTest {
    
    private SchoolBoard board;
    
    @BeforeEach
    void initBoard() {
        assertDoesNotThrow(() -> board = new SchoolBoard(Tower.Black, 8));
    }
    
    @Test
    void testStudentAddAndRemove() {
        board.addStudentToTable(Student.BlueUnicorn);
        board.addStudentToTable(Student.BlueUnicorn);
        assertEquals(board.getCountAtTable(Student.BlueUnicorn), 2);
        assertDoesNotThrow(() -> board.removeStudentFromTable(Student.BlueUnicorn));
        assertDoesNotThrow(() -> board.removeStudentFromTable(Student.BlueUnicorn));
        assertThrows(CollectionUnderflowError.class, () -> board.removeStudentFromTable(Student.BlueUnicorn));
    }

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
            board.pickAndRemoveTower();
            assertEquals(0, board.getAvailableTowerCount());
        });
        assertThrows(InsufficientTowersException.class, board::pickAndRemoveTower);
        assertDoesNotThrow(board::gainTower);
        assertEquals(1, board.getAvailableTowerCount());
    }

    @Test
    void getControlledProfessorTest() {
        assertTrue(board.getControlledProfessors().isEmpty());
        board.setControlledProfessor(Professor.BlueUnicorn);
        assertEquals(1, board.getControlledProfessors().size());
        assertTrue(board.getControlledProfessors().contains(Professor.BlueUnicorn));
    }

    @Test
    void removeStudentFromEntranceTest() {
        board.addStudentToEntrance(Student.BlueUnicorn);
        assertDoesNotThrow(() -> board.removeStudentFromEntrance(Student.BlueUnicorn));
        assertThrows(CollectionUnderflowError.class, () -> board.removeStudentFromEntrance(Student.BlueUnicorn));
    }

    @Test
    void removeStudentFromDiningRoomTest() {
        board.addStudentToTable(Student.BlueUnicorn);
        assertDoesNotThrow(() -> board.removeStudentFromTable(Student.BlueUnicorn));
        assertThrows(CollectionUnderflowError.class, () -> board.removeStudentFromTable(Student.BlueUnicorn));
    }

    @Test
    void GetTowerType() {
        assertEquals(board.getTowerType(), Tower.Black );
    }
    
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
    
    @Test
    void testInvalidConstructor() {
    }
}


