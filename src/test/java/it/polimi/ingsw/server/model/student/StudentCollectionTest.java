package it.polimi.ingsw.server.model.student;

import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class {@code StudentCollectionTest} tests {@link it.polimi.ingsw.server.model.student.StudentCollection StudentCollection}
 * Coverage 100%
 *
 * @author Federico Albertini
 * @see StudentCollection
 */
class StudentCollectionTest {

    private StudentCollection collection;

    @BeforeEach
    void initCollection() {
        collection = new StudentCollection();
    }

    /**
     *  Tests that {@code getCount} can return the count of a single type of {@link it.polimi.ingsw.server.model.student.Student Student}
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
     * Tests that {@code getTotalCount} returns the total count of {@link it.polimi.ingsw.server.model.student.Student Student}
     */
    @Test
    void getTotalCountTest() {
        for(Student s : Student.values()) {
            collection.addStudents(s, 10);
        }
        assertEquals(collection.getTotalCount(), 50);
    }

    /**
     * Tests that {@code removeStudents} removes a {@link it.polimi.ingsw.server.model.student.Student Student} of given type and count from the {@link it.polimi.ingsw.server.model.student.StudentCollection StudentCollection} correctly
     */
    @Test
    void removeStudentsTest() {
        StudentCollection hostedStudent = new StudentCollection();
        for (Student s : Student.values()) {
            hostedStudent.addStudents(s, 10);
            assertDoesNotThrow(() -> hostedStudent.removeStudents(s, 10));
            assertEquals(hostedStudent.getCount(s), 0);
        }
    }

    /**
     * Tests that {@code addStudents} adds {@link it.polimi.ingsw.server.model.student.Student Students} of given type and count from the collection correctly
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
     * Tests that {@code mergeWithCollection} merges two {@link it.polimi.ingsw.server.model.student.StudentCollection StudentCollection} correctly
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
     * Tests that {@code pickRandom} returns a random {@link it.polimi.ingsw.server.model.student.Student Student} type from the {@link it.polimi.ingsw.server.model.student.StudentCollection StudentCollection} and removes it correctly
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
     * Tests that {@code testEquals} returns true if two {@link it.polimi.ingsw.server.model.student.StudentCollection StudentCollections} are the same
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
        assertEquals(collection, collection2);
        assertNotEquals(collection, collection3);
    }

    /**
     * Tests that whenever it tries to remove a {@link it.polimi.ingsw.server.model.student.Student Student} from an empty {@link it.polimi.ingsw.server.model.student.StudentCollection StudentCollection} it throws {@code CollectionUnderflowError}
     */
    @Test
    void underflowTest() {
        StudentCollection collection = new StudentCollection();
        collection.addStudents(Student.BlueUnicorn, 10);
        collection.addStudents(Student.RedDragon, 5);
        collection.addStudents(Student.GreenFrog, -1);
        assertEquals(0, collection.getCount(Student.GreenFrog));
        assertThrows(CollectionUnderflowError.class, () -> collection.removeStudents(Student.BlueUnicorn, 11));
        assertThrows(CollectionUnderflowError.class, () -> collection.removeStudents(Student.GreenFrog, 1));
        assertDoesNotThrow(() -> collection.removeStudents(Student.PinkFair, 0));
    }
    
    /**
     * Tests methods with null params
     */
    @Test
    void nullTest() {
        StudentCollection collection = new StudentCollection();
        collection.addStudents(null, 20);
        assertDoesNotThrow(() -> collection.removeStudents(null, 10));
        collection.mergeWithCollection(null);
        collection.getCount(null);
    }
    
    /**
     * Tests the copy method
     */
    @Test
    void copyTest() {
        collection.addStudents(Student.BlueUnicorn, 10);
        collection.addStudents(Student.RedDragon, 5);
        collection.addStudents(Student.GreenFrog, 3);
        collection.addStudents(Student.PinkFair, 2);
        collection.addStudents(Student.YellowElf, 7);
        assertEquals(collection, collection.copy());
    }
}