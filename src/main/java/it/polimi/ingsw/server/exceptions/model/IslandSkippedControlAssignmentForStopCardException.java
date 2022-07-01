package it.polimi.ingsw.server.exceptions.model;

/**
 * Class {@code IslandSkippedControlAssignmentForStopCardException} is thrown whenever an {@code Island's} control assignment needs to be skipped because on the {@code Island} is present the stop card
 *
 * @see java.lang.Exception
 */
public class IslandSkippedControlAssignmentForStopCardException extends Exception {
    
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public IslandSkippedControlAssignmentForStopCardException() {
        super();
    }
}
