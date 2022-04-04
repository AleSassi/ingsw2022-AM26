package it.polimi.ingsw.model.student;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class StudentCollectionTest test StudentCollection
 * Coverage 100%
 *
 * @author Federico Albertini
 * @see StudentCollection
 */
class StudentCollectionTest {

    StudentCollection collection = new StudentCollection();

    /**
     * Method getCountTest tests that can return the count of a single type of student
     */
    @Test
    void getCountTest() {
        StudentCollection collection = new StudentCollection();
        for(Student s : Student.values()) {
            collection.addStudents(s, 10);
            assertEquals(collection.getCount(s), 10);
        }
    }

    /**
     * Method getTotalCountTest tests that can return the total count of students
     */
    @Test
    void getTotalCountTest() {
        for(Student s : Student.values()) {
            collection.addStudents(s, 10);
        }
        assertEquals(collection.getTotalCount(), 50);
    }

    /**
     * Method removeStudentsTest tests that can remove students of given type and count from the collection correctly
     */
    @Test
    void removeStudentsTest() {
        StudentCollection hostedStudent = new StudentCollection();
        for (Student s : Student.values()) {
            hostedStudent.addStudents(s, 10);
            hostedStudent.removeStudents(s, 10);
            assertEquals(hostedStudent.getCount(s), 0);
        }
    }

    /**
     * Method addStudentsTest tests that can add students of given type and count from the collection correctly
     */
    @Test
    void addStudentsTest() {
        StudentCollection hostedStudent = new StudentCollection();
        for (Student s : Student.values()) {
            hostedStudent.addStudents(s, 10);
            assertEquals(hostedStudent.getCount(s), 10);
        }
    }

    /**
     * Method mergeWithCollectionTest tests that can merge two collections correctly
     */
    @Test
    void mergeWithCollectionTest() {
        StudentCollection collection2 = new StudentCollection();
        for (Student s : Student.values()) {
            collection2.addStudents(s, 10);
            collection.addStudents(s, 5);
        }
        collection.mergeWithCollection(collection2);
        for (Student s : Student.values()) {
            assertEquals(collection.getCount(s), 15);
        }
    }

    /**
     * Method pickRandomTest tests that return a random student type from the collection and remove it correctly
     */
    @Test
    void pickRandomTest() {
        for(Student s: Student.values()) {
            collection.addStudents(s, 10);
        }
        assertDoesNotThrow(() -> {
            Student s = collection.pickRandom();
            switch (s) {
                case YellowElf -> assertEquals(s, Student.YellowElf);
                case GreenFrog -> assertEquals(s, Student.GreenFrog);
                case RedDragon -> assertEquals(s, Student.RedDragon);
                case BlueUnicorn -> assertEquals(s, Student.BlueUnicorn);
                case PinkFair -> assertEquals(s, Student.PinkFair);
            }
        });
        assertEquals(49, collection.getTotalCount());
    }

    /**
     * Method testEqualsTest tests that returns true if two collections are the same
     */
    @Test
    void testEqualsTest() {
        StudentCollection collection2 = new StudentCollection();
        StudentCollection collection3 = new StudentCollection();
        collection.addStudents(Student.YellowElf, 2);
        collection.addStudents(Student.GreenFrog, 3);
        collection2.addStudents(Student.YellowElf, 2);
        collection2.addStudents(Student.GreenFrog, 3);
        collection3.addStudents(Student.GreenFrog, 3);
        collection3.addStudents(Student.RedDragon, 2);
        assertTrue(collection.equals(collection2));
        assertFalse(collection.equals(collection3));
    }

    @Test
    void testHashCodeTest() {
        //TODO:
    }



}