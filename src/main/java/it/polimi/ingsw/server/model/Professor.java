package it.polimi.ingsw.server.model;

import java.util.*;

public enum Professor {
    YellowElf,
    BlueUnicorn,
    GreenFrog,
    RedDragon,
    PinkFair;

    private static final List<Professor> values = List.of(values());

    public static int getRawValueOf(Professor professor) {
        return values.indexOf(professor);
    }
}
