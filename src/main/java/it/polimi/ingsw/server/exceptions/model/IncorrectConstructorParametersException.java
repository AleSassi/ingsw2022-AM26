package it.polimi.ingsw.server.exceptions.model;

/**
 * Class IncorrectConstructorParametersException is thrown whenever a constructor is initialized with wrong parameters
 *
 * @see Exception
 */
public class IncorrectConstructorParametersException extends Exception {
	
	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 */
	public IncorrectConstructorParametersException() {
		super();
	}
}
