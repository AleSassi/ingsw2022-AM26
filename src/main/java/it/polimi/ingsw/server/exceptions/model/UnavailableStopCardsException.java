package it.polimi.ingsw.server.exceptions.model;

/**
 * Class {@code UnavailableStopCardsException} si thrown when a Player tries to use a stopCard even thou he doesn't have one
 */
public class UnavailableStopCardsException extends Exception {
    public UnavailableStopCardsException(String message) {
        super(message);
    }
}