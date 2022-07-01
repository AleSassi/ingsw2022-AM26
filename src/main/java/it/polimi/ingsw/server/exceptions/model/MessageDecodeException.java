package it.polimi.ingsw.server.exceptions.model;

/**
 * Class {@code MessageDecodeException} is thrown whenever the {@code Network message's} decoding process failed
 *
 * @see java.lang.Exception
 */
public class MessageDecodeException extends Exception {
	
	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 */
	public MessageDecodeException() {
		super();
	}
}
