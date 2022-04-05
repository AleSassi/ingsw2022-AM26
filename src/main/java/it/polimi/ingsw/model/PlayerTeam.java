package it.polimi.ingsw.model;


import java.util.ArrayList;

public class PlayerTeam {

    private final ArrayList<Player> players = new ArrayList<>();
    private int leadPlayerIndex;

    public void addPlayer(Player player) {
        players.add(player);
        if (player.getAvailableTowerCount() != 0) {
            leadPlayerIndex = players.size() - 1;
        }
    }

    public ArrayList<Player> getAllPlayers() {
        return players;
    }

    public boolean containsPlayer(Player player) {
        return players.contains(player);
    }

    public Player getLeadPlayer() {
        return players.get(leadPlayerIndex);
    }
}



