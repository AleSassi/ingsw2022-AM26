package it.polimi.ingsw.server.model.student;

import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.server.model.student.Cloud;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;
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
    
    /**
     * Test initialization
     */
    @BeforeEach
    void initCloud() {
        cloud = new Cloud();
    }

    /**
     * Tests {@code extractAllStudentsAndRemove} can return a {@link it.polimi.ingsw.server.model.student.StudentCollection StudentCollection} with all the {@link it.polimi.ingsw.server.model.student.Student Students} extracted from {@link it.polimi.ingsw.server.model.student.Cloud Cloud}
     */
    @Test
    void extractAllStudentsAndRemoveTest() {
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
    
    /**
     * Tests that {@code extractAllStudentsFromEmptyCloudTest} returns an empty {@link it.polimi.ingsw.server.model.student.StudentCollection StudentCollection} if the {@link it.polimi.ingsw.server.model.student.Cloud Cloud} itself is empty
     */
    @Test
    void extractAllStudentsFromEmptyCloudTest() {
        assertDoesNotThrow(() -> {
            StudentCollection result;
            result = cloud.extractAllStudentsAndRemove();
            assertEquals(0, result.getTotalCount());
        });
    }
}