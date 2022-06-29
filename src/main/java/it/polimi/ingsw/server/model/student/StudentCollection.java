package it.polimi.ingsw.server.model.student;
import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.utils.cli.StringFormatter;

import java.util.*;

/**
 * StudentCollection represent the collection of {@link Student}
 * @author Federico Albertini
 */
public class StudentCollection {

    private final int[] students;
    private final transient Random randomizer = new Random();

    /**
     * Initializes the array of {@link Student}
     */
    public StudentCollection() {
        this.students = new int[Student.values().length];
        for (int index = 0; index < Student.values().length; index++) {
            this.students[index] = 0;
        }
    }

    /**
     * Gets the count of the {@link Student} of type s
     * @param s (type {@code Student}) type to count
     * @return (type Student) the count of {@code Students}
     */
    public int getCount(Student s) {
        if (s == null) return 0;

        int studentIndex = Student.getRawValueOf(s);
        return students[studentIndex];
    }

    /**
     * Gets the total count of {@link Student}
     * @return (type int) the total count of Students
     */
    public int getTotalCount() {
        return Arrays.stream(students).sum();
    }

    /**
     * Removes {@code s} {@link Student} from this {@code StudentCollection}
     * @param s (type Student) type to remove
     * @param count (type int) amount to remove
     * @throws CollectionUnderflowError whenever it tries to remove a {@code Student} from an empty {@code StudentCollection}
     */
    public void removeStudents(Student s, int count) throws CollectionUnderflowError {
        if (s == null) return;

        int studentIndex = Student.getRawValueOf(s);
        if (students[studentIndex] - count < 0) throw new CollectionUnderflowError();

        students[studentIndex] = students[studentIndex] - count;
    }

    /**
     * Adds {@code s} {@link Student} from this {@code StudentCollection}
     * @param s (type Student) type to add
     * @param count (type int) amount to add
     */
    public void addStudents(Student s, int count) {
        if (s == null) return;

        int studentIndex = Student.getRawValueOf(s);
        if (count < 0) {
            // We silently fail when the number of students is negative
            return;
        }
        students[studentIndex] = students[studentIndex] + count;
    }

    /**
     * Merges to {@code StudentCollection}
     * @param otherCollection {@code StudentCollection} to merge with
     */
    public void mergeWithCollection(StudentCollection otherCollection) {
        if (otherCollection == null) return;

        for (Student s: Student.values()) {
            addStudents(s, otherCollection.getCount(s));
        }
    }

    /**
     * Picks a random {@link Student} from the {@code StudentCollection}
     * @return  (type Student) the randomly selected {@code Student}
     * @throws CollectionUnderflowError whenever it tries to remove a {@code Student} from an empty {@code StudentCollection}
     */
    public Student pickRandom() throws CollectionUnderflowError {
        if (getTotalCount() == 0) throw new CollectionUnderflowError();
        int ranInt = randomizer.nextInt(0, getTotalCount());
        List<Integer> progressiveCounts = getProgressiveCounts();
        for (int index = 0; index < progressiveCounts.size(); index++) {
            int lowerRangeBounds = index == 0 ? 0 : progressiveCounts.get(index - 1);
            int upperRangeBounds = progressiveCounts.get(index);
            if (ranInt >= lowerRangeBounds && ranInt < upperRangeBounds) {
                Student pickedStudent = Student.values()[index];
                removeStudents(pickedStudent, 1);
                return pickedStudent;
            }
        }
        //Will never be executed
        throw new CollectionUnderflowError("StudentCollection ERROR: Could not find a random student in a collection, that mathematically should never happen");
    }

    /**
     * Gets the count of each {@link  Student} type in this {@code StudentCollection}
     * @return (type List of Integer) the count of each {@code Student} type
     */
    private List<Integer> getProgressiveCounts() {
        List<Integer> result = new ArrayList<>(students.length);
        int lastProgressiveCount = 0;
        for (Integer studentCount: students) {
            result.add(lastProgressiveCount + studentCount);
            lastProgressiveCount += studentCount;
        }
        return result;
    }

    /**
     * Copies this {@code StudentCollection}
     * @return (type StudentCollection) the copied {@code StudentCollection}
     */
    public StudentCollection copy() {
        StudentCollection result = new StudentCollection();
        result.mergeWithCollection(this);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StudentCollection that = (StudentCollection) o;

        return Arrays.equals(students, that.students);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(students);
        result = 31 * result + randomizer.hashCode();
        return result;
    }
}
