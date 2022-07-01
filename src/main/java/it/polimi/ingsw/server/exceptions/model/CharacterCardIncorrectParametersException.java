package it.polimi.ingsw.server.exceptions.model;

import java.security.PrivilegedActionException;
/**
 this class rapresent an exception that is thrown when we send incorrect character card parameter
 @Author Alessandro Sassi
 */
public class CharacterCardIncorrectParametersException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public CharacterCardIncorrectParametersException() {
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public CharacterCardIncorrectParametersException(String message) {
        super(message);
    }
}
