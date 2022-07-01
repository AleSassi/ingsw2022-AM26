package it.polimi.ingsw.server.exceptions.model;

/**
 * Class {@code StudentMovementInvalidException} is thrown whenever a Player tries to make an illegal Student's movement
 *
 * @see java.lang.Exception
 */
public class StudentMovementInvalidException extends Exception {
	
	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 */
	public StudentMovementInvalidException() {
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
	public StudentMovementInvalidException(String message) {
		super(message);
	}
}
