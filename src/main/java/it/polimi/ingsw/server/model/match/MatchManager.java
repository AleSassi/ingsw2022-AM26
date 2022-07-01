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
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.characters.CharacterCard;
import it.polimi.ingsw.server.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;
import it.polimi.ingsw.server.model.student.StudentHost;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This Class represent the {@code MatchManger}
 * @author Alessandro Sassi, Federico Albertini
 */
public abstract class MatchManager {
	
	private List<Player> playersSortedByCurrentTurnOrder;
	private int currentLeadPlayer;
	private MatchPhase matchPhase;
	private TableManager managedTable;
	private PawnCounts pawnCounts;
	private int numberOfStudentsPickedByCurrentPlayer_AP1 = 0;
	private MatchVariant matchVariant;
	private List<AssistantCard> playedAssistantsInRound = new ArrayList<>();
	
	//region Public methods (used by the Controller to run actions)

	/**
	 * Constructs and sets up the match by adding all {@link it.polimi.ingsw.server.model.Player Players}, initializing the {@link it.polimi.ingsw.server.model.TableManager TableManager} and initializing the entrance for each {@code Player}
	 * @param variant (type MatchVariant) variant of the game chose by the first {@code Player}
	 * @param playersNicknames (type List of String) {@code Player's} nicknames
	 * @param wiz (type List of Wizard) chosen {@code Wizards}
	 * @throws InvalidPlayerCountException whenever the {@code Player count} isn't correct
	 * @throws IncorrectConstructorParametersException whenever the {@code Parameters} of the constructor aren't correct
	 */
	public void startMatch(MatchVariant variant, List<String> playersNicknames, List<Wizard> wiz) throws InvalidPlayerCountException, IncorrectConstructorParametersException {
		int playerCount = playersNicknames.size();
		if (playerCount > 4) throw new InvalidPlayerCountException("MatchManager ERROR: the number of Players in a Match must be < 4. Got" + playerCount);
		
		pawnCounts = new PawnCounts(playerCount);
		managedTable = new TableManager(pawnCounts.getCloudTileCount(), variant == MatchVariant.ExpertRuleSet);
		this.matchVariant = variant;
		
		for (int i = 0; i < playerCount; i++) {
			addPlayer(playersNicknames.get(i), wiz.get(i), pawnCounts.getTowersPerPlayer(), getMatchVariant() == MatchVariant.BasicRuleSet ? -1 : managedTable.getCoinFromReserve());
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
			//e.printStackTrace();
			System.out.println("MatchManager WARNING: Insufficient students in the Bag, at least a Cloud will have no students on it");
		}
		matchPhase = MatchPhase.PlanPhaseStepTwo;
		
		// Register for notification listening
		NotificationCenter.shared().addObserver(this, this::didReceiveMatchVictoryNotification, NotificationName.PlayerVictory, managedTable);
		for (Player player: getAllPlayers()) {
			NotificationCenter.shared().addObserver(this, this::didReceiveMatchVictoryNotification, NotificationName.PlayerVictory, player);
		}
	}
	
	/**
	 * Gets the match variant
	 * @return The match variant
	 */
	public MatchVariant getMatchVariant() {
		return matchVariant;
	}
	
