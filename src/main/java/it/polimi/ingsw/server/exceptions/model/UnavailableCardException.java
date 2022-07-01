package it.polimi.ingsw.server.exceptions.model;

/**
 * Class {@code UnavailableCardException} is thrown whenever a Player tries to use an unavailable Card
 *
 * @see java.lang.Exception
 */
public class UnavailableCardException extends Exception {
    
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public UnavailableCardException() {
        super();
    }
}
