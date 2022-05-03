package it.polimi.ingsw.server.model;

import it.polimi.ingsw.utils.cli.ANSIColors;

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
    
    public ANSIColors getProfessorColor() {
        switch (this) {
            case YellowElf -> {
                return ANSIColors.Yellow;
            }
            case BlueUnicorn -> {
                return ANSIColors.Blue;
            }
            case GreenFrog -> {
                return ANSIColors.Green;
            }
            case RedDragon -> {
                return ANSIColors.Red;
            }
            case PinkFair -> {
                return ANSIColors.Pink;
            }
        }
        return ANSIColors.Red;
    }
}
