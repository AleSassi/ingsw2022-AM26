package it.polimi.ingsw.server.exceptions.model;

/**
 * Class {@code TooManyTowersException} is thrown whenever the Player attempts to take control of more {@code Towers} than the maximum number
 *
 * @see java.lang.Exception
 */
public class TooManyTowersException extends Exception {
    
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public TooManyTowersException() {
        super();
    }
}
