package it.polimi.ingsw.server.exceptions.model;
/**
 this class represents an exception that is thrown when the player try to take a number of students from an object that doesn't contain the required number of Students
 @author Alessandro Sassi
 @see java.lang.Exception
 */
public class CollectionUnderflowError extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public CollectionUnderflowError() {
        super();
    }
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public CollectionUnderflowError(String message) {
        super(message);
    }
}
