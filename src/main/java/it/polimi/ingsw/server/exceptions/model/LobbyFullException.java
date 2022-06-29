package it.polimi.ingsw.server.exceptions.model;

/**
 * Class {@code LobbyFullException} is thrown whenever a Player tries to log in to a full lobby
 */
public class LobbyFullException extends Exception {
    public LobbyFullException() {
    }
}