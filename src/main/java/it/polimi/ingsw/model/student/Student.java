package it.polimi.ingsw.model.student;

import it.polimi.ingsw.model.Professor;
import java.util.*;

public enum Student {
    YellowElf (Professor.YellowElf),
    BlueUnicorn (Professor.BlueUnicorn),
    GreenFrog (Professor.GreenFrog),
    RedDragon (Professor.RedDragon),
    PinkFair (Professor.PinkFair);

    private final Professor associatedProfessor;

    private static final Random randomizer = new Random();
    private static final List<Student> values = Collections.unmodifiableList(Arrays.asList(values()));

    Student(Professor professor){
        associatedProfessor = professor;
    }

    public Professor getAssociatedProfessor() {
        return this.associatedProfessor;
    }

    public static Student getRandomStudent() {
        int randIndex = randomizer.nextInt(0, values.size());
        return values.get(randIndex);
    }
}
