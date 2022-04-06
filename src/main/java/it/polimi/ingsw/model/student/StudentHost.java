package it.polimi.ingsw.model.student;

import it.polimi.ingsw.exceptions.CollectionUnderflowError;

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

    public void removeStudents(Student s, int count) throws CollectionUnderflowError {
        hostedStudents.removeStudents(s, count);
    }

    public Student removeRandom() throws CollectionUnderflowError {
        return hostedStudents.pickRandom();
    }

}
