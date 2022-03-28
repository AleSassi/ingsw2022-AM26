package it.polimi.ingsw.model;

import it.polimi.ingsw.model.assistants.Wizard;
import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.model.student.*;
import it.polimi.ingsw.model.assistants.*;
import java.util.*;

public class Player {

    private final String nickname;
    private int availableTowers;
    private final Tower towerColor;

    public Player(String nickname, Wizard wiz, Tower towerColor, int initialTowerCount) {
        this.nickname = nickname;
        this.towerColor = towerColor;
        this.availableTowers = initialTowerCount;
    }

    public String getNickname() {
        return nickname;
    }

    public List<AssistantCard> getAvailableAssistantCards() {
        return null;
    }

    public AssistantCard getLastPlayedAssistantCard() {
        return null;
    }

    public List<Professor> getControlledProfessors() {
        return null;
    }

    public int getCountAtTable(Student s) {
        return 0;
    }

    public CharacterCard getActiveCharacterCard() {
        return null;
    }

    public void playAssistantCardAtIndex(int cardIndex) {

    }

    public void addStudentToEntrance(Student s) {

    }
    public void removeStudentFromEntrance(Student s) {

    }

    /**
     * Returns the number of students with the same type as S currently in the Dining Room
     */
    public int placeStudentAtTableAndGetCoin(Student s) {
        return 0;
    }

    public void removeStudentFromTable(Student s) {

    }

    public void addProfessor(Professor p) {

    }

    public void removeProfessor(Professor p) {

    }

    public Tower getTowerType() {
        return towerColor;
    }

    public void gainTower() {

    }

    public Tower pickAndRemoveTower() throws InsufficientTowersException {
        if (availableTowers == 0) throw new InsufficientTowersException();
        availableTowers -= 1;
        return towerColor;
    }

    public void addAllStudentsToEntrance(StudentCollection sc) {

    }

    public void playCharacterCard(CharacterCard card) {

    }

    public void deactivateCard() {

    }

    private boolean isWinner() {
        return false;
    }

    public void notifyVictory() {

    }

}
