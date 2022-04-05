package it.polimi.ingsw.model.Playertest;

import it.polimi.ingsw.model.InsufficientTowersException;
import it.polimi.ingsw.model.SchoolBoard;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.model.student.EmptyCollectionException;
import it.polimi.ingsw.model.student.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SchoolBoardTest {

    @Test
    void getCountAtTheTable() {
        SchoolBoard board=new SchoolBoard(Tower.Black);
        board.AddStudentToTable(Student.BlueUnicorn);
        board.AddStudentToTable(Student.BlueUnicorn);
        assertEquals(board.GetCountAtTheTable(Student.BlueUnicorn), 2);
        assertDoesNotThrow(() -> board.RemoveStudentTable(Student.BlueUnicorn));
        assertDoesNotThrow(() -> board.RemoveStudentTable(Student.BlueUnicorn));
        assertThrows(EmptyCollectionException.class, () -> board.RemoveStudentTable(Student.BlueUnicorn));


    }

    @Test
    void getAvaibleTowerTowers() {
        SchoolBoard board=new SchoolBoard(Tower.Black);
        int count=board.getAvaibleTowerTowers();
        board.gainTower();
        board.gainTower();
        assertEquals(count+2,board.getAvaibleTowerTowers() );


    }

    @Test
    void getControlledProfessor() {
        SchoolBoard board=new SchoolBoard(Tower.Black);
        board.getControlledProfessor();
    }



    @Test
    void removeStudentFromEntrance() {
        SchoolBoard board=new SchoolBoard(Tower.Black);
        board.addStudentToEntrance(Student.BlueUnicorn);
        assertDoesNotThrow(() -> board.RemoveStudentFromEntrance(Student.BlueUnicorn));
        assertThrows(EmptyCollectionException.class, () -> board.RemoveStudentFromEntrance(Student.BlueUnicorn));
    }


    @Test
    void pickandRemoveTower() {
        SchoolBoard board=new SchoolBoard(Tower.Black);
        try{
        assertEquals(board.PickAndRemove(), Tower.Black);}
        catch(InsufficientTowersException e){}
        assertDoesNotThrow(() -> board.PickAndRemove());
    }
}