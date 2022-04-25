package it.polimi.ingsw.model.student;

import it.polimi.ingsw.exceptions.model.CollectionUnderflowError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class StudentHostTest test StudentHost
 * Coverage 100%
 *
 * @author Federico Albertini
 * @see StudentHost
 */
class StudentHostTest {

    /**
     * Method getCountTest test that can return the count of student of the same type
     */
    @Test
    void getCountTest() {
        StudentHost host = new StudentHost();
        for(Student s: Student.values()){
            host.placeStudents(s, 10);
            assertEquals(host.getCount(s), 10);
        }
    }

    /**
     * Method isEmptyTest test that can return true if the StudentHost is empty
     */
    @Test
    void isEmptyTest() {
        StudentHost host = new StudentHost();
        assertTrue(host.isEmpty());
    }

    /**
     * Method placeStudentsTest test that can place student of a given type and number correctly
     */
    @Test
    void placeStudentsTest() {
        StudentHost host = new StudentHost();
        for(Student s: Student.values()){
            host.placeStudents(s, 10);
            assertEquals(host.getCount(s), 10);
        }
    }

    /**
     * Method removeStudentTest test that can remove student of a given type and number correctly
     */
    @Test
    void removeStudentTest() {
        StudentHost host = new StudentHost();
        assertDoesNotThrow(() -> {
            for(Student s: Student.values()){
                host.placeStudents(s, 10);
                host.removeStudents(s, 10);
                assertEquals(host.getCount(s), 0);
            }
        });
    }

    /**
     * Method removeRandomTest test that can remove a random student from a StudentHost
     */
    @Test
    void removeRandomTest() {
        StudentHost host = new StudentHost();
        for(Student s: Student.values()){
            host.placeStudents(s, 10);
        }
        assertDoesNotThrow(() -> {
            host.removeRandom();
        });
        int count = 0;
        for(Student s: Student.values()){
            count += host.getCount(s);
        }
        assertEquals(count, 49);


    }

    @Test
    void underflowTest() {
        StudentHost studentHost = new StudentHost();
        studentHost.placeStudents(Student.BlueUnicorn, 10);
        studentHost.placeStudents(Student.RedDragon, 5);
        studentHost.placeStudents(Student.GreenFrog, -1);
        assertEquals(0, studentHost.getCount(Student.GreenFrog));
        assertThrows(CollectionUnderflowError.class, () -> studentHost.removeStudents(Student.BlueUnicorn, 11));
        assertThrows(CollectionUnderflowError.class, () -> studentHost.removeStudents(Student.GreenFrog, 1));
        assertDoesNotThrow(() -> studentHost.removeStudents(Student.PinkFair, 0));
    }

    @Test
    void nullTest() {
        StudentHost studentHost = new StudentHost();
        studentHost.placeStudents(null, 20);
        assertDoesNotThrow(() -> studentHost.removeStudents(null, 10));
        studentHost.getCount(null);
    }
}