package it.polimi.ingsw.server.exceptions.model;

/**
 * Class {@code TableFullException} is thrown whenever the dining room is full
 *
 * @see java.lang.Exception
 */
public class TableFullException extends Exception {
	
	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 */
	public TableFullException() {
		super();
	}
}
