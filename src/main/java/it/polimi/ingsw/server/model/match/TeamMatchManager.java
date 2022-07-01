package it.polimi.ingsw.server.model.match;

import it.polimi.ingsw.server.exceptions.model.IncorrectConstructorParametersException;
import it.polimi.ingsw.server.exceptions.model.IslandSkippedInfluenceForStopCardException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.PlayerTeam;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.Wizard;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class represent the {@link  MatchManager} in case of team {@code Match}
 */
public class TeamMatchManager extends MatchManager {
	
	List<PlayerTeam> teams;
	
	/**
	 * Initializes a new, empty team match
	 */
	public TeamMatchManager() {
		teams = new ArrayList<>();
	}

	/**
	 * Initialize the {@link Player} and adds it to the team in the order of login
	 * @param nickname (type String) {@code Player's} nickname
	 * @param wiz (type Wizard) chosen {@code Wizard}
	 * @param maxTowerCount (type int) max number of {@code Tower}
	 * @param initialCoins (type int) the number of coins the Player owns at the beginning of the game. Set it to -1 when the Coins feature is disabled
	 * @throws IncorrectConstructorParametersException whenever the {@code Parameters} of the constructor aren't correct
	 */
	@Override
	protected void addPlayer(String nickname, Wizard wiz, int maxTowerCount, int initialCoins) throws IncorrectConstructorParametersException {
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
		
		Player player = new Player(nickname, wiz, towerType, numberOfTowers, initialCoins);
		teams.get(teams.size() - 1).addPlayer(player);
	}

	/**
	 * Gets all the {@link Player} of the {@code Match}
	 * @return (type List of Player) returns all the {@code Players}
	 */
	@Override
	public List<Player> getAllPlayers() {
		List<Player> result = new ArrayList<>();
		for (PlayerTeam team : teams) {
			result.addAll(team.getAllPlayers());
		}
		return result;
	}
	/**
	 * Gets just the {@link Player} with {@link Tower}
	 * @return (type List of Player) returns all the {@code Players}
	 */
	@Override
	protected List<Player> getPlayersWithTowers() {
		List<Player> result = new ArrayList<>();
		for (PlayerTeam team : teams) {
			result.add(team.getLeadPlayer());
		}
		return result;
	}
	
	@Override
	protected Player getPlayerControllingIsland() throws IslandSkippedInfluenceForStopCardException {
		// Find the new team that controls the island - return the Player with the Towers
		for (PlayerTeam playerTeam: teams) {
			int totalTeamInfluence = getTeamInfluenceOnIsland(playerTeam);
			boolean teamControlsIsland = true;
			for (PlayerTeam otherTeam : teams) {
				if (!otherTeam.equals(playerTeam)) {
					if (getTeamInfluenceOnIsland(otherTeam) >= totalTeamInfluence) {
						teamControlsIsland = false;
						break;
					}
				}
			}
			if (teamControlsIsland) {
				return playerTeam.getLeadPlayer();
			}
		}
		return null;
	}
	
	/**
	 * Gets the influence of a Team on an Island
	 * @param playerTeam The team to get the influence of
	 * @return The influence of the team
	 * @throws IslandSkippedInfluenceForStopCardException If the island was skipped because there was a stop card on it
	 */
	private int getTeamInfluenceOnIsland(PlayerTeam playerTeam) throws IslandSkippedInfluenceForStopCardException {
		int totalTeamInfluence = 0;
		for (Player player: playerTeam.getAllPlayers()) {
			totalTeamInfluence += getInfluenceOfPlayer(player);
		}
		return totalTeamInfluence;
	}
	
	@Override
	protected String getPlayerTeamName(Player player) {
		int teamIndex = 0;
		for (PlayerTeam team: teams) {
			if (team.getAllPlayers().contains(player)) {
				break;
			}
			teamIndex += 1;
		}
		return "Team " + (teamIndex + 1);
	}
}
