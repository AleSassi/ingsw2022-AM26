package it.polimi.ingsw.server.exceptions.model;
/**
 this class represents an exception that is thrown when the player tries to take an element from an empty collection
 @author Alessandro Sassi
 @see java.lang.Exception
 */
public class EmptyCollectionException extends Exception {
    
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public EmptyCollectionException(){
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
    public EmptyCollectionException(String message){
        super(message);
    }

}
