package it.polimi.ingsw.server.exceptions.model;

/**
 * Class {@code StudentMovementInvalidException} is thrown whenever a Player tries to make an illegal Student's movement
 */
public class StudentMovementInvalidException extends Exception {
	public StudentMovementInvalidException() {
	}
	
	public StudentMovementInvalidException(String message) {
		super(message);
	}
}
