package it.polimi.ingsw.model.match;

import it.polimi.ingsw.exceptions.CollectionUnderflowError;
import it.polimi.ingsw.exceptions.InvalidPlayerCountException;
import it.polimi.ingsw.exceptions.LobbyFullException;
import it.polimi.ingsw.exceptions.NicknameNotUniqueException;
import it.polimi.ingsw.model.assistants.Wizard;

import java.util.*;

public class GameLobby {

    private final int maxPlayerCount;
    private final MatchVariant variant;
    private final List<String> playersInLobby;
    private final List<Wizard> chosenWizards;
    private GameLobbyState state;

    public GameLobby(int maxPlayerCount, MatchVariant variant) {
        this.maxPlayerCount = maxPlayerCount;
        playersInLobby = new ArrayList<>();
        chosenWizards = new ArrayList<>();
        this.state = GameLobbyState.FillableWithPlayers;
        this.variant = variant;
    }

    public int getNumberOfPlayers() {
        return playersInLobby.size();
    }

    private boolean nicknameIsUnique(String nickname) {
        return !playersInLobby.contains(nickname);
    }

    public GameLobbyState getCurrentState() {
        return state;
    }

    public void addPlayer(String nickname, Wizard wiz) throws LobbyFullException, NicknameNotUniqueException {
        if (state == GameLobbyState.Full || state == GameLobbyState.MatchRunning || playersInLobby.size() + 1 > maxPlayerCount) throw new LobbyFullException();
        if (!nicknameIsUnique(nickname)) throw new NicknameNotUniqueException();
        playersInLobby.add(nickname);
        chosenWizards.add(wiz);
        if (playersInLobby.size() == maxPlayerCount) {
            state = GameLobbyState.Full;
        }
    }

    public MatchManager startGame() throws InvalidPlayerCountException, CollectionUnderflowError {
        if (state == GameLobbyState.Full) {
            MatchManager manager;
            if (getNumberOfPlayers() < 4) {
                manager = new IndependentPlayerMatchManager();
            } else {
                manager = new TeamMatchManager();
            }
            manager.setUpMatch(variant, playersInLobby, chosenWizards);
            state = GameLobbyState.MatchRunning;
            return manager;
        }
        return null;
    }

}
