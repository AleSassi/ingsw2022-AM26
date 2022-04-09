package it.polimi.ingsw.model.match;

import it.polimi.ingsw.exceptions.InvalidPlayerCountException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.model.assistants.Wizard;

import java.util.ArrayList;
import java.util.List;

public class IndependentPlayerMatchManager extends MatchManager {

    private final List<Player> players = new ArrayList<>();

    @Override
    protected void addPlayer(String nickname, Wizard wiz) throws InvalidPlayerCountException {
        Tower tower;
        switch (players.size()) {
            case 0 -> tower = Tower.Black;
            case 1 -> tower = Tower.White;
            case 2 -> tower = Tower.Gray;
            default -> throw new InvalidPlayerCountException();
        }
        Player player = new Player(nickname, wiz, tower,getPawnCounts().getTowerPerPlayerCount());
        players.add(player);
    }

    @Override
    protected List<Player> getAllPlayers() {
        return  players;
    }

    @Override
    protected List<Player> getPlayersWithTowers() {
        return getAllPlayers();
    }


}
