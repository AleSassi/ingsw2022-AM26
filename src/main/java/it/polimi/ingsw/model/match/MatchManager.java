package it.polimi.ingsw.model.match;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Professor;
import it.polimi.ingsw.model.TableManager;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.model.assistants.Wizard;
import it.polimi.ingsw.model.characters.CharacterCard;
import it.polimi.ingsw.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.model.student.Student;
import it.polimi.ingsw.model.student.StudentCollection;

import java.util.Comparator;
import java.util.List;

public abstract class MatchManager {
 
	private List<Player> playersSortedByCurrentTurnOrder;
	private int currentLeadPlayer;
	private MatchPhase matchPhase;
	private TableManager managedTable;
	private PawnCounts pawnCounts;
	
	//region Public methods (used by the Controller to run actions)
	/**
	 * Constructs and sets up the match by adding all players, initializing the TableManager and initializing the entrance for each player
	 * @param variant Choose by the first player, Basic or Experts
	 */
	public void startMatch(MatchVariant variant, List<String> playersNicknames, List<Wizard> wiz) throws InvalidPlayerCountException, IncorrectConstructorParametersException {
		int playerCount = playersNicknames.size();
		if (playerCount > 4) throw new InvalidPlayerCountException("MatchManager ERROR: the number of Players in a Match must be < 4. Got" + playerCount);
		
		pawnCounts = new PawnCounts(playerCount);
		managedTable = new TableManager(pawnCounts.getCloudTileCount(), variant == MatchVariant.ExpertRuleSet);
		
		for (int i = 0; i < playerCount; i++) {
			addPlayer(playersNicknames.get(i), wiz.get(i), pawnCounts.getTowersPerPlayer());
		}
		for (Player player : getAllPlayers()) {
			initEntrance(player);
		}
		// Initialize the lead player
		currentLeadPlayer = 0;
		playersSortedByCurrentTurnOrder = getAllPlayers();
		// The PlanPhaseOne match phase must be executed automatically
		try {
			PP_FillCloudCards();
		} catch (CollectionUnderflowError e) {
			// It will never happen, but if it does we print its stack trace
			e.printStackTrace();
		}
		matchPhase = MatchPhase.PlanPhaseStepTwo;
	}
	
	/**
	 * Manage the course of the game depending on the match phase
	 *
	 * @param AssistantCardIndex Index of the Assistant card to play
	 * @param studentToMove      Student type to move
	 * @param islandDestination  Island idx to move to the students
	 * @param moveToIsland       Move to Island or To dining room
	 * @param motherNatureSteps  Steps to move mother nature
	 * @param cloudIdx           Cloud's idx from which to pick the students
	 */
	public void runAction(int AssistantCardIndex, Student studentToMove, int islandDestination, boolean moveToIsland, int motherNatureSteps, int cloudIdx) throws StudentMovementInvalidException, AssistantCardNotPlayableException {
		switch (matchPhase) {
			case PlanPhaseStepTwo -> PP_PlayAssistantCard(AssistantCardIndex);
			case ActionPhaseStepOne -> {
				if (moveToIsland) {
					AP_MoveStudentToIsland(studentToMove, islandDestination);
				} else {
					AP_MoveStudentToDiningRoom(studentToMove);
				}
			}
			case ActionPhaseStepTwo -> {
				AP_MoveMotherNatureBySteps(motherNatureSteps);
				AP_CheckAndChangeCurrentIslandControl();
			}
			case ActionPhaseStepThree -> AP_CollectAllStudentsFromCloud(cloudIdx);
		}
		if (matchPhase == MatchPhase.ActionPhaseStepThree && moveToNextPlayer()) {
			roundCheckMatchEnd();
		}
	}
	
