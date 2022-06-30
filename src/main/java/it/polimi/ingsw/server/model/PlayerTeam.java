package it.polimi.ingsw.server.model;


import java.util.ArrayList;
import java.util.List;

public class PlayerTeam {

    private final ArrayList<Player> players = new ArrayList<>();
    private int leadPlayerIndex;
    /**
     * add player to the team
     */
    public void addPlayer(Player player) {
        players.add(player);
        if (player.getAvailableTowerCount() != 0) {
            leadPlayerIndex = players.size() - 1;
        }
    }

    /**
     * return the list of player belonging to the team
     */
    public List<Player> getAllPlayers() {
        // We should copy the Players list to avoid object modifications
        return new ArrayList<>(players);
    }
    /**
     * the method take a player like parameter and check if this player is inside the team
     */
    public boolean containsPlayer(Player player) {
        return players.contains(player);
    }

    /**
     * return the player that have the tower
     */
    public Player getLeadPlayer() {
        return players.get(leadPlayerIndex);
    }
}



