package it.polimi.ingsw.server.exceptions.model;
/**
 this class rapresent an exception that is thrown when player try tu use a character card
 which has no more uses available
 @Author Alessandro Sassi
 */
public class CharacterCardNoMoreUsesAvailableException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public CharacterCardNoMoreUsesAvailableException() {
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