	/**
	 * Change the currentPlayer to the next player in the playerSortedByCurrentOrder and change the phase according to the rules
	 */
	public boolean moveToNextPlayer() {
		if (currentLeadPlayer == playersSortedByCurrentTurnOrder.size() - 1) {
			if (matchPhase == MatchPhase.PlanPhaseStepTwo) {
				playersSortedByCurrentTurnOrder = getPlayersSortedByRoundTurnOrder();
				currentLeadPlayer = 0;
			} else if (matchPhase == MatchPhase.ActionPhaseStepThree) {
				currentLeadPlayer = 0;
				// Perform planPhaseStepOne automatically
				try {
					PP_FillCloudCards();
				} catch (CollectionUnderflowError e) {
					System.out.println("MatchManager WARNING: Insufficient students in the Bag, the Cloud will have no students on it");
				}
			}
			matchPhase = matchPhase.nextPhase();
			return true;
		} else {
			if (matchPhase == MatchPhase.ActionPhaseStepThree) {
				matchPhase = MatchPhase.ActionPhaseStepOne;
				currentLeadPlayer++;
			} else if (matchPhase == MatchPhase.PlanPhaseStepTwo) {
				currentLeadPlayer++;
			} else {
				matchPhase = matchPhase.nextPhase();
			}
			return false;
		}
	}
	
	/**
	 * Sort the players by the assistant card priority number (the lowest first) for the next round
	 *
	 * @return the list of ordered player
	 */
	public List<Player> getPlayersSortedByRoundTurnOrder() {
		//TODO: Need to alter the sorting lambda to account for when the Player has the same priority number
		List<Player> result = getAllPlayers();
		result.sort(Comparator.comparingInt(player -> (player.getLastPlayedAssistantCard().getPriorityNumber() - player.getAssistantCardOrderModifier())));
		return result;
	}
	
	/**
	 * @return the current playing player
	 */
	public Player getCurrentPlayer() {
		return playersSortedByCurrentTurnOrder.get(currentLeadPlayer);
	}
	
	/**
	 * Checks if the assistant card is playable by checking the card that has been played by the lst player is the same as the one he wants to play
	 */
	public boolean isAssistantCardPlayable(int cardIdxForCurrentPlayer) {
		boolean result = true;
		for (Player p : getAllPlayers()) {
			if (p.getLastPlayedAssistantCard() == getCurrentPlayer().getAvailableAssistantCards().get(cardIdxForCurrentPlayer)) {
				result = false;
				break;
			}
		}
		boolean isOnlyOnePlayable = getAllPlayers().stream().filter((p) -> !p.equals(getCurrentPlayer())).map(Player::getLastPlayedAssistantCard).toList().containsAll(getCurrentPlayer().getAvailableAssistantCards());
		return result || isOnlyOnePlayable;
	}
	//endregion
	
	//region Public CharacterCard methods
	/**
	 * Purchase the character card
	 */
	public boolean purchaseCharacterCards(int cardIndex) {
		CharacterCard card = managedTable.getCardAtIndex(cardIndex);
		return getCurrentPlayer().playCharacterCard(card);
	}
	
	/**
	 * Executes the relative effect of the card that the player played
	 */
	public void useCharacterCard(CharacterCardParamSet userInfo) throws CharacterCardIncorrectParametersException, CharacterCardNoMoreUsesAvailableException {
		if (getCurrentPlayer().getActiveCharacterCard() != null) {

			getCurrentPlayer().getActiveCharacterCard().useCard(managedTable, getAllPlayers(), getCurrentPlayer(), userInfo);
		}
	}
	//endregion
	
	//region Abstract methods
	/**
	 * Create and adds the player with the nickname and wizard
	 */
	protected abstract void addPlayer(String nickname, Wizard wiz, int maxTowerCount) throws InvalidPlayerCountException, IncorrectConstructorParametersException;
	
	/**
	 * return the list of all player in order of addition
	 */
	protected abstract List<Player> getAllPlayers();
	
	/**
	 * @return The list of players with towers
	 */
	protected abstract List<Player> getPlayersWithTowers();
	//endregion

	//region TestMethod
	public void setMatchPhase(MatchPhase matchPhase) {
		this.matchPhase = matchPhase;
	}

	public TableManager getManagedTable() {
		return managedTable;
	}
	//endregion

	//region Private methods & Game Phase methods
	
