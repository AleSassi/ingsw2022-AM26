package it.polimi.ingsw.server.model.student;

import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.utils.cli.StringFormatter;

/**
 * This Class represent all the object that can host {@link Student}
 *
 * @author Federico Albertini
 */
public class StudentHost {

    private final StudentCollection hostedStudents = new StudentCollection();

    /**
     * Gets the count of the {@link Student} of type s
     * @param s (type {@code Student}) type to count
     * @return (type Student) the count of {@code Students}
     */
    public int getCount(Student s) {
        return hostedStudents.getCount(s);
    }

    /**
     * Tells if the {@link StudentCollection} of this {@code StudentHost} is empty
     * @return (type boolean) true if this {@code StudentHost} is empty
     */
    public boolean isEmpty() {
        return hostedStudents.getTotalCount() == 0;
    }

    /**
     * Places a certain amount of {@link  Student} of a type on this {@code StudentHost}
     * @param s (type Student) type of {@code Student} to place
     * @param count (type int) amount of {@code Students} to place
     */
    public void placeStudents(Student s, int count) {
        hostedStudents.addStudents(s, count);
    }

    /**
     * Removes a certain amount of {@link Student} of a type on this {@code StudentHost}
     * @param s (type Student) type of {@code Student} to remove
     * @param count (type int) amount of {@code Students} to remove
     * @throws CollectionUnderflowError whenever it tries to remove a {@code Student} from an empty {@link StudentCollection}
     */
    public void removeStudents(Student s, int count) throws CollectionUnderflowError {
        hostedStudents.removeStudents(s, count);
    }

    /**
     * Removes a random {@link Student} from the {@code StudentHost}
     * @return (type Student) the randomly removed {@code Student}
     * @throws CollectionUnderflowError whenever it tries to remove a {@code Student} from an empty {@link StudentCollection}
     */
    public Student removeRandom() throws CollectionUnderflowError {
        return hostedStudents.pickRandom();
    }

    /**
     * Copies this {@code StudentHost}
     * @return (type StudentHost) the copied {@code StudentHost}
     */
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
