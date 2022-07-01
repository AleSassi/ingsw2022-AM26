package it.polimi.ingsw.server.model.student;

import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;

/**
 * Cloud class represent the cloud on which are the {@link Student}
 * @author Federico Albertini
 */
public class Cloud extends StudentHost {

    /**
     * Extracts all the {@link Student} from this {@code Cloud}
     *
     * @return (type StudentCollection) returns the removed {@code StudentCollection}
     *
     * @see Student
     * @see StudentCollection
     */
    public StudentCollection extractAllStudentsAndRemove() {
        StudentCollection result = new StudentCollection();
        for (Student s : Student.values()) {
            int studentCount = getCount(s);
            try {
                removeStudents(s, studentCount);
                result.addStudents(s, studentCount);
            } catch (CollectionUnderflowError e) {
                //Do nothing - Ignore the student because its count is 0
            }
        }
        return result;
    }
}
