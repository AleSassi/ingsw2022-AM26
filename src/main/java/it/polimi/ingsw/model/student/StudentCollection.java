package it.polimi.ingsw.model.student;
import com.google.gson.annotations.Expose;
import it.polimi.ingsw.exceptions.CollectionUnderflowError;

import java.util.*;

public class StudentCollection {

    private final int[] students;
    private final transient Random randomizer = new Random();

    public StudentCollection() {
        this.students = new int[Student.values().length];
        for (int index = 0; index < Student.values().length; index++) {
            this.students[index] = 0;
        }
    }

    public int getCount(Student s) {
        if (s == null) return 0;

        int studentIndex = Student.getRawValueOf(s);
        return students[studentIndex];
    }

    public int getTotalCount() {
        return Arrays.stream(students).sum();
    }

    public void removeStudents(Student s, int count) throws CollectionUnderflowError {
        if (s == null) return;

        int studentIndex = Student.getRawValueOf(s);
        if (students[studentIndex] - count < 0) throw new CollectionUnderflowError();

        students[studentIndex] = students[studentIndex] - count;
    }

    public void addStudents(Student s, int count) {
        if (s == null) return;

        int studentIndex = Student.getRawValueOf(s);
        if (count < 0) {
            // We silently fail when the number of students is negative
            return;
        }
        students[studentIndex] = students[studentIndex] + count;
    }

    public void mergeWithCollection(StudentCollection otherCollection) {
        if (otherCollection == null) return;

        for (Student s: Student.values()) {
            addStudents(s, otherCollection.getCount(s));
        }
    }

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

    private List<Integer> getProgressiveCounts() {
        List<Integer> result = new ArrayList<>(students.length);
        int lastProgressiveCount = 0;
        for (Integer studentCount: students) {
            result.add(lastProgressiveCount + studentCount);
            lastProgressiveCount += studentCount;
        }
        return result;
    }
    
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
