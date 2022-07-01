package it.polimi.ingsw.server.exceptions.model;

/**
 this class represents an exception that is thrown when player tries to use a character card which has no more uses available
 @author Alessandro Sassi
 @see java.lang.Exception
 */
public class CharacterCardNoMoreUsesAvailableException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public CharacterCardNoMoreUsesAvailableException() {
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
    public CharacterCardNoMoreUsesAvailableException(String message) {
        super(message);
    }
}
