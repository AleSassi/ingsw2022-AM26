package it.polimi.ingsw.model.student;

public class Cloud extends StudentHost {

    public StudentCollection extractAllStudentsAndRemove() throws EmptyCollectionException {
        StudentCollection result = new StudentCollection();
        for (Student s : Student.values()) {
            int studentCount = getCount(s);
            removeStudent(s, studentCount);
            result.addStudents(s, studentCount);
        }
        return result;

    }
}
