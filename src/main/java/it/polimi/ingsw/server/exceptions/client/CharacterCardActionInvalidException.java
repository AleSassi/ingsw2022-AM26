package it.polimi.ingsw.server.exceptions.client;

/**
 * Class {@code CharacterCardActionInvalidException} is thrown whenever a {@code CharacterCard} is used improperly
 */
public class CharacterCardActionInvalidException extends Exception {
	
	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 */
	public CharacterCardActionInvalidException() {
		super();
	}
}
