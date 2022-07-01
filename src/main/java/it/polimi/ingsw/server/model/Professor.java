package it.polimi.ingsw.server.model;

import java.util.*;

/**
 * An enum representing the possible values for Professor tiles
 */
public enum Professor {
    YellowElf,
    BlueUnicorn,
    GreenFrog,
    RedDragon,
    PinkFair;

    private static final List<Professor> values = List.of(values());
    
    /**
     * Gets the index value of the enum
     * @param professor The professor to find the index value of
     * @return The index value of the professor
     */
    public static int getRawValueOf(Professor professor) {
        return values.indexOf(professor);
    }
}
