package it.polimi.ingsw.server.model.match;

import it.polimi.ingsw.server.exceptions.model.IncorrectConstructorParametersException;
import it.polimi.ingsw.server.exceptions.model.InvalidPlayerCountException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.Wizard;

import java.util.ArrayList;
import java.util.List;

public class IndependentPlayerMatchManager extends MatchManager {

    private final List<Player> players;
    
    public IndependentPlayerMatchManager() {
        players = new ArrayList<>();
    }
    
    @Override
    protected void addPlayer(String nickname, Wizard wiz, int maxTowerCount) throws InvalidPlayerCountException, IncorrectConstructorParametersException {
        if (players.size() > 3) throw new InvalidPlayerCountException();
        
        Tower tower = null;
        switch (players.size()) {
            case 0 -> tower = Tower.Black;
            case 1 -> tower = Tower.White;
            case 2 -> tower = Tower.Gray;
        }
        players.add(new Player(nickname, wiz, tower, maxTowerCount));
    }

    @Override
    public List<Player> getAllPlayers() {
        // We should copy the Players list to avoid object modifications
        return new ArrayList<>(players);
    }

    @Override
    protected List<Player> getPlayersWithTowers() {
        return getAllPlayers();
    }
}
