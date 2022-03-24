package it.polimi.ingsw.model.student;

import it.polimi.ingsw.model.Professor;

public enum Student {
    YellowElf (Professor.YellowElf),
    BlueUnicorn (Professor.BlueUnicorn),
    GreenFrog (Professor.GreenFrog),
    RedDragon (Professor.RedDragon),
    PinkFair(Professor.PinkFair);

    Professor associatedProfessor;

    Student(Professor professor){
        associatedProfessor = professor;
    }
    public Professor getAssociatedProfessor(){
        return this.associatedProfessor;
    }
}
