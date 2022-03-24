package it.polimi.ingsw.model;

public class AssistantCard {
    int cardCharacterID;
    int priorityNumber;
    int motherNatureSteps;

    public AssistantCard(int value){ //constructor
        cardCharacterID=value;
    }

    public int Getid(){ //constructor
       return cardCharacterID;
    }

    public int getPriorityNumber(){
        return priorityNumber;

    }


    public int getMotherNatureSteps(){
        return motherNatureSteps;
    }
}
