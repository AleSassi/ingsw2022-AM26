package it.polimi.ingsw.server.model.student;

import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentHost;
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
     * Tests that {@code getCount} returns the count of {@link it.polimi.ingsw.server.model.student.Student Student} of the same type
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
     * Tests that {@code isEmpty} returns true if the {@link it.polimi.ingsw.server.model.student.StudentHost StudentHost} is empty
     */
    @Test
    void isEmptyTest() {
        StudentHost host = new StudentHost();
        assertTrue(host.isEmpty());
    }

    /**
     * Tests that {@code placeStudents} places {@link it.polimi.ingsw.server.model.student.Student Students} of a given type and number correctly
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
     * Tests that {@code removeStudent} removes {@link it.polimi.ingsw.server.model.student.Student Students} of a given type and number correctly
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
     * Tests that {@code removeRandom} removes a random {@link it.polimi.ingsw.server.model.student.Student Student} from a {@link it.polimi.ingsw.server.model.student.StudentHost StudentHost}
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

    /**
     * Tests if it throws CollectionUnderflowError whenever it removes {@link it.polimi.ingsw.server.model.student.Student Student} from an empty {@link it.polimi.ingsw.server.model.student.StudentCollection StudentCollection}
     */
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
    
    /**
     * tests methods with null params
     */
    @Test
    void nullTest() {
        StudentHost studentHost = new StudentHost();
        studentHost.placeStudents(null, 20);
        assertDoesNotThrow(() -> studentHost.removeStudents(null, 10));
        studentHost.getCount(null);
    }
}