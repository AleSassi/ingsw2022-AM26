package it.polimi.ingsw.exceptions;

public class CollectionUnderflowError extends Exception {
    public CollectionUnderflowError() {
    }

    public CollectionUnderflowError(String message) {
        super(message);
    }
}
