package it.polimi.ingsw.server.exceptions.model;

/**
 * Class CharacterCardAlreadyInUseException is thrown whenever the Player attempts to use a Character Card which cannot be played since another player is already using it
 *
 * @see Exception
 */
public class CharacterCardAlreadyInUseException extends Exception {
    
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public CharacterCardAlreadyInUseException() {
        super();
    }
}
