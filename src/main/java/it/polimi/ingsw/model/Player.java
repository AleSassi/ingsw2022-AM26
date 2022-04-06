package it.polimi.ingsw.model;

import it.polimi.ingsw.model.assistants.Wizard;
import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.model.student.*;
import it.polimi.ingsw.model.assistants.*;

import java.util.*;

public class Player {

    private final String nickname;
    private CharacterCard playedCard;
    private final AvailableCardsDeck availableCardsDeck;
    private final PlayedCardDeck playedCardDeck;
    private final SchoolBoard board;
    private int availableCoins;

    public Player(String nickname, Wizard wiz, Tower towerColor, int initialTowerCount) {
        this.nickname = nickname;
        this.board = new SchoolBoard(towerColor, initialTowerCount);
        this.availableCardsDeck = new AvailableCardsDeck();
        this.playedCardDeck = new PlayedCardDeck();
    }

    /**
     * return nickname of player
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * return list of card that player can use
     */
    public ArrayList<AssistantCard> getAvailableAssistantCards() {
        ArrayList<AssistantCard> cards = new ArrayList<>();
        for (int i = 0; i < availableCardsDeck.getCount(); i++) {
            cards.add(availableCardsDeck.getCard(i));
        }
        return cards;
    }

    /**
     * return played assistant card
     */
    public AssistantCard getLastPlayedAssistantCard() {
        return playedCardDeck.topCard();
    }



    public ArrayList<Professor> getControlledProfessors() {
        return board.getControlledProfessors();
    }

    /**
     * return number of student(with type S) in the dining room
     */
    public int getCountAtTable(Student s) {
        return board.getCountAtTable(s);
    }

    /**
     * return the active characther card
     */

    public CharacterCard getActiveCharacterCard() {
        return playedCard;
    }

    /**
     * allow player to use card from avaible deck of assistant
     */

    public void playAssistantCardAtIndex(int cardIndex) {
        AssistantCard temp = availableCardsDeck.getCard(cardIndex);
        availableCardsDeck.removeCard(cardIndex);
        playedCardDeck.AddCardOnTop(temp);
    }
    /**
     * add a student of type s to entance
     */
    public void addStudentToEntrance(Student s) {
        board.addStudentToEntrance(s);
    }
    /**
     * remove a student of type s from entance
     */
    public void removeStudentFromEntrance(Student s) throws EmptyCollectionException {
        board.removeStudentFromEntrance(s);
    }
    /**
     * add a student to dining room e give a coin if it is the third student added
     */
    public void placeStudentAtTableAndGetCoin(Student s) {
        board.addStudentToTable(s);
        if (board.getCountAtTable(s) % 3 == 0) {
            this.availableCoins += 1;
        }
    }

    /**
     * remove a student of type s from diningroom
     */
    public void removeStudentFromTable(Student s) throws EmptyCollectionException {
        board.removeStudentFromTable(s);
    }

    public void addProfessor(Professor p) {
        board.setControlledProfessor(p);
    }

    public void removeProfessor(Professor p) {
        board.removeProfessorControl(p);
    }

    public Tower getTowerType() {
        return board.getTowerType();
    }

    public void gainTower() throws TooManyTowersException {
        board.gainTower();
    }

    public int getAvailableTowerCount() {
        return board.getAvailableTowerCount();
    }

    public Tower pickAndRemoveTower() throws InsufficientTowersException {
        return board.pickAndRemoveTower();
    }

    public void addAllStudentsToEntrance(StudentCollection sc) {
        for (Student s : Student.values()) {
            int Count = sc.getCount(s);
            for (int i = 0; i < Count; i++) {
                addStudentToEntrance(s);
            }
        }
    }

    public boolean playCharacterCard(CharacterCard card) {
        int price = card.getPrice();
        if (price <= this.availableCoins) {
            card.purchase();
            playedCard = card;
            this.availableCoins -= price;
            return true;
        }
        return false;
    }

    public void deactivateCard() {
        playedCard = null;
    }

    public void notifyVictory() {
        //We should use Listener to notify the victory of a Player
    }

}
