package it.polimi.ingsw.model;


import java.util.ArrayList;
import java.util.List;

public class PlayerTeam {

    private final ArrayList<Player> players = new ArrayList<>();
    private int leadPlayerIndex;

    public void addPlayer(Player player) {
        players.add(player);
        if (player.getAvailableTowerCount() != 0) {
            leadPlayerIndex = players.size() - 1;
        }
    }

    public List<Player> getAllPlayers() {
        // We should copy the Players list to avoid object modifications
        List<Player> result = new ArrayList<>();
        for (Player player: players) {
            result.add(player.copy());
        }
        return result;
    }

    public boolean containsPlayer(Player player) {
        return players.contains(player);
    }

    public Player getLeadPlayer() {
        return players.get(leadPlayerIndex);
    }
}



