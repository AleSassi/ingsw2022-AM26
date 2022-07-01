package it.polimi.ingsw.server.exceptions.model;
/**
 This class represents an exception that is thrown when the player chooses an empty cloud, unless the Bag is also empty
 @author Alessandro Sassi
 @see java.lang.Exception
 */
public class CloudPickInvalidException extends Exception {
	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 */
	public CloudPickInvalidException() {
		super();
	}
}
