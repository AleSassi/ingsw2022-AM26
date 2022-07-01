package it.polimi.ingsw.server.exceptions.model;

/**
 * Class {@code NicknameNotUniqueException} is thrown when a player chooses an already existing nickname
 *
 * @see java.lang.Exception
 */
public class NicknameNotUniqueException extends Exception {
    
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public NicknameNotUniqueException() {
        super();
    }
}
