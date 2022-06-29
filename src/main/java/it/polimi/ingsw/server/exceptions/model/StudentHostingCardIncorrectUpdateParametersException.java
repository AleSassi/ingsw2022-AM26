package it.polimi.ingsw.server.exceptions.model;

/**
 * Class {@code StudentHostingCardIncorrectUpdateParametersException} is thrown whenever the parameters for a {@code CharacterHostingCard} are not correct
 */
public class StudentHostingCardIncorrectUpdateParametersException extends Exception {
    public StudentHostingCardIncorrectUpdateParametersException(String message) {
        super(message);
    }
}
