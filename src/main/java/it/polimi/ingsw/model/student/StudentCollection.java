package it.polimi.ingsw.model.student;
import it.polimi.ingsw.exceptions.CollectionUnderflowError;

import java.util.*;

public class StudentCollection {

    private final List<Integer> students;
    private final Random randomizer;

    public StudentCollection() {
        this.students = new ArrayList<>(Student.values().length);
        this.randomizer = new Random();
        for (int index = 0; index < Student.values().length; index++) {
            this.students.add(0);
        }
    }

    public int getCount(Student s) {
        int studentIndex = Student.getRawValueOf(s);
        return students.get(studentIndex);
    }

    public int getTotalCount() {
        return students.stream().mapToInt(element -> element).sum();
    }

    public void removeStudents(Student s, int count) throws CollectionUnderflowError {
        int studentIndex = Student.getRawValueOf(s);
        if (students.get(studentIndex) - count < 0) throw new CollectionUnderflowError();

        students.set(studentIndex, students.get(studentIndex) - count);
    }

    public void addStudents(Student s, int count) {
        int studentIndex = Student.getRawValueOf(s);
        students.set(studentIndex, students.get(studentIndex) + count);
    }

    public void mergeWithCollection(StudentCollection otherCollection) {
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
        List<Integer> result = new ArrayList<>(students.size());
        int lastProgressiveCount = 0;
        for (Integer studentCount: students) {
            result.add(lastProgressiveCount + studentCount);
            lastProgressiveCount += studentCount;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StudentCollection that = (StudentCollection) o;

        return students.equals(that.students);
    }

    @Override
    public int hashCode() {
        int result = students.hashCode();
        result = 31 * result + randomizer.hashCode();
        return result;
    }
}
