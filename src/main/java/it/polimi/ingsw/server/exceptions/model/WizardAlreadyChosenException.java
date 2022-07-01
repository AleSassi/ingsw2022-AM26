package it.polimi.ingsw.server.exceptions.model;

/**
 * Class {@code WizardAlreadyChosenException} is thrown when a player chose an already chosen Wizard
 *
 * @see java.lang.Exception
 */
public class WizardAlreadyChosenException extends Exception {
	
	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 */
	public WizardAlreadyChosenException() {
		super();
	}
}
