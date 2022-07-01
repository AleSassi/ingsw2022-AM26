package it.polimi.ingsw.server.exceptions.server;

/**
 * Class {@code UnrecognizedCommandException} is thrown whenever the {@code Server} receives an unknown command
 *
 * @see java.lang.Exception
 */
public class UnrecognizedCommandException extends Exception {
	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 */
	public UnrecognizedCommandException() {
		super();
	}
}
