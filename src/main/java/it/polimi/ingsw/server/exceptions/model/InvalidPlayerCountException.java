package it.polimi.ingsw.server.exceptions.model;

/**
 * Class InvalidPlayerCountException is thrown whenever a match is created with a wrong Player count for the match type
 *
 * @see Exception
 */
public class InvalidPlayerCountException extends Exception{
    
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public InvalidPlayerCountException(){
        super();
    }
    
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public InvalidPlayerCountException(String message){
        super(message);
    }
}
