package it.polimi.ingsw.server.exceptions.model;

/**
 * Class CharacterCardNotPurchasedException is thrown whenever the Player attempts to use a Character Card which has not been purchased by the Player
 *
 * @see Exception
 */
public class CharacterCardNotPurchasedException extends Exception {
	
	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 */
	public CharacterCardNotPurchasedException() {
		super();
	}
}
