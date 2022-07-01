package it.polimi.ingsw.server.exceptions.model;
/**
 this class rapresent an exception that is thrown when the player try to take a number of  student from an object that doesen't cointain required number of Students
 @Author Alessandro Sassi
 */
public class CollectionUnderflowError extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public CollectionUnderflowError() {
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
