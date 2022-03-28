package it.polimi.ingsw.model;

public enum AssistantCard {

    TURTLE(1,1),
    ELEFANTE(2,1),
    DOG(3,2),
    POLIPO(4,2),
    SERPENTE(5,3),
    VOLPE(6,3),
    ACQUILA(7,4),
    CAT(8,4),
    PAVONE(9,5),
    LION(10,5);
    private int PriorityNumber;
    private int motherNatureSteps;

    /**
     *constructor
     */

    private AssistantCard(int PriorityNumber, int motherNatureSteps){
        this.PriorityNumber=PriorityNumber;
        this.motherNatureSteps=motherNatureSteps;


    }




    public int getPriorityNumber(){
        return this.PriorityNumber;

    }


    public int getMotherNatureSteps(){
        return this.motherNatureSteps;
    }
}
