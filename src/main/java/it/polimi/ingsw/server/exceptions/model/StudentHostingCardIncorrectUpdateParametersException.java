package it.polimi.ingsw.server.exceptions.model;

/**
 * Class {@code StudentHostingCardIncorrectUpdateParametersException} is thrown whenever the parameters for a {@code CharacterHostingCard} are not correct
 *
 * @see java.lang.Exception
 */
public class StudentHostingCardIncorrectUpdateParametersException extends Exception {
    
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public StudentHostingCardIncorrectUpdateParametersException(String message) {
        super(message);
    }
}
