package it.polimi.ingsw.model.student;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentCollectionTest {

    StudentCollection collection = new StudentCollection();

    @Test
    void getCount() {
        StudentCollection collection = new StudentCollection();
        for(Student s : Student.values()) {
            collection.addStudents(s, 10);
            assertEquals(collection.getCount(s), 10);
        }
    }

    @Test
    void getTotalCount() {
        for(Student s : Student.values()) {
            collection.addStudents(s, 10);
        }
        assertEquals(collection.getTotalCount(), 50);
    }

    @Test
    void removeStudents() {
        StudentCollection hostedStudent = new StudentCollection();
        for (Student s : Student.values()) {
            hostedStudent.addStudents(s, 10);
            hostedStudent.removeStudents(s, 10);
            assertEquals(hostedStudent.getCount(s), 0);
        }
    }

    @Test
    void addStudents() {
        StudentCollection hostedStudent = new StudentCollection();
        for (Student s : Student.values()) {
            hostedStudent.addStudents(s, 10);
            assertEquals(hostedStudent.getCount(s), 10);
        }
    }

    @Test
    void mergeWithCollection() {
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

    @Test
    void pickRandom() {
    }

    @Test
    void testEquals() {
    }

    @Test
    void testHashCode() {
    }



}