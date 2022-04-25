package it.polimi.ingsw.exceptions.model;

public class CollectionUnderflowError extends Exception {
    public CollectionUnderflowError() {
    }

    public CollectionUnderflowError(String message) {
        super(message);
    }
}
