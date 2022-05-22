package it.polimi.ingsw.server.model.student;

import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.student.Student;
import org.junit.jupiter.api.Test;

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
    @Test
    void getAssociatedProfessorTest() {
        for (Student s : Student.values()) {
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
    @Test
    void getRawValueOfTest() {
        int i = 0;
        for (Student s : Student.values()) {
            assertEquals(Student.getRawValueOf(s), i);
            i++;
        }
    }

    /**
     * Method getRandomStudent test that returns a random Student
     */
    @Test
    void getRandomStudentTest() {
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
    
    @Test
    void testGetColor() {
        assertEquals("red", Student.RedDragon.getColor());
        assertEquals("green", Student.GreenFrog.getColor());
        assertEquals("blue", Student.BlueUnicorn.getColor());
        assertEquals("yellow", Student.YellowElf.getColor());
        assertEquals("pink", Student.PinkFair.getColor());
    }
}