	/**
	 * Manages the course of the game depending on the {@link  MatchPhase}, executing a player action
	 *
	 * @param AssistantCardIndex (type int) Index of the {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard} to play
	 * @param studentToMove      (type Student) {@link it.polimi.ingsw.server.model.student.Student Student} type to move
	 * @param islandDestination  (type int) {@link it.polimi.ingsw.server.model.student.Island Island's} idx to move the {@code Students} to
	 * @param moveToIsland       (type boolean) Move to {@link it.polimi.ingsw.server.model.student.Island Island} or to dining room
	 * @param motherNatureSteps  (type int) Steps to move mother nature
	 * @param cloudIdx           (type int) {@link it.polimi.ingsw.server.model.student.Cloud Cloud's} idx from which to pick the {@code Students}
	 * @throws StudentMovementInvalidException whenever the {@code Student's} movement is not aloud
	 * @throws AssistantCardNotPlayableException whenever the {@code AssistantCard} is not aloud to be played
	 * @throws CloudPickInvalidException whenever the picked {@link it.polimi.ingsw.server.model.student.Cloud Cloud} is not valid
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
		if (moveToNextPlayer() && matchPhase == MatchPhase.PlanPhaseStepTwo) { // The match phase will be set by moveToNextPlayer() to the next one, so we need to check for that
			roundCheckMatchEnd();
		}
	}
	
	/**
	 * Changes the currentPlayer to the next {@link it.polimi.ingsw.server.model.Player Player} in the playerSortedByCurrentOrder and changes the {@link it.polimi.ingsw.server.model.match.MatchPhase MatchPhase} according to the rules
	 * @return (type boolean) returns true if it needs to move the next {@code Player} in playerSortedByCurrentOrder
	 */
	protected boolean moveToNextPlayer() {
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
			case ActionPhaseStepTwo -> {
				matchPhase = matchPhase.nextPhase();
				// If we don't have enough students in the bag we need to skip the Clouds
				if (managedTable.isBagEmpty()) {
					return moveToNextPlayer();
				}
			}
			case ActionPhaseStepThree -> {
				numberOfStudentsPickedByCurrentPlayer_AP1 = 0;
				//Reset the active character card
				getCurrentPlayer().deactivateCard();
				boolean startsNewRound = false;
				if (currentLeadPlayer == playersSortedByCurrentTurnOrder.size() - 1) {
					playersSortedByCurrentTurnOrder = getPlayersSortedByClockwiseOrder();
					currentLeadPlayer = 0;
					// Perform planPhaseStepOne automatically
					try {
						PP_FillCloudCards();
					} catch (CollectionUnderflowError e) {
						System.out.println("MatchManager WARNING: Insufficient students in the Bag, the Cloud will have no students on it");
					}
					playedAssistantsInRound = new ArrayList<>();
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
	 * Sorts the {@link it.polimi.ingsw.server.model.Player Players} by the {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard} priority number (the lowest first) for the next round
	 *
	 * @return (type List of Player) the list of ordered {@code Players}
	 */
	public List<Player> getPlayersSortedByRoundTurnOrder() {
		//TODO: Need to alter the sorting lambda to account for when the Player has the same priority number
		List<Player> result = getAllPlayers();
		result.sort(Comparator.comparingInt(playerA -> (playerA.getLastPlayedAssistantCard().getPriorityNumber() + playerA.getAssistantCardOrderModifier())));
		return result;
	}
	
	/**
	 * Gets the list of players sorted by clockwise order
	 * @return The list of players sorted by clockwise order
	 */
	private List<Player> getPlayersSortedByClockwiseOrder() {
		List<Player> result = new ArrayList<>();
		//Rearrange moving to the end all players until you find the one with the lowest card
		Player playerWithLowestCard = getAllPlayers().get(0);
		for (Player player: getAllPlayers()) {
			if ((player.getLastPlayedAssistantCard().getPriorityNumber() - player.getAssistantCardOrderModifier()) < (playerWithLowestCard.getLastPlayedAssistantCard().getPriorityNumber() - playerWithLowestCard.getAssistantCardOrderModifier())) {
				playerWithLowestCard = player;
			}
		}
		//Rearrange
		boolean isPlayerAnchorReached = false;
		for (Player player: getAllPlayers()) {
			if (player.equals(playerWithLowestCard)) {
				isPlayerAnchorReached = true;
			}
			if (isPlayerAnchorReached) {
				result.add(player);
			}
		}
		for (Player player: getAllPlayers()) {
			if (player.equals(playerWithLowestCard)) {
				break;
			}
			result.add(player);
		}
		return result;
	}
	
	/**
	 * Gets the current {@link it.polimi.ingsw.server.model.Player Player}
	 * @return the current playing {@code Player}
	 */
	public Player getCurrentPlayer() {
		return playersSortedByCurrentTurnOrder.get(currentLeadPlayer);
	}

	/**
	 * Gets the current {@link  MatchPhase}
	 * @return (type MatchPhase)  returns the current {@code MatchPhase}
	 */
	public MatchPhase getMatchPhase() {
		return matchPhase;
	}
	
	/**
	 * Checks if the {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard} is playable by checking the card that has been played by the last {@link it.polimi.ingsw.server.model.Player Players} is the same as the one he wants to play
	 * @param cardIdxForCurrentPlayer (type int) {@code AssistantCard's} index to check
	 * @return (type boolean) returns true if the {@code AssistantCard} is playable
	 */
	public boolean isAssistantCardPlayable(int cardIdxForCurrentPlayer) {
		// Check for out-of-bounds
		if (cardIdxForCurrentPlayer < 0 || cardIdxForCurrentPlayer >= getCurrentPlayer().getAvailableAssistantCards().size()) {
			return false;
		}
		boolean result = true;
		for (AssistantCard assistantCard: playedAssistantsInRound) {
			if (assistantCard == getCurrentPlayer().getAvailableAssistantCards().get(cardIdxForCurrentPlayer)) {
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
	 * Purchase the {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard}
	 * @param cardIndex (type int) {@code CharacterCard's} index to purchase
	 * @return (type boolean) returns true if the {@code CharacterCard} is purchased correctly
	 * @throws CharacterCardAlreadyInUseException whenever the selected {@code CharacterCard} is already in use
	 * @throws CharacterCardIncorrectParametersException whenever the {@code Parameters} for the {@code CharacterCard} are not correct
	 */
	public boolean purchaseCharacterCards(int cardIndex) throws CharacterCardAlreadyInUseException, CharacterCardIncorrectParametersException {
		try {
			CharacterCard card = managedTable.getCardAtIndex(cardIndex);
			if (getAllPlayers().stream().filter((player) -> player.getActiveCharacterCard() != null && player.getActiveCharacterCard().getCharacter() == card.getCharacter()).toList().isEmpty()) {
				// Purchase the card
				return getCurrentPlayer().playCharacterCard(card);
			} else {
				throw new CharacterCardAlreadyInUseException();
			}
		} catch (IndexOutOfBoundsException e) {
			throw new CharacterCardIncorrectParametersException();
		}
	}
	
	/**
	 * Executes the relative effect of the {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard} that the {@link it.polimi.ingsw.server.model.Player Player} played
	 * @param userInfo (type CharacterCardNetworkParamSet) {@code CharacterCard's} parameters
	 * @return (type int) played {@code CharacterCard's} index
	 * @throws CharacterCardIncorrectParametersException whenever the {@code Parameters} for the {@code CharacterCard} are not correct
	 * @throws CharacterCardNoMoreUsesAvailableException whenever the selected {@code CharacterCard} has no more uses
	 * @throws CharacterCardNotPurchasedException whenever the selected {@code CharacterCard} has not been purchased
	 */
	public int useCharacterCard(CharacterCardNetworkParamSet userInfo) throws CharacterCardIncorrectParametersException, CharacterCardNoMoreUsesAvailableException, CharacterCardNotPurchasedException {
		if (getCurrentPlayer().getActiveCharacterCard() != null) {
			StudentHost sourceStudentHost = managedTable.getIslandAtIndex(userInfo.getSourceIslandIndex());
			StudentHost destinationStudentHost = managedTable.getIslandAtIndex(userInfo.getTargetIslandIndex());
			CharacterCardParamSet localUserInfo = new CharacterCardParamSet(userInfo.getSrcStudentColor(), userInfo.getDstStudentColor(), null, null, userInfo.isStudentDestinationIsSelf(), userInfo.getChosenMotherNatureAdditionalSteps(), userInfo.getSourceIslandIndex(), userInfo.getTargetIslandIndex(), userInfo.getStopCardMovementMode());
			return getCurrentPlayer().getActiveCharacterCard().useCard(managedTable, getAllPlayers(), getCurrentPlayer(), localUserInfo);
		} else {
			throw new CharacterCardNotPurchasedException();
		}
	}
	//endregion
	
	//region Abstract methods
	/**
	 * Initialize the {@link it.polimi.ingsw.server.model.Player Player} and adds it to the list
	 * @param nickname (type String) {@code Player's} nickname
	 * @param wiz (type Wizard) chosen {@code Wizard}
	 * @param maxTowerCount (type int) max number of {@link it.polimi.ingsw.server.model.Tower Tower}
	 * @param initialCoins (type int) the number of coins the Player owns at the beginning of the game. Set it to -1 when the Coins feature is disabled
	 * @throws IncorrectConstructorParametersException whenever the parameters aren't correct
	 * @throws InvalidPlayerCountException whenever the {@code Player} count isn't correct
	 */
	protected abstract void addPlayer(String nickname, Wizard wiz, int maxTowerCount, int initialCoins) throws InvalidPlayerCountException, IncorrectConstructorParametersException;

	/**
	 * Gets all the {@link it.polimi.ingsw.server.model.Player Players} of the {@code Match}
	 * @return (type List of Player) returns all the {@code Players}
	 */
	public abstract List<Player> getAllPlayers();

	/**
	 * Gets just the {@link it.polimi.ingsw.server.model.Player Players} with {@link it.polimi.ingsw.server.model.Tower Tower}
	 * @return (type List of Player) returns all the {@code Players}
	 */
	protected abstract List<Player> getPlayersWithTowers();
	
	/**
	 * Retrieves the team name for a Player, in case the Match is team based
	 * @param player The Player for which to find the team name
	 * @return <code>null</code> if the Match is not team-based, else the name of the team the Player belongs to
	 */
	protected abstract String getPlayerTeamName(Player player);
	//endregion

	//region Private methods & Game Phase methods
	
	/**
	 * Initialize {@link it.polimi.ingsw.server.model.Player Player's} entrance
	 * @param p (type Player) {@code Player's} entrance to initialize
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
	 * Picks the {@link it.polimi.ingsw.server.model.student.Student Students} from the bag and places them on the {@link it.polimi.ingsw.server.model.student.Cloud Clouds} depending on the number of {@link it.polimi.ingsw.server.model.Player Players}
	 * @throws CollectionUnderflowError whenever it tries to remove a {@code Student} from an empty {@code StudentCollection}
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
	 * Check if the {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard} is playable and plays it
	 * @param cardIndex (type int) {@code AssistantCard's} index
	 * @throws AssistantCardNotPlayableException whenever the {@code AssistantCard} is not allowed to be played
	 */
	private void PP_PlayAssistantCard(int cardIndex) throws AssistantCardNotPlayableException {
		if (!isAssistantCardPlayable(cardIndex)) throw new AssistantCardNotPlayableException();
		try {
			playersSortedByCurrentTurnOrder.get(currentLeadPlayer).playAssistantCardAtIndex(cardIndex);
			//If the Assistant card has already been played, the current Player must play after the other
			if (getCurrentPlayer().getLastPlayedAssistantCard() != null) {
				int numberOfSameCardsOnTable = (int) getAllPlayers().stream().filter((player) -> !player.equals(getCurrentPlayer())).map(Player::getLastPlayedAssistantCard).filter((card) -> getCurrentPlayer().getLastPlayedAssistantCard().equals(card)).count();
				boolean isAlreadyPlayed = numberOfSameCardsOnTable > 0;
				if (isAlreadyPlayed) {
					getCurrentPlayer().setAssistantCardOrderModifier(numberOfSameCardsOnTable);
				}
				playedAssistantsInRound.add(getCurrentPlayer().getLastPlayedAssistantCard());
			}
		} catch (CollectionUnderflowError | IndexOutOfBoundsException e) {
			throw new AssistantCardNotPlayableException();
		}
	}
	
	/**
	 * Moves the {@link it.polimi.ingsw.server.model.student.Student Student} from the {@link it.polimi.ingsw.server.model.Player Player's} entrance to an {@link it.polimi.ingsw.server.model.student.Island Island}
	 * @param s (type Student) {@code Student to move}
	 * @param islandIdx (type int) {@code Island's} index
	 * @throws StudentMovementInvalidException whenever the {@code Student's} movement is not aloud
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
	 * Moves the {@link it.polimi.ingsw.server.model.student.Student Student} to the dining room
	 * @param s (type Student) {@code Student} to move
	 * @throws StudentMovementInvalidException whenever the {@code Student's} movement is not aloud
	 */
	private void AP_MoveStudentToDiningRoom(Student s) throws StudentMovementInvalidException {
		try {
			getCurrentPlayer().removeStudentFromEntrance(s);
			getCurrentPlayer().placeStudentAtTableAndGetCoin(s, managedTable);
			AP_CheckAndAssignProfessorTo(s);
		} catch (CollectionUnderflowError e) {
			throw new StudentMovementInvalidException(getCurrentPlayer().getNickname() + " " + s + "cannot be moved to its table in the Dining Room since the Player does not have such Student in its Entrance space");
		} catch (TableFullException e) {
			throw new StudentMovementInvalidException("ActionPhase ERROR: the student " + s + "cannot be moved to its table in the Dining Room since the Table is already full");
		}
	}
	
	/**
	 * Picks all the {@link it.polimi.ingsw.server.model.student.Student Students} from the {@link it.polimi.ingsw.server.model.student.Cloud Cloud} and place them in the {@link it.polimi.ingsw.server.model.Player Player's} entrance
	 * @throws CloudPickInvalidException whenever the {@code Cloud} picked cannot be selected

	 * @param cloudIdx (type int) {@code Cloud's} idx
	 */
	private void AP_CollectAllStudentsFromCloud(int cloudIdx) throws CloudPickInvalidException {
		try {
			getCurrentPlayer().addAllStudentsToEntrance(managedTable.pickStudentsFromCloud(cloudIdx));
		} catch (CollectionUnderflowError e) {
			throw new CloudPickInvalidException();
		}
	}
	
	/**
	 * Moves Mother Nature by the step and checks if there are any modifier activated by the active {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard}
	 * @param steps (type int) {@code Mother Nature's} steps
	 */
	private void AP_MoveMotherNatureBySteps(int steps) {
		int stepsWithCardModifier = steps;
		if (getCurrentPlayer().getActiveCharacterCard() != null && getCurrentPlayer().getActiveCharacterCard().getCharacter().getChangesMNSteps()) {
			try {
				stepsWithCardModifier += getCurrentPlayer().getActiveCharacterCard().useCard(managedTable, getAllPlayers(), getCurrentPlayer(), new CharacterCardParamSet(null, null, null, null, false, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland));
			} catch (CharacterCardIncorrectParametersException | CharacterCardNoMoreUsesAvailableException ignored) {
				// Do nothing, the Steps will remain the default
			}
		}
		managedTable.moveMotherNature(stepsWithCardModifier);
	}
	
	/**
	 * Checks the number of {@link it.polimi.ingsw.server.model.student.Student Students} and assigns the relative {@link it.polimi.ingsw.server.model.Professor Professor} to the {@link it.polimi.ingsw.server.model.Player Player}
	 * @param checkedStudent (type Student) {@code Students} to check
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
	 * Checks the influence on the current {@link it.polimi.ingsw.server.model.student.Island Island} and in case assigns the control to a {@link it.polimi.ingsw.server.model.Player Player}
	 */
	private void AP_CheckAndChangeCurrentIslandControl() {
		try {
			Tower currentIslandTower = managedTable.getCurrentIsland().getActiveTowerType();
			// Find the previous owner
			Optional<Player> previousOwner = getPlayersWithTowers().stream().filter((player) -> player.getTowerType() == currentIslandTower).findFirst();
			Player newOwner = getPlayerControllingIsland();
			AtomicBoolean changeControl = new AtomicBoolean(true);
			previousOwner.ifPresent(previous -> {
				changeControl.set(!previous.equals(newOwner));
			});
			if (changeControl.get() && newOwner != null) {
				changeIslandControl(newOwner);
			}
		} catch (IslandSkippedInfluenceForStopCardException | IslandSkippedControlAssignmentForStopCardException e) {
			// If we skipped the Island due to the StopCard being there, we should not do anything
		}
	}
	
	/**
	 * Finds the Player that should control the current Island
	 * @return The Player that should take ownership of the Island
	 */
	protected abstract Player getPlayerControllingIsland() throws IslandSkippedInfluenceForStopCardException;
	
	/**
	 * Computes the influence of the player on the current Island
	 * @param player The Player you want to compute the influence of
	 * @return The influence number of the Player on the current island
	 * @throws IslandSkippedInfluenceForStopCardException If the Island influence computation was skipped due to a StopCard being present on the Island
	 */
	protected int getInfluenceOfPlayer(Player player) throws IslandSkippedInfluenceForStopCardException {
		return managedTable.getInfluenceOnCurrentIsland(player);
	}
	
	/**
	 * Changes control of the current island to the destination player, unifying it with adjacent ones if necessary
	 * @param to The PLayer that takes control of the Island
	 * @throws IslandSkippedControlAssignmentForStopCardException If the Island control swap was skipped due to a StopCard being present on the Island
	 */
	protected void changeIslandControl(Player to) throws IslandSkippedControlAssignmentForStopCardException {
		Tower currentIslandTower = managedTable.getCurrentIsland().getActiveTowerType();
		// Find the previous owner
		Optional<Player> previousOwner = getPlayersWithTowers().stream().filter((player) -> player.getTowerType() == currentIslandTower).findFirst();
		//Change control
		managedTable.changeControlOfCurrentIsland(previousOwner.orElse(null), to);
	}
	
	/**
	 * Check the Match End conditions: {@link it.polimi.ingsw.server.model.Player Player} has no more {@link it.polimi.ingsw.server.model.Tower Towers}, no {@link it.polimi.ingsw.server.model.student.Student Students} left in the bag, no more {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCards} available
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

	/**
	 * MatchVictoryNotification Callback
	 * @param notification (type Notification) {@code Notification's} message
	 */
	private void didReceiveMatchVictoryNotification(Notification notification) {
		if (notification.getUserInfo() != null && notification.getUserInfo().containsKey(NotificationKeys.WinnerTowerType.getRawValue())) {
			List<Tower> winningTowers = (List<Tower>) notification.getUserInfo().get(NotificationKeys.WinnerTowerType.getRawValue());
			resolveParityAndNotifyWinnerNicknames(winningTowers);
		}
	}

	/**
	 * In case of victory resolves the parity in case of Teams Match and notifies the {@link it.polimi.ingsw.server.model.Player Players}
	 * @param winningTowers (type List Tower) list of winning {@code Towers}
	 */
	private void resolveParityAndNotifyWinnerNicknames(List<Tower> winningTowers) {
		List<Player> activePlayers = getAllPlayers();
		List<String> winnerNicknames = new ArrayList<>();
		if (winningTowers.size() == 1) {
			winnerNicknames.addAll(activePlayers.stream().filter((player) -> player.getTowerType() == winningTowers.get(0)).map(Player::getNickname).toList());
		} else {
			//Need to resolve parity by checking the number of professors
			int[] professorCounts = new int[Tower.values().length];
			for (Player player: getAllPlayers()) {
				professorCounts[player.getTowerType().index()] += player.getControlledProfessors().size();
			}
			//Find the max indices - if more than 1 then we have a parity that we need to keep
			List<Integer> maxIndices = new ArrayList<>();
			int max = 0;
			for (int professorCount : professorCounts) {
				if (professorCount > max) {
					max = professorCount;
				}
			}
			for (int i = 0; i < professorCounts.length; i++) {
				if (professorCounts[i] == max) {
					maxIndices.add(i);
				}
			}
			List<Tower> trueWinningTowers = maxIndices.stream().map((index) -> Tower.values()[index]).toList();
			winnerNicknames.addAll(activePlayers.stream().filter((player) -> trueWinningTowers.contains(player.getTowerType())).map(Player::getNickname).toList());
		}
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.WinnerNickname.getRawValue(), winnerNicknames);
		NotificationCenter.shared().post(NotificationName.PlayerVictory, this, userInfo);
	}
	//endregion
	
	//region Messages

	/**
	 * Generates the {@link TableStateMessage}
	 * @return (type TableStateMessage) {@code TableStateMessage}
	 */
	public TableStateMessage generateTableStateMessage() {
		return managedTable.getStateMessage();
	}

	/**
	 * Generates the {@link PlayerStateMessage}
	 * @param playerNickname (type String) {@code Player's} state to generate message
	 * @return (type PlayerStateMessage) {@code PlayerStateMessage}
	 */
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
				return new PlayerStateMessage(player, activeCharacterCardIdx, getPlayerTeamName(player));
			}
		}
		return null;
	}
	//endregion
}
