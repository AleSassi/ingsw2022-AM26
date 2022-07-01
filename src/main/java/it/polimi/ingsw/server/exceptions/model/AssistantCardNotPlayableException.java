package it.polimi.ingsw.server.exceptions.model;

/**
 * Class AssistantCardNotPlayableException is thrown whenever the Player attempts to use an Assistant Card which cannot be played since another player already used it in the same turn
 *
 * @see Exception
 */
public class AssistantCardNotPlayableException extends Exception {
    
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public AssistantCardNotPlayableException(){
        super();
    }
}

