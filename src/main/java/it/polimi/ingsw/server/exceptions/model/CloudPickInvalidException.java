package it.polimi.ingsw.server.exceptions.model;
/**
 this class rapresent an exception that is thrown when the player choose an empty cloud unless the Bag is also empty
 @Author Alessandro Sassi
 */
public class CloudPickInvalidException extends Exception {
	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 */
	public CloudPickInvalidException() {
	}
}
