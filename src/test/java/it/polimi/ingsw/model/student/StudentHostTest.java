package it.polimi.ingsw.model.student;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentHostTest {

    @Test
    void getCount() {
        StudentHost host = new StudentHost();
        for(Student s: Student.values()){
            host.placeStudents(s, 10);
            assertEquals(host.getCount(s), 10);
        }
    }

    @Test
    void isEmpty() {
        StudentHost host = new StudentHost();
        assertTrue(host.isEmpty());
    }

    @Test
    void placeStudents() {
        StudentHost host = new StudentHost();
        for(Student s: Student.values()){
            host.placeStudents(s, 10);
            assertEquals(host.getCount(s), 10);
        }
    }

    @Test
    void removeStudent() {
        StudentHost host = new StudentHost();
        assertDoesNotThrow(() -> {
            for(Student s: Student.values()){
                host.placeStudents(s, 10);
                host.removeStudents(s, 10);
                assertEquals(host.getCount(s), 0);
            }
        });
    }

    @Test
    void removeRandom() {
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
    void mergeHostedStudentWith() {
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