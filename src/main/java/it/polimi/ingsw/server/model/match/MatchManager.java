package it.polimi.ingsw.server.model.match;

import it.polimi.ingsw.server.controller.network.messages.CharacterCardNetworkParamSet;
import it.polimi.ingsw.server.controller.network.messages.PlayerStateMessage;
import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;
import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.exceptions.model.*;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.TableManager;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.characters.CharacterCard;
import it.polimi.ingsw.server.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.server.model.student.Cloud;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;
import it.polimi.ingsw.server.model.student.StudentHost;

import java.util.*;

public abstract class MatchManager {
 
	private List<Player> playersSortedByCurrentTurnOrder;
	private int currentLeadPlayer;
	private MatchPhase matchPhase;
	private TableManager managedTable;
	private PawnCounts pawnCounts;
	private int numberOfStudentsPickedByCurrentPlayer_AP1 = 0;
	
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
		
		// Register for notification listening
		NotificationCenter.shared().addObserver(this::didReceiveMatchVictoryNotification, NotificationName.PlayerVictory, managedTable);
		for (Player player: getAllPlayers()) {
			NotificationCenter.shared().addObserver(this::didReceiveMatchVictoryNotification, NotificationName.PlayerVictory, player);
		}
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
	public void runAction(int AssistantCardIndex, Student studentToMove, int islandDestination, boolean moveToIsland, int motherNatureSteps, int cloudIdx) throws StudentMovementInvalidException, AssistantCardNotPlayableException, CloudPickInvalidException {
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
			case ActionPhaseStepThree -> {
				AP_CollectAllStudentsFromCloud(cloudIdx);
				if (getCurrentPlayer().getActiveCharacterCard() != null) {
					getCurrentPlayer().getActiveCharacterCard().deactivate();
				}
			}
		}
		if (moveToNextPlayer() && matchPhase == MatchPhase.ActionPhaseStepThree) {
			roundCheckMatchEnd();
		}
	}
	
	/**
	 * Change the currentPlayer to the next player in the playerSortedByCurrentOrder and change the phase according to the rules
	 */
	public boolean moveToNextPlayer() {
		switch (matchPhase) {
			case PlanPhaseStepTwo -> {
				if (currentLeadPlayer == playersSortedByCurrentTurnOrder.size() - 1) {
					playersSortedByCurrentTurnOrder = getPlayersSortedByRoundTurnOrder();
					currentLeadPlayer = 0;
					matchPhase = matchPhase.nextPhase();
					return true;
				} else {
					currentLeadPlayer += 1;
				}
			}
			case ActionPhaseStepOne -> {
				numberOfStudentsPickedByCurrentPlayer_AP1 += 1;
				if (numberOfStudentsPickedByCurrentPlayer_AP1 == 3) {
					matchPhase = matchPhase.nextPhase();
				}
			}
			case ActionPhaseStepTwo -> matchPhase = matchPhase.nextPhase();
			case ActionPhaseStepThree -> {
				numberOfStudentsPickedByCurrentPlayer_AP1 = 0;
				boolean startsNewRound = false;
				if (currentLeadPlayer == playersSortedByCurrentTurnOrder.size() - 1) {
					currentLeadPlayer = 0;
					// Perform planPhaseStepOne automatically
					try {
						PP_FillCloudCards();
					} catch (CollectionUnderflowError e) {
						System.out.println("MatchManager WARNING: Insufficient students in the Bag, the Cloud will have no students on it");
					}
					startsNewRound = true;
					matchPhase = MatchPhase.PlanPhaseStepTwo;
				} else {
					currentLeadPlayer += 1;
					matchPhase = MatchPhase.ActionPhaseStepOne;
				}
				return startsNewRound;
			}
		}
		return false;
	}
	
	/**
	 * Sort the players by the assistant card priority number (the lowest first) for the next round
	 *
	 * @return the list of ordered player
	 */
	public List<Player> getPlayersSortedByRoundTurnOrder() {
		//TODO: Need to alter the sorting lambda to account for when the Player has the same priority number
		List<Player> result = getAllPlayers();
		result.sort((playerA, playerB) -> (playerB.getLastPlayedAssistantCard().getPriorityNumber() - playerB.getAssistantCardOrderModifier()) - (playerA.getLastPlayedAssistantCard().getPriorityNumber() - playerA.getAssistantCardOrderModifier()));
		return result;
	}
	
	/**
	 * @return the current playing player
	 */
	public Player getCurrentPlayer() {
		return playersSortedByCurrentTurnOrder.get(currentLeadPlayer);
	}
	
	public MatchPhase getMatchPhase() {
		return matchPhase;
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
	public int useCharacterCard(CharacterCardNetworkParamSet userInfo) throws CharacterCardIncorrectParametersException, CharacterCardNoMoreUsesAvailableException, CharacterCardNotPurchasedException {
		if (getCurrentPlayer().getActiveCharacterCard() != null) {
			StudentHost sourceStudentHost = managedTable.getIslandAtIndex(userInfo.getSourceIslandIndex());
			StudentHost destinationStudentHost = managedTable.getIslandAtIndex(userInfo.getTargetIslandIndex());
			CharacterCardParamSet localUserInfo = new CharacterCardParamSet(userInfo.getSrcStudentColor(), userInfo.getDstStudentColor(), null, null, userInfo.isStudentDestinationIsSelf(), userInfo.getChosenMotherNatureAdditionalSteps(), userInfo.getSourceIslandIndex(), userInfo.getTargetIslandIndex(), userInfo.getStopCardMovementMode());
			return getCurrentPlayer().getActiveCharacterCard().useCard(managedTable, getAllPlayers(), getCurrentPlayer(), localUserInfo);
		}
		return -1;
	}
	
	/**
	 * Executes the relative effect of the card that the player played
	 */
	private int useCharacterCard(CharacterCardParamSet userInfo) throws CharacterCardIncorrectParametersException, CharacterCardNoMoreUsesAvailableException, CharacterCardNotPurchasedException {
		if (getCurrentPlayer().getActiveCharacterCard() != null) {

			return getCurrentPlayer().getActiveCharacterCard().useCard(managedTable, getAllPlayers(), getCurrentPlayer(), userInfo);
		}
		return -1;
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
	public abstract List<Player> getAllPlayers();
	
	/**
	 * @return The list of players with towers
	 */
	protected abstract List<Player> getPlayersWithTowers();
	//endregion

	//TODO: Can we remove these and create a test case where we properly simulate a Match?
	//region Methods used to simplify testing of match phases
	protected void setMatchPhase(MatchPhase matchPhase) {
		this.matchPhase = matchPhase;
	}

	protected TableManager getManagedTable() {
		return managedTable;
	}

	protected void setCurrentLeadPlayer(int currentLeadPlayer) {
		this.currentLeadPlayer = currentLeadPlayer;
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
			throw new StudentMovementInvalidException(getCurrentPlayer().getNickname() + " " + s + "cannot be moved to its table in the Dining Room since the Player does not have such Student in its Entrance space");
		} catch (TableFullException e) {
			throw new StudentMovementInvalidException("ActionPhase ERROR: the student " + s + "cannot be moved to its table in the Dining Room since the Table is already full");
		}
	}
	
	/**
	 * Picks all the students from the cloud and place them in the player's entrance
	 *
	 * @param cloudIdx clouds's id
	 */
	private void AP_CollectAllStudentsFromCloud(int cloudIdx) throws CloudPickInvalidException {
		try {
			getCurrentPlayer().addAllStudentsToEntrance(managedTable.pickStudentsFromCloud(cloudIdx));
		} catch (CollectionUnderflowError e) {
			throw new CloudPickInvalidException();
		}
	}
	
	/**
	 * Moves Mother Nature by the step and checks if there are any modifier activated by the active character cards
	 */
	private void AP_MoveMotherNatureBySteps(int steps) {
		int stepsWithCardModifier = steps;
		if (getCurrentPlayer().getActiveCharacterCard() != null && getCurrentPlayer().getActiveCharacterCard().getCharacter().getChangesMNSteps()) {
			try {
				stepsWithCardModifier += getCurrentPlayer().getActiveCharacterCard().useCard(managedTable, null, getCurrentPlayer(), null);
				System.out.println(stepsWithCardModifier - steps);
			} catch (CharacterCardIncorrectParametersException | CharacterCardNoMoreUsesAvailableException e) {
				// Do nothing, the Steps will remain the default
				System.out.println("Exception");
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
			Tower currentIslandTower = managedTable.getCurrentIsland().getActiveTowerType();
			// Find the previous owner
			Optional<Player> previousOwner = getPlayersWithTowers().stream().filter((player) -> player.getTowerType() == currentIslandTower).findFirst();
			// Find the new player that controls the island
			for (Player player: getAllPlayers()) {
				int influence = managedTable.getInfluenceOnCurrentIsland(player);
				boolean playerControlsIsland = true;
				for (Player otherPlayer: getAllPlayers()) {
					if (!otherPlayer.equals(player)) {
						if (managedTable.getInfluenceOnCurrentIsland(otherPlayer) >= influence) {
							playerControlsIsland = false;
							break;
						}
					}
				}
				if (playerControlsIsland) {
					managedTable.changeControlOfCurrentIsland(previousOwner.orElse(null), player);
					break;
				}
			}
		} catch (IslandSkippedInfluenceForStopCardException | IslandSkippedControlAssignmentForStopCardException e) {
			// If we skipped the Island due to the StopCard being there, we should not do anything
		}
	}
	
	/**
	 * Check the Match End conditions: Player has no more tower, no students left in the bag, no more assistant cards available
	 */
	private void roundCheckMatchEnd() {
		if (!managedTable.checkAndNotifyMatchEnd()) {
			for (Player p : getAllPlayers()) {
				if (p.getAvailableAssistantCards().size() == 0) {
					//Notify Match End
					List<Tower> winningTowers = managedTable.getWinningTowers();
					resolveParityAndNotifyWinnerNicknames(winningTowers);
					break;
				}
			}
		}
	}
	
	private void didReceiveMatchVictoryNotification(Notification notification) {
		if (notification.getUserInfo() != null && notification.getUserInfo().containsKey(NotificationKeys.WinnerTowerType.getRawValue())) {
			List<Tower> winningTowers = (List<Tower>) notification.getUserInfo().get(NotificationKeys.WinnerTowerType.getRawValue());
			resolveParityAndNotifyWinnerNicknames(winningTowers);
		}
	}
	
	private void resolveParityAndNotifyWinnerNicknames(List<Tower> winningTowers) {
		List<Player> activePlayers = getAllPlayers();
		int currentMax = 0;
		List<String> winnerNicknames = new ArrayList<>();
		if (winningTowers.size() == 1) {
			winnerNicknames.addAll(activePlayers.stream().filter((player) -> player.getTowerType() == winningTowers.get(0)).map(Player::getNickname).toList());
		} else {
			for (Tower winningTower: winningTowers) {
				winnerNicknames = getWinnerNicknames(activePlayers, currentMax, winnerNicknames, winningTower);
			}
		}
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.WinnerNickname.getRawValue(), winnerNicknames);
		NotificationCenter.shared().post(NotificationName.PlayerVictory, this, userInfo);
	}
	
	private List<String> getWinnerNicknames(List<Player> activePlayers, int currentMax, List<String> winnerNicknames, Tower winningTower) {
		int controlledProfCountWithTower = activePlayers.stream().filter((player) -> player.getTowerType() == winningTower).mapToInt((player) -> player.getControlledProfessors().size()).sum();
		if (controlledProfCountWithTower > currentMax) {
			winnerNicknames = activePlayers.stream().filter((player) -> player.getTowerType() == winningTower).map(Player::getNickname).toList();
		} else if (controlledProfCountWithTower == currentMax) {
			winnerNicknames.addAll(activePlayers.stream().filter((player) -> player.getTowerType() == winningTower).map(Player::getNickname).toList());
		}
		return winnerNicknames;
	}
	//endregion
	
	//region Messages
	public TableStateMessage generateTableStateMessage() {
		return managedTable.getStateMessage();
	}
	
	public PlayerStateMessage generatePlayerStateMessage(String playerNickname) {
		List<Player> players = getAllPlayers();
		for (Player player: players) {
			if (Objects.equals(player.getNickname(), playerNickname)) {
				//Generate message
				Integer activeCharacterCardIdx = null;
				for (int characterIdx = 0; characterIdx < managedTable.getPlayableCharacterCards().size(); characterIdx++) {
					CharacterCard characterCard = managedTable.getPlayableCharacterCards().get(characterIdx);
					if (player.getActiveCharacterCard() != null && characterCard.getCharacter() == player.getActiveCharacterCard().getCharacter()) {
						activeCharacterCardIdx = characterIdx;
						break;
					}
				}
				return new PlayerStateMessage(player.getNickname(), activeCharacterCardIdx, player.getAvailableAssistantCards(), player.getLastPlayedAssistantCard(), player.getBoard(), player.getAvailableCoins(), player.getWizard());
			}
		}
		return null;
	}
	//endregion
}
