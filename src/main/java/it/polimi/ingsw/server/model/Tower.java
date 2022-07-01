package it.polimi.ingsw.server.model;

import java.util.Arrays;

/**
 * An enum representing the possible tower colors
 */
public enum Tower {
    White,
    Gray,
    Black;
    
    /**
     * Gets the index of the tower (ordinal value)
     * @return The ordinal value of the enum
     */
    public int index() {
        return Arrays.stream(values()).toList().indexOf(this);
    }
}
