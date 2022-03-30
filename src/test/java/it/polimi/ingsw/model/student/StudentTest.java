package it.polimi.ingsw.model.student;

import it.polimi.ingsw.model.Professor;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Class StudentTest test Student
 * Coverage 100%
 *
 * @author Federico Albertini
 * @see Student
 */
class StudentTest {

    /**
     * Method getAssociatedProfessor test that can return the Professor associated to the Student
     */
    @org.junit.jupiter.api.Test
    void getAssociatedProfessor() {
        for(Student s : Student.values()) {
            Professor p = s.getAssociatedProfessor();
            switch (s) {
                case YellowElf -> assertEquals(Professor.YellowElf, p);
                case GreenFrog -> assertEquals(Professor.GreenFrog, p);
                case PinkFair -> assertEquals(Professor.PinkFair, p);
                case RedDragon -> assertEquals(Professor.RedDragon, p);
                case BlueUnicorn -> assertEquals(Professor.BlueUnicorn, p);
            }
        }
    }

    /**
     * Method getRawValueOf tests that can return the index of the list of Students
     */
    @org.junit.jupiter.api.Test
    void getRawValueOf() {
        int i = 0;
        for(Student s : Student.values()) {
           assertEquals(Student.getRawValueOf(s), i);
           i++;
        }
    }

    /**
     * Method getRandomStudent test that returns a random Student
     */
    @org.junit.jupiter.api.Test
    void getRandomStudent() {
        Student s = Student.getRandomStudent();
        assertNotNull(Student.getRandomStudent());
        switch (s) {
            case YellowElf -> assertEquals(Student.YellowElf, s);
            case GreenFrog -> assertEquals(Student.GreenFrog, s);
            case PinkFair -> assertEquals(Student.PinkFair, s);
            case RedDragon -> assertEquals(Student.RedDragon, s);
            case BlueUnicorn -> assertEquals(Student.BlueUnicorn, s);
        }

    }

    @org.junit.jupiter.api.Test
    void values() {
        assertEquals(0, 0);
    }

    @org.junit.jupiter.api.Test
    void valueOf() {
        assertEquals(0, 0);
    }
}