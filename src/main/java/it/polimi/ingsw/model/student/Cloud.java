package it.polimi.ingsw.model.student;

public class Cloud extends StudentHost {

    public StudentCollection extractAllStudentsAndRemove() {
        StudentCollection result = new StudentCollection();
        for (Student s : Student.values()) {
            int studentCount = getCount(s);
            try {
                removeStudent(s, studentCount);
                result.addStudents(s, studentCount);
            } catch (EmptyCollectionException e) {
                //Do nothing - Ignore the student because its count is 0
            }
        }
        return result;

    }
}