	/**
	 * Initialize Player's p entrance
	 */
	private void initEntrance(Player p) {
		try {
			p.addAllStudentsToEntrance(managedTable.pickStudentsFromBag(pawnCounts.getStudentsMovedToRoom()));
		} catch (CollectionUnderflowError e) {
			// This exception should never be raised, since the Bag should always have more than enough Students to initialize all Players
			e.printStackTrace();
		}
	}
	
	/**
	 * Picks the students from the bag and places them on the clouds depending on the number of players
	 */
	private void PP_FillCloudCards() throws CollectionUnderflowError {
		int numberOfStudent = pawnCounts.getStudentsDrawnFromCloud();
		StudentCollection tmp;
		for (int cloudIdx = 0; cloudIdx < managedTable.getNumberOfClouds(); cloudIdx++) {
			tmp = managedTable.pickStudentsFromBag(numberOfStudent);
			for (Student s : Student.values()) {
				managedTable.placeStudentOnCloud(s, cloudIdx, tmp.getCount(s));
			}
		}
	}
	
	/**
	 * Check if the Assistant card is playable and plays it
	 */
	private void PP_PlayAssistantCard(int cardIndex) throws AssistantCardNotPlayableException {
		if (!isAssistantCardPlayable(cardIndex)) throw new AssistantCardNotPlayableException();
		try {
			playersSortedByCurrentTurnOrder.get(currentLeadPlayer).playAssistantCardAtIndex(cardIndex);
			//If the Assistant card has already been played, the current Player must play after the other
			if (getCurrentPlayer().getLastPlayedAssistantCard() == null) {
				int numberOfSameCardsOnTable = (int) getAllPlayers().stream().filter((player) -> !player.equals(getCurrentPlayer())).map(Player::getLastPlayedAssistantCard).filter((card) -> card.equals(getCurrentPlayer().getLastPlayedAssistantCard())).count();
				boolean isAlreadyPlayed = numberOfSameCardsOnTable > 1;
				if (isAlreadyPlayed) {
					getCurrentPlayer().setAssistantCardOrderModifier(numberOfSameCardsOnTable);
				}
			}
		} catch (CollectionUnderflowError | IndexOutOfBoundsException e) {
			throw new AssistantCardNotPlayableException();
		}
	}
	
	/**
	 * Moves the student s from the Player entrance space to the islandIdx island
	 */
	private void AP_MoveStudentToIsland(Student s, int islandIdx) throws StudentMovementInvalidException {
		try {
			getCurrentPlayer().removeStudentFromEntrance(s);
			managedTable.placeStudentOnIsland(s, islandIdx);
		} catch (CollectionUnderflowError e) {
			throw new StudentMovementInvalidException("ActionPhase ERROR: the student " + s + " cannot be moved to island " + islandIdx + " since the Player does not have such Student in its Entrance space");
		}
	}
	
	/**
	 * Moves the student s to the dining room
	 */
	private void AP_MoveStudentToDiningRoom(Student s) throws StudentMovementInvalidException {
		try {
			getCurrentPlayer().removeStudentFromEntrance(s);
			getCurrentPlayer().placeStudentAtTableAndGetCoin(s);
			AP_CheckAndAssignProfessorTo(s);
		} catch (CollectionUnderflowError e) {
			throw new StudentMovementInvalidException("ActionPhase ERROR: the student " + s + "cannot be moved to its table in the Dining Room since the Player does not have such Student in its Entrance space");
		} catch (TableFullException e) {
			throw new StudentMovementInvalidException("ActionPhase ERROR: the student " + s + "cannot be moved to its table in the Dining Room since the Table is already full");
		}
	}
	
	/**
	 * Picks all the students from the cloud and place them in the player's entrance
	 *
	 * @param cloudIdx clouds's id
	 */
	private void AP_CollectAllStudentsFromCloud(int cloudIdx) {
		getCurrentPlayer().addAllStudentsToEntrance(managedTable.pickStudentsFromCloud(cloudIdx));
	}
	
