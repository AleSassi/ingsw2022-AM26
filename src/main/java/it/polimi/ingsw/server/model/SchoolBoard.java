package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exceptions.model.*;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentHost;

import java.util.*;
/**
 * This Class represent the {@code SchoolBoard}
 * @author Leonardo Betti
 */
public class SchoolBoard {

    /**
     * initialize{@code SchoolBoard}
     */
    private final int maxTowerCount;
    private int availableTowerCount;
    private final Tower towerType;
    private StudentHost diningRoom;
    private StudentHost entrance;
    private boolean[] controlledProfessors;
    /**
     * Constructs and sets up the Schoolboard
     * @param tower (type Tower) type of player tower
     * @param initialTowerCount (type List of int) number of tower
     * @throws IncorrectConstructorParametersException whenever the {@code Parameters} of the constructor aren't correct
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
     * return number of  {@link it.polimi.ingsw.server.model.student.Student student}, that has type
     * @param s (type of student)
     */
    public int getCountAtTable(Student s) {
        return diningRoom.getCount(s);
    }
    /**
     * getter
     */
    public StudentHost getEntrance() {
        return entrance.copy();
    }
    /**
     * getter
     */
    public StudentHost getDiningRoom() {
        return diningRoom.copy();
    }
    /**
     * getter
     */
    public int getAvailableTowerCount() {
        return availableTowerCount;
    }
    /**
     * getter
     */
    public Tower getTowerType() {
        return towerType;
    }

    /**
     * return a list of the controlled {@link it.polimi.ingsw.server.model.Professor professor}
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
    /**
     * set the index of a list of {@link it.polimi.ingsw.server.model.Professor professor}to true, where the index, is the index of
     * @param professor (type of professor) chosen {@code Professor}
     */
    public void setControlledProfessor(Professor professor) {
        if (professor == null) return;
        
        int profIndex = Arrays.asList(Professor.values()).indexOf(professor);
        controlledProfessors[profIndex] = true;
    }

    /**
     * set the index of a list of {@link it.polimi.ingsw.server.model.Professor professor}to false, where the index, is the index of
     * @param professor (type of professor) chosen {@code Professor}
     */
    public void removeProfessorControl(Professor professor) {
        if (professor == null) return;
        
        int profIndex = Arrays.asList(Professor.values()).indexOf(professor);
        controlledProfessors[profIndex] = false;
    }
    /**
     * add a {@link it.polimi.ingsw.server.model.student.Student student}, that has type
     * @param s (type of student) chosen {@code Student}, to the entrance
     */
    public void addStudentToEntrance(Student s) {
        entrance.placeStudents(s, 1);
    }


    /**
     * remove a {@link it.polimi.ingsw.server.model.student.Student student}, that has type
     * @param s (type of student) chosen {@code Student}, to the entrance
     */
    public void removeStudentFromEntrance(Student s) throws CollectionUnderflowError {
        entrance.removeStudents(s, 1);
    }
    /**
     * add a {@link it.polimi.ingsw.server.model.student.Student student}, that has type
     * @param s (type of student) chosen {@code Student}, to the dining room
     */
    public void addStudentToTable(Student s) {
        diningRoom.placeStudents(s, 1);
    }

    /**
     * remove a {@link it.polimi.ingsw.server.model.student.Student student}, that has type
     * @param s (type of student) chosen {@code Student}, to the dining room
     */
    public void removeStudentFromTable(Student s) throws CollectionUnderflowError {
        diningRoom.removeStudents(s, 1);
    }
    /**
     * increment the counter(int) of tower
     @throws TooManyTowersException whenever the number of tower has max value
     */
    public void gainTower() throws TooManyTowersException {
        if (availableTowerCount == maxTowerCount) {throw new TooManyTowersException();}
        availableTowerCount += 1;
    }
    /**
     * decrment the counter of tower and return the type of {@link it.polimi.ingsw.server.model.Tower tower} that schoolboard have
     * @throws InsufficientTowersException whenever the number of tower  doen't have sufficent tower
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
