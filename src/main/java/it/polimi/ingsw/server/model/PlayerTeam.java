package it.polimi.ingsw.server.model;


import java.util.ArrayList;
import java.util.List;
/**
 * This Class represent the {@code PlayerTeam}, a set of Players that are part of the same team
 * @author Leonardo Betti
 */
public class PlayerTeam {

    private final ArrayList<Player> players = new ArrayList<>();
    private int leadPlayerIndex;
    
    /**
     * Adds a Player to the team
     * @param player The Player to add to the team
     */
    public void addPlayer(Player player) {
        players.add(player);
        if (player.getAvailableTowerCount() != 0) {
            leadPlayerIndex = players.size() - 1;
        }
    }

    /**
     * Gets the list of players part of the team
     * @return The list of {@link it.polimi.ingsw.server.model.Player players} part of the team
     */
    public List<Player> getAllPlayers() {
        // We should copy the Players list to avoid object modifications
        return new ArrayList<>(players);
    }
    
    /**
     * Determines if the team contains the player
     * @param player Th eplayer to check if part of the team
     * @return (type Bool) If the team contains the player
     */
    public boolean containsPlayer(Player player) {
        return players.contains(player);
    }

    /**
     * Gets the lead player, the one with touwers
     * @return the {@link it.polimi.ingsw.server.model.Player Lead player} who is the one with towers
     */
    public Player getLeadPlayer() {
        return players.get(leadPlayerIndex);
    }
}



