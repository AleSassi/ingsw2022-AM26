package it.polimi.ingsw.server.model.match;

import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.exceptions.model.*;

import java.util.*;

/**
 * This Class represents the Lobby of the Match
 */
public class GameLobby {

    private final int maxPlayerCount;
    private final MatchVariant variant;
    private final List<String> playersInLobby;
    private final List<Wizard> chosenWizards;
    private GameLobbyState state;

    /**
     * This constructor initializes the maxPlayerCount, playersInLobby, chosenWizards, the current {@code GameLobbyState} and the {@code Variant}
     * @param maxPlayerCount (type int) the maximum {@link it.polimi.ingsw.server.model.Player Player} count
     * @param variant (type MatchVariant) the {@link it.polimi.ingsw.server.model.match.MatchVariant MatchVariant} chose for the game
     */
    public GameLobby(int maxPlayerCount, MatchVariant variant) {
        this.maxPlayerCount = maxPlayerCount;
        playersInLobby = new ArrayList<>();
        chosenWizards = new ArrayList<>();
        this.state = GameLobbyState.FillableWithPlayers;
        this.variant = variant;
    }

    /**
     * Gets the number of {@link it.polimi.ingsw.server.model.Player Players} in this {@code GameLobby}
     * @return (type int) the number of {@code Players}
     */
    public int getNumberOfPlayers() {
        return playersInLobby.size();
    }

    /**
     * Gets the maximum {@link it.polimi.ingsw.server.model.Player Player} count
     * @return (type int) the maximum {@link it.polimi.ingsw.server.model.Player Player} count
     */
    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    /**
     * Gets the {@link it.polimi.ingsw.server.model.Player Players} nicknames, in order of entry
     * @return (type String[]) the PLayers nicknames
     */
    public String[] getPlayerNicknames() {
        return playersInLobby.toArray(new String[0]);
    }

    /**
     * Checks if the new logged in {@link it.polimi.ingsw.server.model.Player Player's} {@code Nickname} is unique
     * @param nickname (type String) {@code Nickname} to check
     * @return (type boolean) true if is unique
     */
    private boolean nicknameIsUnique(String nickname) {
        return !playersInLobby.contains(nickname);
    }

    /**
     * Gets the current {@code GameLobby's} state
     * @return (type GameLobbyState) the current {@code GameLobby's} state
     */
    public GameLobbyState getCurrentState() {
        return state;
    }

    /**
     * Gets the {@link it.polimi.ingsw.server.model.match.MatchVariant MatchVariant} chosen for this game
     * @return (type MatchVariant) the {@code MatchVariant} chosen for this game
     */
    public MatchVariant getVariant() {
        return variant;
    }

    /**
     * Adds the new {@link it.polimi.ingsw.server.model.Player Player} to the {@code GameLobby}
     * @param nickname (type String) {@code Player's} nickname
     * @param wiz (type Wizard) chosen {@code Wizard}
     * @throws LobbyFullException whenever a new {@code Players} tries to enter a full {@code GameLobby}
     * @throws NicknameNotUniqueException whenever the chosen {@code Nickname} is not unique
     * @throws WizardAlreadyChosenException whenever the chosen {@code Wizard} has already been chosen
     */
    public void addPlayer(String nickname, Wizard wiz) throws LobbyFullException, NicknameNotUniqueException, WizardAlreadyChosenException {
        if (state == GameLobbyState.Full || state == GameLobbyState.MatchRunning || playersInLobby.size() + 1 > maxPlayerCount) throw new LobbyFullException();
        if (!nicknameIsUnique(nickname)) throw new NicknameNotUniqueException();
        if (chosenWizards.contains(wiz)) throw new WizardAlreadyChosenException();
        playersInLobby.add(nickname);
        chosenWizards.add(wiz);
        if (playersInLobby.size() == maxPlayerCount) {
            state = GameLobbyState.Full;
        }
    }

    /**
     * Starts the game by initializing a new {@link it.polimi.ingsw.server.model.match.MatchManager MatchManager}
     * @return (type MatchManager) the {@code MatchManager}
     * @throws InvalidPlayerCountException whenever the {@code Player} count isn't correct
     * @throws IncorrectConstructorParametersException whenever the parameters aren't correct
     */
    public MatchManager startGame() throws InvalidPlayerCountException, IncorrectConstructorParametersException {
        if (state == GameLobbyState.Full) {
            MatchManager manager;
            if (getNumberOfPlayers() < 4) {
                manager = new IndependentPlayerMatchManager();
            } else {
                manager = new TeamMatchManager();
            }
            manager.startMatch(variant, playersInLobby, chosenWizards);
            state = GameLobbyState.MatchRunning;
            return manager;
        }
        return null;
    }

}
