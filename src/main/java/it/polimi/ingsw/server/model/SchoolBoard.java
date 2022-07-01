package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exceptions.model.*;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentHost;

import java.util.*;
/**
 * This Class represent the {@code SchoolBoard}, the space where a Player has their students, controlled professors and towers
 * @author Leonardo Betti
 */
public class SchoolBoard {

    private final int maxTowerCount;
    private int availableTowerCount;
    private final Tower towerType;
    private StudentHost diningRoom;
    private StudentHost entrance;
    private boolean[] controlledProfessors;
    
    /**
     * Constructs and sets up the School Board
     * @param tower (type Tower) type of player tower
     * @param initialTowerCount (type List of int) initial number of towers
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
     * Gets the number of {@link it.polimi.ingsw.server.model.student.Student students} of the same type in the dining room
     * @param s The student in the dining room
     * @return The number of students with the same color in the dining room
     */
    public int getCountAtTable(Student s) {
        return diningRoom.getCount(s);
    }
    
    /**
     * Gets the collection of students that are in the entrance space
     * @return The collection of students that are in the entrance space
     */
    public StudentHost getEntrance() {
        return entrance.copy();
    }
    
    /**
     * Gets the collection of students that are in the Dining room space
     * @return The collection of students that are in the dining room space
     */
    public StudentHost getDiningRoom() {
        return diningRoom.copy();
    }
    
    /**
     * Gets the number of available towers
     * @return The number of available towers
     */
    public int getAvailableTowerCount() {
        return availableTowerCount;
    }
    
    /**
     * Gets the tower color
     * @return The tower color
     */
    public Tower getTowerType() {
        return towerType;
    }

    /**
     * Gets the list of controlled {@link it.polimi.ingsw.server.model.Professor professors}
     * @return The list of controlled {@link it.polimi.ingsw.server.model.Professor professors}
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
     * Sets a Professor as controlled by the Player owning this board
     * @param professor The professor owned by the board
     */
    public void setControlledProfessor(Professor professor) {
        if (professor == null) return;
        
        int profIndex = Arrays.asList(Professor.values()).indexOf(professor);
        controlledProfessors[profIndex] = true;
    }
    
    /**
     * Removes a Professor previously controlled by the Player owning this board
     * @param professor The professor owned by the board
     */
    public void removeProfessorControl(Professor professor) {
        if (professor == null) return;
        
        int profIndex = Arrays.asList(Professor.values()).indexOf(professor);
        controlledProfessors[profIndex] = false;
    }
    
    /**
     * Adds a {@link it.polimi.ingsw.server.model.student.Student student} to the entrance space
     * @param s The student to add
     */
    public void addStudentToEntrance(Student s) {
        entrance.placeStudents(s, 1);
    }
    
    
    /**
     * Removes a {@link it.polimi.ingsw.server.model.student.Student student} from the entrance space
     * @param s The student to remove
     */
    public void removeStudentFromEntrance(Student s) throws CollectionUnderflowError {
        entrance.removeStudents(s, 1);
    }
    
    /**
     * Adds a {@link it.polimi.ingsw.server.model.student.Student student} to the dining room
     * @param s The student to add
     */
    public void addStudentToTable(Student s) {
        diningRoom.placeStudents(s, 1);
    }
    
    /**
     * Removes a {@link it.polimi.ingsw.server.model.student.Student student} from the dining room
     * @param s The student to remove
     */
    public void removeStudentFromTable(Student s) throws CollectionUnderflowError {
        diningRoom.removeStudents(s, 1);
    }
    
    /**
     * Makes the board gain a tower
     * @throws TooManyTowersException whenever the number of tower has max value
     */
    public void gainTower() throws TooManyTowersException {
        if (availableTowerCount == maxTowerCount) {throw new TooManyTowersException();}
        availableTowerCount += 1;
    }
    
    /**
     * Picks a tower from the board, removing it from the available towers
     * @return The picked tower color
     * @throws InsufficientTowersException whenever the board doesn't have enough towers (victory condition)
     */
    public Tower pickAndRemoveTower() throws InsufficientTowersException {
        if (this.availableTowerCount == 1) throw new InsufficientTowersException();

        this.availableTowerCount -= 1;
        return towerType;
    }
    
    /**
     * Creates a copy of school board
     * @return A copy of the board
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
     * Gets the number of students in the entrance space
     * @return The number of students in the entrance space
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
