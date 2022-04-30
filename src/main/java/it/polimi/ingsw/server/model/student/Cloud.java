package it.polimi.ingsw.server.model.student;

import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;

public class Cloud extends StudentHost {

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