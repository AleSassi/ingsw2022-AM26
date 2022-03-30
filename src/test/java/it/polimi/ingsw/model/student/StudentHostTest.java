package it.polimi.ingsw.model.student;

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

    /**
     * Method mergeHostedStudentWithTest test that can merge two HostedStudent together correctly
     */
    @Test
    void mergeHostedStudentWithTest() {
        StudentHost host1 = new StudentHost();
        StudentHost host2 = new StudentHost();
        for(Student s: Student.values()){
            host1.placeStudents(s, 5);
            host2.placeStudents(s, 10);
        }
        host1.mergeHostedStudentWith(host2);

        for(Student s: Student.values()){
            assertEquals(host1.getCount(s), 15);
        }
    }
}