	/**
	 * Moves Mother Nature by the step and checks if there are any modifier activated by the active character cards
	 */
	private void AP_MoveMotherNatureBySteps(int steps) {
		int stepsWithCardModifier = steps;
		if (getCurrentPlayer().getActiveCharacterCard() != null && getCurrentPlayer().getActiveCharacterCard().getCharacter().getChangesMNSteps()) {
			try {
				stepsWithCardModifier += getCurrentPlayer().getActiveCharacterCard().useCard(managedTable, null, getCurrentPlayer(), null);
			} catch (CharacterCardIncorrectParametersException | CharacterCardNoMoreUsesAvailableException e) {
				// Do nothing, the Steps will remain the default
			}
		}
		managedTable.moveMotherNature(stepsWithCardModifier);
	}
	
	/**
	 * Checks the number of students checkedStudent and assign the relative professor to the player
	 */
	private void AP_CheckAndAssignProfessorTo(Student checkedStudent) {
		Professor associatedProf = checkedStudent.getAssociatedProfessor();
		int controlledStudentCount = getCurrentPlayer().getCountAtTable(checkedStudent);
		if (getCurrentPlayer().getActiveCharacterCard() != null && getCurrentPlayer().getActiveCharacterCard().getCharacter().getChangesProfControl()) {
			try {
				controlledStudentCount += getCurrentPlayer().getActiveCharacterCard().useCard(managedTable, getAllPlayers(), getCurrentPlayer(), null);
			} catch (CharacterCardIncorrectParametersException | CharacterCardNoMoreUsesAvailableException e) {
				// Do nothing, the number of controlled students will stay the same
			}
		}
		// Here we check if the control should change and change the Professor control
		for (Player otherPlayer : getAllPlayers()) {
			if (!otherPlayer.getNickname().equals(getCurrentPlayer().getNickname())) {
				if (otherPlayer.getCountAtTable(checkedStudent) < controlledStudentCount) {
					if (managedTable.isProfessorAvailable(associatedProf)) {
						managedTable.removeProfessor(associatedProf);
						getCurrentPlayer().addProfessor(associatedProf);
					} else if (otherPlayer.getControlledProfessors().contains(associatedProf)) {
						otherPlayer.removeProfessor(associatedProf);
						getCurrentPlayer().addProfessor(associatedProf);
					}
				}
			}
		}
		
	}
	
	/**
	 * Checks the influence on the current Island and in case assigns the control to a player
	 */
	private void AP_CheckAndChangeCurrentIslandControl() {
		try {
			int currentPlayerInfluence = managedTable.getInfluenceOnCurrentIsland(getCurrentPlayer());
			Player newIslandOwner = getCurrentPlayer();
			for (Player p : getAllPlayers()) {
				if (p != getCurrentPlayer()) {
					if (currentPlayerInfluence < managedTable.getInfluenceOnCurrentIsland(p)) {
						newIslandOwner = p;
					}
				}
			}
			Tower currentIslandTower = managedTable.getCurrentIsland().getActiveTowerType();
			if (currentIslandTower == null) {
				managedTable.changeControlOfCurrentIsland(null, newIslandOwner);
			} else if (currentIslandTower != newIslandOwner.getTowerType()) {
				Player teammateWithSameTowers = newIslandOwner;
				Player oldIslandOwner = null;
				for (Player p : getPlayersWithTowers()) {
					if (p.getTowerType() == newIslandOwner.getTowerType() && p.getAvailableTowerCount() > 0) {
						// P is the teammate with the Towers
						teammateWithSameTowers = p;
					} else if (p.getTowerType() == currentIslandTower && p.getAvailableTowerCount() > 0) {
						// P is the old owner of the Island
						oldIslandOwner = p;
					}
				}
				managedTable.changeControlOfCurrentIsland(oldIslandOwner, teammateWithSameTowers);
			}
		} catch (IslandSkippedInfluenceForStopCardException | IslandSkippedControlAssignmentForStopCardException e) {
			// If we skipped the Island due to the StopCard being there, we should not do anything
		}
	}
	
	/**
	 * Check the Match End conditions: Player has no more tower, no students left in the bag, no more assistant cards available
	 */
	private void roundCheckMatchEnd() {
		for (Player p : getAllPlayers()) {
			if (p.getAvailableAssistantCards().size() == 0 || managedTable.isBagEmpty()) {
				//TODO: Notify Match End
				break;
			}
		}
	}

	//endregion
}
