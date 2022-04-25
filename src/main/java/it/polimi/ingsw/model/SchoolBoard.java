package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.student.*;

import java.util.*;

public class SchoolBoard {

    private final int maxTowerCount;
    private int availableTowerCount;
    private final Tower towerType;
    private StudentHost diningRoom;
    private StudentHost entrance;
    private boolean[] controlledProfessors;
    /**
     * constructor
     */
    public SchoolBoard(Tower tower, int initialTowerCount) throws IncorrectConstructorParametersException {
        if (tower == null || initialTowerCount < 0 || initialTowerCount > 8) throw new IncorrectConstructorParametersException();
        
        towerType = tower;
        availableTowerCount = initialTowerCount;
        controlledProfessors = new boolean[Professor.values().length];
        diningRoom = new StudentHost();
        entrance = new StudentHost();
        maxTowerCount = initialTowerCount;
    }
    /**
     * return the number  of student that has type s present in the diningroom
     */
    public int getCountAtTable(Student s) {
        return diningRoom.getCount(s);
    }


    public int getAvailableTowerCount() {
        return availableTowerCount;
    }

    public Tower getTowerType() {
        return towerType;
    }

    /**
     * return the nuumber of professor
     */
    public ArrayList<Professor> getControlledProfessors() {
        ArrayList<Professor> result = new ArrayList<>();
        for (int i = 0; i < controlledProfessors.length; i++) {
            if (controlledProfessors[i]) {
                result.add(Professor.values()[i]);
            }
        }
        return result;
    }

    public void setControlledProfessor(Professor professor) {
        if (professor == null) return;
        
        int profIndex = Arrays.asList(Professor.values()).indexOf(professor);
        controlledProfessors[profIndex] = true;
    }

    public void removeProfessorControl(Professor professor) {
        if (professor == null) return;
        
        int profIndex = Arrays.asList(Professor.values()).indexOf(professor);
        controlledProfessors[profIndex] = false;
    }
    /**
     * insert a student with type s to the entrance
     */
    public void addStudentToEntrance(Student s) {
        entrance.placeStudents(s, 1);
    }


    /**
     * remove a student with type s from the entrance
     */
    public void removeStudentFromEntrance(Student s) throws CollectionUnderflowError {
        entrance.removeStudents(s, 1);
    }
    /**
     * insert a student with type s to the dining room
     */
    public void addStudentToTable(Student s) {
        diningRoom.placeStudents(s, 1);
    }

    /**
     * remove a student with type s from the dining room
     */
    public void removeStudentFromTable(Student s) throws CollectionUnderflowError {
        diningRoom.removeStudents(s, 1);
    }
    /**
     * increment the number of avaible tower
     */
    public void gainTower() throws TooManyTowersException {
        if (availableTowerCount == maxTowerCount) {throw new TooManyTowersException();}
        availableTowerCount += 1;
    }
    /**
     * return the color of tower and decrement the towercounter on schoolboard
     */
    public Tower pickAndRemoveTower() throws InsufficientTowersException {
        if (this.availableTowerCount == 0) throw new InsufficientTowersException();

        this.availableTowerCount -= 1;
        return towerType;
    }
    /**
     * create a copy of school board
     */
    public SchoolBoard copy() {
        try {
            SchoolBoard result = new SchoolBoard(towerType, maxTowerCount);
            result.availableTowerCount = availableTowerCount;
            result.diningRoom = diningRoom.copy();
            result.entrance = entrance.copy();
            result.controlledProfessors = controlledProfessors.clone();
            return result;
        } catch (IncorrectConstructorParametersException e) {
            // Should never happen
            e.printStackTrace();
            return this;
        }
    }

    /**
     * This method is used just for the test
     */
    public int getNumberOfStudentsInEntrance() {
        int result = 0;
        for (Student s : Student.values()) {
            result += entrance.getCount(s);
        }

        return result;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        SchoolBoard that = (SchoolBoard) o;
        
        if (maxTowerCount != that.maxTowerCount) return false;
        if (availableTowerCount != that.availableTowerCount) return false;
        if (towerType != that.towerType) return false;
        if (!diningRoom.equals(that.diningRoom)) return false;
        if (!entrance.equals(that.entrance)) return false;
        return Arrays.equals(controlledProfessors, that.controlledProfessors);
    }
    
    @Override
    public int hashCode() {
        int result = maxTowerCount;
        result = 31 * result + availableTowerCount;
        result = 31 * result + (towerType != null ? towerType.hashCode() : 0);
        result = 31 * result + diningRoom.hashCode();
        result = 31 * result + entrance.hashCode();
        result = 31 * result + Arrays.hashCode(controlledProfessors);
        return result;
    }


}
