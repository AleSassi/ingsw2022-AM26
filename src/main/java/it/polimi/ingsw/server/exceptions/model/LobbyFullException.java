package it.polimi.ingsw.server.exceptions.model;

/**
 * Class {@code LobbyFullException} is thrown whenever a Player tries to log in to a full lobby
 *
 * @see java.lang.Exception
 */
public class LobbyFullException extends Exception {
    
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public LobbyFullException() {
        super();
    }
}