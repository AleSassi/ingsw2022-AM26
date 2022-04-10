package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.student.*;

import java.util.*;

public class SchoolBoard {

    private final int maxTowerCount;
    private int availableTowerCount;
    private final Tower towerType;
    private final StudentHost diningRoom;
    private final StudentHost entrance;
    private final boolean[] controlledProfessors;

    public SchoolBoard(Tower tower, int initialTowerCount) throws IncorrectConstructorParametersException {
        if (tower == null || initialTowerCount < 0 || initialTowerCount > 8) throw new IncorrectConstructorParametersException();
        
        towerType = tower;
        availableTowerCount = initialTowerCount;
        controlledProfessors = new boolean[Professor.values().length];
        diningRoom = new StudentHost();
        entrance = new StudentHost();
        maxTowerCount = initialTowerCount;
    }

    public int getCountAtTable(Student s) {
        return diningRoom.getCount(s);
    }

    public int getAvailableTowerCount() {
        return availableTowerCount;
    }

    public Tower getTowerType() {
        return towerType;
    }

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

    public void addStudentToEntrance(Student s) {
        entrance.placeStudents(s, 1);
    }

    public void removeStudentFromEntrance(Student s) throws CollectionUnderflowError {
        entrance.removeStudents(s, 1);
    }

    public void addStudentToTable(Student s) {
        diningRoom.placeStudents(s, 1);
    }

    public void removeStudentFromTable(Student s) throws CollectionUnderflowError {
        diningRoom.removeStudents(s, 1);
    }

    public void gainTower() throws TooManyTowersException {
        if (availableTowerCount == maxTowerCount) {throw new TooManyTowersException();}
        availableTowerCount += 1;
    }

    public Tower pickAndRemoveTower() throws InsufficientTowersException {
        if (this.availableTowerCount == 0) throw new InsufficientTowersException();

        this.availableTowerCount -= 1;
        return towerType;
    }
}
