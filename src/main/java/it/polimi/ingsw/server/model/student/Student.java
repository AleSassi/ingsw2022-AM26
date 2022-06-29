package it.polimi.ingsw.server.model.student;

import it.polimi.ingsw.server.model.Professor;

import java.util.*;

/**
 * Student Enum represent the Students
 *
 * @author Alessandro Sassi, Federico Albertini
 */
public enum Student {
    GreenFrog(Professor.GreenFrog),
    RedDragon(Professor.RedDragon),
    YellowElf(Professor.YellowElf),
    PinkFair(Professor.PinkFair),
    BlueUnicorn(Professor.BlueUnicorn);

    /**
     * Associated Professor
     */
    private final Professor associatedProfessor;

    private static final Random randomizer = new Random();
    private static final List<Student> values = List.of(values());

    /**
     * Constructor associates the {@link Professor} to the relative {@code Student}
     * @param professor (type Professor) to associate to this {@code Student}
     */
    Student(Professor professor) {
        associatedProfessor = professor;
    }

    /**
     * Gets the associated {@link Professor}
     *
     * @return (type Professor) the associated {@code Professor}
     */
    public Professor getAssociatedProfessor() {
        return this.associatedProfessor;
    }

    /**
     * Gets the raw index of the {@code Student}
     * @param student (type Student) of which to get the indec
     * @return (type int) the index of {@code Student}
     */
    public static int getRawValueOf(Student student) {
        return values.indexOf(student);
    }

    /**
     * Selects a random {@code Student}
     * @return (type Student) a {@code Student}
     */
    public static Student getRandomStudent() {
        int randIndex = randomizer.nextInt(0, values.size());
        return values.get(randIndex);
    }

    /**
     * Gets the color of the {@code Student}
     * @return (type string) the color
     */
    public String getColor() {
        switch (this) {
            case YellowElf -> {
                return "yellow";
            }
            case BlueUnicorn -> {
                return "blue";
            }
            case GreenFrog -> {
                return "green";
            }
            case RedDragon -> {
                return "red";
            }
            case PinkFair -> {
                return "pink";
            }
        }
        return null;
    }
}
