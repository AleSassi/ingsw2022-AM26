package it.polimi.ingsw.server.exceptions.model;

/**
 * Class {@code IslandSkippedInfluenceForStopCardException} is thrown whenever an {@code Island's} influence calculation needs to be skipped because on the {@code Island} is present the stop card
 *
 * @see java.lang.Exception
 */
public class IslandSkippedInfluenceForStopCardException extends Exception {
    
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public IslandSkippedInfluenceForStopCardException() {
        super();
    }
}
