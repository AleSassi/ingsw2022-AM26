package it.polimi.ingsw.model;

public class AssistantCard {
    int cardCharacterID;
    int priorityNumber;
    int motherNatureSteps;

    public AssistantCard(int id){
        cardCharacterID=id;
    }

    /**
     *constructor with id parameter
     */
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
