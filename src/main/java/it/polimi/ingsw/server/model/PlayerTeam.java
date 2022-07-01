package it.polimi.ingsw.server.model;


import java.util.ArrayList;
import java.util.List;
/**
 * This Class represent the {@code PlayerTeam}
 * @author Leonardo Betti
 */
public class PlayerTeam {

    private final ArrayList<Player> players = new ArrayList<>();
    private int leadPlayerIndex;
    /** add the
     *@param player (type {@link it.polimi.ingsw.server.model.Player player}) to the (@Code Playteam)
     */
    public void addPlayer(Player player) {
        players.add(player);
        if (player.getAvailableTowerCount() != 0) {
            leadPlayerIndex = players.size() - 1;
        }
    }

    /**
     * getter, return all the {@link it.polimi.ingsw.server.model.Player player} inside team
     */
    public List<Player> getAllPlayers() {
        // We should copy the Players list to avoid object modifications
        return new ArrayList<>(players);
    }
    /** @return (type Bool) if
     *@param player (type {@link it.polimi.ingsw.server.model.Player player}) is inside the team
     */
    public boolean containsPlayer(Player player) {
        return players.contains(player);
    }

    /**
     getter, return the{@link it.polimi.ingsw.server.model.Player player} who is the leader
     */
    public Player getLeadPlayer() {
        return players.get(leadPlayerIndex);
    }
}



