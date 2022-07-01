package it.polimi.ingsw.server.exceptions.model;
/**
 this class represents an exception that is thrown when player tries to place more towers than what are available
 @author Alessandro Sassi
 @see java.lang.Exception
 */
public class InsufficientTowersException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public InsufficientTowersException() {
        super();
    }
}
