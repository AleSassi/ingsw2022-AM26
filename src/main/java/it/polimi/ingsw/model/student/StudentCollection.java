package it.polimi.ingsw.model.student;


import java.util.*;

public class StudentCollection{



    private final HashMap<Student, Integer>  students = new HashMap<>();

    public HashMap<Student, Integer> getStudents() {
        return students;
    }
    public int getCount(Student s) {
       return students.get(s);
    }

    public int getTotalCount(){
        int totalCount = 0;
        for(Student i : Student.values()) {
            totalCount += getCount(i);
        }
        return totalCount;
    }

    public void removeStudents(Student s, int count){
        students.put(s, students.get(s) - count);
    }

    void addStudents(Student s, int count) {
        students.put(s, count);
    }

    public Student pickRandom() {
        Random random = new Random();
        return Student.values()[random.nextInt(Student.values().length)];

    }


}
