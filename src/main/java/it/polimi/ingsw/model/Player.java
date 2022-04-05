package it.polimi.ingsw.model;

import it.polimi.ingsw.model.assistants.Wizard;
import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.model.match.MatchManager;
import it.polimi.ingsw.model.student.*;
import it.polimi.ingsw.model.assistants.*;
import java.util.*;

public class Player {

    private final String nickname;
    private int availableTowers;
    private final Tower towerColor;
    private final boolean[] controlledProfessors;
    private CharacterCard playedCard;
    private final AvailableCardsDeck mydeck;
    private PlayedCardDeck myplayedcard;
    private final SchoolBoard Board;
    private int Avaiblecoin;
    MatchManager match;
    private Wizard wiz;



    public Player(String nickname, Wizard wiz, Tower towerColor, int initialTowerCount) {
        this.nickname = nickname;
        this.towerColor = towerColor;
        this.availableTowers = initialTowerCount;
        this.controlledProfessors = new boolean[Professor.values().length];
        Board=new SchoolBoard(towerColor);
        mydeck=new AvailableCardsDeck();
    }

    /**
     *return nickname of player
     */
    public String getNickname() {
        return nickname;
    }
    /**
     *return list of card that player can use
     */
    public ArrayList<AssistantCard> getAvailableAssistantCards() {
        int i;
        ArrayList<AssistantCard> mycards= new ArrayList<AssistantCard>();
        int numberofcard=mydeck.getCount();
        for(i=0;i<numberofcard;i++){
        mycards.add(mydeck.getCard(i));
        }
        return mycards;
    }

    public AssistantCard getLastPlayedAssistantCard() {
       AssistantCard Lastcard=myplayedcard.topCard();
       return Lastcard;
    }

    public ArrayList<Professor> getControlledProfessors() {
        return Board.getControlledProfessor();

    }

    public int getCountAtTable(Student s) {
     int Count=Board.GetCountAtTheTable(s);
     return Count;
    }



    public CharacterCard getActiveCharacterCard() {
        return playedCard;
    }


    public void playAssistantCardAtIndex(int cardIndex) {
    AssistantCard temp;
    temp=mydeck.getCard(cardIndex);
    mydeck.removeCard(cardIndex);
    myplayedcard.AddCardOnTop(temp);
    }


    public void addStudentToEntrance(Student s) {
        Board.addStudentToEntrance(s);

    }


    public void removeStudentFromEntrance(Student s)throws EmptyCollectionException  {
        try{
            Board.RemoveStudentFromEntrance(s);
            }
        catch (EmptyCollectionException e){
            throw e;
        }
    }


    public void placeStudentAtTableAndGetCoin(Student s) {

        Board.AddStudentToTable(s);
        this.Avaiblecoin=this.Avaiblecoin+1;

    }

    public void removeStudentFromTable(Student s)throws EmptyCollectionException{
        try{
        Board.RemoveStudentTable(s);}
        catch(EmptyCollectionException e){
            throw e;

        }
    }

    public void addProfessor(Professor p) {
        controlledProfessors[Professor.getRawValueOf(p)] = true;

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
        int i;
        for(Student s:Student.values()) {
            int Count=sc.getCount(s);
            for(i=0;i<Count;i++){
                addStudentToEntrance(s);}
    }
    }

    public boolean playCharacterCard(CharacterCard card) {
        card.purchase();
        int price=card.getPrice();
        if(price<this.Avaiblecoin){
        playedCard = card;
        this.Avaiblecoin=(this.Avaiblecoin)-price;
        return true;}
        else{
            return false;
        }

    }

    public void deactivateCard() {
        playedCard=null;

    }



    public void notifyVictory() {
        match.notify();

    }

}
