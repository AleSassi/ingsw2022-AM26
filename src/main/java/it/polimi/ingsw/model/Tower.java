package it.polimi.ingsw.model;

import java.util.Arrays;

public enum Tower {
    White,
    Gray,
    Black;
    
    public int index() {
        return Arrays.stream(values()).toList().indexOf(this);
    }
}
