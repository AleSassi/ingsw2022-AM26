package it.polimi.ingsw.model.student;

import it.polimi.ingsw.exceptions.CollectionUnderflowError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class CloudTest test Cloud
 * Coverage 100%
 *
 * @author Federico Albertini
 * @see Cloud
 */

class CloudTest {

    private Cloud cloud;

    @BeforeEach
    void initCloud() {
        cloud = new Cloud();
    }

    /**
     * Method extractAllStudentsAndRemove tests that can return a StudentCollection with all the Students extracted from cloud
     */
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

        assertThrows(CollectionUnderflowError.class, () -> cloud.removeRandom());
    }
}