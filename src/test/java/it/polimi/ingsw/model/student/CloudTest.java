package it.polimi.ingsw.model.student;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CloudTest {
    Cloud cloud = new Cloud();

    @Test
    void extractAllStudentsAndRemove() {
        for (Student s : Student.values()) {
            cloud.placeStudents(s, 10);
        }

        assertDoesNotThrow(() -> {
            StudentCollection result;
            result = cloud.extractAllStudentsAndRemove();
            for (Student s : Student.values()) {
                assertEquals(result.getCount(s), 10);
            }
        });


    }
}