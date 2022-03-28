package it.polimi.ingsw.model.student;

public class StudentHost {

    private final StudentCollection hostedStudents = new StudentCollection();

    public int getCount(Student s) {
        return hostedStudents.getCount(s);
    }

    public boolean isEmpty() {
        return hostedStudents.getTotalCount() == 0;
    }

    public void placeStudents(Student s, int count) {
        hostedStudents.addStudents(s, count);
    }
    public void removeStudent(Student s, int count) throws EmptyCollectionException {
        if (hostedStudents.getCount(s) == 0) throw new EmptyCollectionException();
        hostedStudents.removeStudents(s, count);
    }

    public Student removeRandom() throws EmptyCollectionException {
        return hostedStudents.pickRandom();
    }

    public void mergeHostedStudentWith(StudentHost other) {
        hostedStudents.mergeWithCollection(other.hostedStudents);
    }

}
