package it.polimi.ingsw.server.model.student;

import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.utils.cli.StringFormatter;

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
    
    public StudentHost copy() {
        StudentHost result = new StudentHost();
        result.hostedStudents.mergeWithCollection(hostedStudents);
        return result;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        StudentHost that = (StudentHost) o;
    
        return hostedStudents.equals(that.hostedStudents);
    }
    
    @Override
    public int hashCode() {
        return hostedStudents.hashCode();
    }


}
