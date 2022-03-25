package it.polimi.ingsw.model.student;

public class StudentHost {

    private StudentCollection hostedStudents = new StudentCollection();

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
        if(hostedStudents.getCount(s) == 0) throw new EmptyCollectionException();
        else hostedStudents.removeStudents(s, count);
    }
    public Student removeRandom() throws EmptyCollectionException {
        Student s = hostedStudents.pickRandom();
        if (hostedStudents.getCount(s) == 0) throw new EmptyCollectionException();
        else {
            removeStudent(s, 1);
            return s;
        }
    }

}
