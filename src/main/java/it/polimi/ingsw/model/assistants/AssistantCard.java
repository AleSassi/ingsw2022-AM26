package it.polimi.ingsw.model.assistants;

public enum AssistantCard {
    TURTLE(1,1),
    ELEPHANT(2,1),
    DOG(3,2),
    OCTOPUS(4,2),
    SNAKE(5,3),
    FOX(6,3),
    EAGLE(7,4),
    CAT(8,4),
    PEAFOWL(9,5),
    LION(10,5);

    private int  priorityNumber;
    private int  motherNatureSteps;


    private AssistantCard(int prioritynumber, int mothernatureSteps){
        this.priorityNumber=prioritynumber;
        this.motherNatureSteps=mothernatureSteps;
    }

    public int getPriorityNumber(){
        return priorityNumber;

    }


    public int getMotherNatureSteps(){
        return motherNatureSteps;
    }
}
