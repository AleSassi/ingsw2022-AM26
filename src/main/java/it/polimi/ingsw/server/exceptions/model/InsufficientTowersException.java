package it.polimi.ingsw.server.exceptions.model;
/**
 this class rapresent an exception that is thrown when player try to take more towers than are available
 @Author Alessandro Sassi
 */
public class InsufficientTowersException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public InsufficientTowersException() {
    }
}
