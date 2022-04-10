package it.polimi.ingsw.model.match;

import it.polimi.ingsw.exceptions.IncorrectConstructorParametersException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.PlayerTeam;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.model.assistants.Wizard;

import java.util.ArrayList;
import java.util.List;

public class TeamMatchManager extends MatchManager {
	
	List<PlayerTeam> teams;
	
	public TeamMatchManager() {
		teams = new ArrayList<>();
	}
	
	@Override
	protected void addPlayer(String nickname, Wizard wiz, int maxTowerCount) throws IncorrectConstructorParametersException {
		if (teams.isEmpty() || teams.get(teams.size() - 1).getAllPlayers().size() == 2) {
			//We add a new Team if needed
			PlayerTeam team = new PlayerTeam();
			teams.add(team);
		}
		
		int numberOfTowers = 0;
		Tower towerType;
		if (teams.size() == 1) {
			towerType = Tower.Black;
		} else {
			towerType = Tower.White;
		}
		if (teams.get(teams.size() - 1).getAllPlayers().size() == 0) {
			numberOfTowers = maxTowerCount;
		}
		
		Player player = new Player(nickname, wiz, towerType, numberOfTowers);
		teams.get(teams.size() - 1).addPlayer(player);
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
		for (PlayerTeam team : teams) {
			result.add(team.getLeadPlayer());
		}
		return result;
	}
}
