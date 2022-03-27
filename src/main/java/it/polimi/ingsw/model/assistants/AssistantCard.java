package it.polimi.ingsw.model.assistants;

public final class AssistantCard {

    private final int cardID;
    private int priorityNumber;
    private int motherNatureSteps;

    public AssistantCard(int id){
        cardID = id;
    }

    /**
     *constructor with id parameter
     */
    public int getCardID(){
       return cardID;
    }

    public int getPriorityNumber(){
        return priorityNumber;
    }

    public int getMotherNatureSteps(){
        return motherNatureSteps;
    }
}
