package it.polimi.ingsw.server.exceptions.model;

/**
 * Class {@code UnavailableStopCardsException} is thrown when a Player tries to use a stopCard even though the card doesn't have one
 *
 * @see java.lang.Exception
 */
public class UnavailableStopCardsException extends Exception {
    
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public UnavailableStopCardsException(String message) {
        super(message);
    }
}