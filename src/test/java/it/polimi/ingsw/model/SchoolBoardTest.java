package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.CollectionUnderflowError;
import it.polimi.ingsw.exceptions.InsufficientTowersException;
import it.polimi.ingsw.model.student.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SchoolBoardTest {

    @Test
    void testStudentAddAndRemove() {
        SchoolBoard board = new SchoolBoard(Tower.Black, 8);
        board.addStudentToTable(Student.BlueUnicorn);
        board.addStudentToTable(Student.BlueUnicorn);
        assertEquals(board.getCountAtTable(Student.BlueUnicorn), 2);
        assertDoesNotThrow(() -> board.removeStudentFromTable(Student.BlueUnicorn));
        assertDoesNotThrow(() -> board.removeStudentFromTable(Student.BlueUnicorn));
        assertThrows(CollectionUnderflowError.class, () -> board.removeStudentFromTable(Student.BlueUnicorn));
    }

    @Test
    void testTowerRemoveAndAdd() {
        SchoolBoard board = new SchoolBoard(Tower.Black, 6);
        int count = board.getAvailableTowerCount();
        assertDoesNotThrow(() -> {
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
        SchoolBoard board = new SchoolBoard(Tower.Black, 8);
        assertTrue(board.getControlledProfessors().isEmpty());
        board.setControlledProfessor(Professor.BlueUnicorn);
        assertEquals(1, board.getControlledProfessors().size());
        assertTrue(board.getControlledProfessors().contains(Professor.BlueUnicorn));
    }

    @Test
    void removeStudentFromEntranceTest() {
        SchoolBoard board = new SchoolBoard(Tower.Black, 8);
        board.addStudentToEntrance(Student.BlueUnicorn);
        assertDoesNotThrow(() -> board.removeStudentFromEntrance(Student.BlueUnicorn));
        assertThrows(CollectionUnderflowError.class, () -> board.removeStudentFromEntrance(Student.BlueUnicorn));
    }

    @Test
    void removeStudentFromDiningRoomTest() {
        SchoolBoard board = new SchoolBoard(Tower.Black, 8);
        board.addStudentToTable(Student.BlueUnicorn);
        assertDoesNotThrow(() -> board.removeStudentFromTable(Student.BlueUnicorn));
        assertThrows(CollectionUnderflowError.class, () -> board.removeStudentFromTable(Student.BlueUnicorn));
    }

    @Test
    void GetTowerType() {
        SchoolBoard board = new SchoolBoard(Tower.Black, 8);
        assertEquals(board.getTowerType(), Tower.Black );
    }



}


