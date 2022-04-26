package it.polimi.ingsw.server.exceptions.model;

public class CollectionUnderflowError extends Exception {
    public CollectionUnderflowError() {
    }

    public CollectionUnderflowError(String message) {
        super(message);
    }
}
