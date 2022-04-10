package it.polimi.ingsw.model.match;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.assistants.Wizard;

import java.util.ArrayList;
import java.util.List;

public class TeamMatchManager extends MatchManager{

    List<PlayerTeam> teams = new ArrayList<>();

    @Override
    protected void addPlayer(String nickname, Wizard wiz) throws InvalidPlayerCountException, IncorrectConstructorParametersException {
        int numberOfTower = 0;
        if (teams.isEmpty() || teams.get(teams.size() - 1).getAllPlayers().size() == 2) {
            PlayerTeam team = new PlayerTeam();
            teams.add(team);
        }
        Tower towerType = null;
        if(teams.size() == 1) {
            towerType = Tower.Black;
        } else if(teams.size() == 2) {
            towerType = Tower.White;
        }
        if (teams.get(teams.size() - 1).getAllPlayers().size() == 0) {
            numberOfTower = getPawnCounts().getTowerPerPlayerCount();
        }
        Player player = new Player(nickname, wiz, towerType, numberOfTower);
        teams.get(teams.size()-1).addPlayer(player);
    }

    @Override
    protected List<Player> getAllPlayers() {
        List<Player> result = new ArrayList<>();
        for (PlayerTeam team : teams) {
            result.addAll(team.getAllPlayers());
        }
        return result;
    }

    @Override
    protected List<Player> getPlayersWithTowers() {
        List<Player> result = new ArrayList<>();
        result.add(teams.get(0).getLeadPlayer());
        result.add(teams.get(1).getLeadPlayer());
        return result;
    }


}
