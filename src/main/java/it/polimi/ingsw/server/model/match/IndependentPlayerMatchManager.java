package it.polimi.ingsw.server.model.match;

import it.polimi.ingsw.server.exceptions.model.IncorrectConstructorParametersException;
import it.polimi.ingsw.server.exceptions.model.InvalidPlayerCountException;
import it.polimi.ingsw.server.exceptions.model.IslandSkippedInfluenceForStopCardException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.Wizard;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class represent the {@link it.polimi.ingsw.server.model.match.MatchManager MatchManager} in the case of single {@link it.polimi.ingsw.server.model.Player Players} {@code Match}
 *
 * @see MatchManager
 * @author Federico Albertini
 */
public class IndependentPlayerMatchManager extends MatchManager {

    private final List<Player> players;

    /**
     * Initializes an empty match
     */
    public IndependentPlayerMatchManager() {
        players = new ArrayList<>();
    }

    /**
     * Initialize the {@link it.polimi.ingsw.server.model.Player Player} with all the parameters and adds it to the match
     * @param nickname (type String) {@code Player's} nickname
     * @param wiz (type Wizard) chosen {@code Wizard}
     * @param maxTowerCount (type int) max number of {@link it.polimi.ingsw.server.model.Tower Tower}
     * @param initialCoins (type int) the number of coins the Player owns at the beginning of the game. Set it to -1 when the Coins feature is disabled
     * @throws InvalidPlayerCountException whenever the {@code Player count} isn't correct
     * @throws IncorrectConstructorParametersException whenever the {@code Parameters} of the constructor aren't correct
     */
    @Override
    protected void addPlayer(String nickname, Wizard wiz, int maxTowerCount, int initialCoins) throws InvalidPlayerCountException, IncorrectConstructorParametersException {
        if (players.size() > 3) throw new InvalidPlayerCountException();
        
        Tower tower = null;
        switch (players.size()) {
            case 0 -> tower = Tower.Black;
            case 1 -> tower = Tower.White;
            case 2 -> tower = Tower.Gray;
        }
        players.add(new Player(nickname, wiz, tower, maxTowerCount, initialCoins));
    }

    /**
     * Gets all the {@link it.polimi.ingsw.server.model.Player Players} of the {@code Match}
     * @return (type List Player) returns all the {@code Players}
     */
    @Override
    public List<Player> getAllPlayers() {
        // We should copy the Players list to avoid object modifications
        return new ArrayList<>(players);
    }

    /**
     * Gets just the {@link it.polimi.ingsw.server.model.Player Players} with {@link it.polimi.ingsw.server.model.Tower Tower}
     * @return (type List Player) returns all the {@code Players}
     */
    @Override
    protected List<Player> getPlayersWithTowers() {
        return getAllPlayers();
    }
    
    @Override
    protected Player getPlayerControllingIsland() throws IslandSkippedInfluenceForStopCardException {
        // Find the new player that controls the island
        for (Player player: getAllPlayers()) {
            int influence = getInfluenceOfPlayer(player);
            boolean playerControlsIsland = true;
            for (Player otherPlayer : getAllPlayers()) {
                if (!otherPlayer.equals(player)) {
                    if (getInfluenceOfPlayer(otherPlayer) >= influence) {
                        playerControlsIsland = false;
                        break;
                    }
                }
            }
            if (playerControlsIsland) {
                return player;
            }
        }
        return null;
    }
    
    @Override
    protected String getPlayerTeamName(Player player) {
        return null;
    }
}
