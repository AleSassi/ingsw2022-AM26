package it.polimi.ingsw.model.match;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Professor;
import it.polimi.ingsw.model.TableManager;
import it.polimi.ingsw.model.assistants.Wizard;
import it.polimi.ingsw.model.characters.CharacterCard;
import it.polimi.ingsw.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.model.student.Student;
import it.polimi.ingsw.model.student.StudentCollection;

import java.util.*;

public abstract class MatchManager {
    private List<Player> playersSortedByCurrentTurnOrder = getAllPlayers();
    private int currentLeadPlayer = 0;
    private MatchPhase matchPhase = MatchPhase.PlanPhaseStepOne;
    private TableManager managedTable;
    private int playerCount;
    private final PawnCounts pawnCounts = new PawnCounts(playerCount);

    protected PawnCounts getPawnCounts() {
        return pawnCounts;
    }

    /**
     * Sets up the match by adding all to players, initializing the TableManager and initializing the entrance for each player
     * @param variant Choose by the first player, Basic or Experts
     * @param playersNicknames
     * @param wiz
     *
     */
    public void setUpMatch(MatchVariant variant, List<String> playersNicknames, List<Wizard> wiz) throws InvalidPlayerCountException, IncorrectConstructorParametersException, CollectionUnderflowError {
        playerCount = playersNicknames.size();
        managedTable = new TableManager(pawnCounts.getCloudTileCount(), variant == MatchVariant.ExpertRulesSet);

        for (int i = 0; i < playerCount; i++) {
            addPlayer(playersNicknames.get(i), wiz.get(i));
        }
        for (Player p : getAllPlayers()) {
            initEntrance(p);
        }

    }

    /**
     * Create and adds the player with the nickname and wizard
     */
    protected abstract void addPlayer(String nickname, Wizard wiz) throws InvalidPlayerCountException, IncorrectConstructorParametersException;

    /**
     * return the list of all player in order of addition
     */
    protected abstract List<Player> getAllPlayers();

    /**
     * Initialize Player's p entrance
     */
    private void initEntrance(Player p) throws CollectionUnderflowError, InvalidPlayerCountException {
        StudentCollection sc;
        sc = managedTable.pickStudentsFromBag(pawnCounts.getStudentsPickedFromBag());
        p.addAllStudentsToEntrance(sc);
    }

    /**
     * Sort the players by the assistant card priority number (the lowest first) for the next round
     * @return the list of ordered player
     */
    public List<Player> getPlayersSortedByRoundTurnOrder() {
        List<Player> tmp = getAllPlayers();
        Collections.sort(tmp, (player1, player2) -> player1.getLastPlayedAssistantCard().getPriorityNumber() - player2.getLastPlayedAssistantCard().getPriorityNumber());
        return tmp;
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
        boolean isOnlyOnePlayable = getCurrentPlayer().getAvailableAssistantCards().stream().noneMatch((card) -> !(getAllPlayers().stream().filter((p) -> !p.equals(getCurrentPlayer())).map(Player::getLastPlayedAssistantCard).toList().contains(card)));
        return result || isOnlyOnePlayable;
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
            }
            matchPhase.nextPhase();
            getCurrentPlayer().deactivateCard();
            return true;
        } else {
            if (matchPhase == MatchPhase.ActionPhaseStepThree) {
                matchPhase = MatchPhase.ActionPhaseStepOne;
                currentLeadPlayer++;
            } else if (matchPhase == MatchPhase.PlanPhaseStepTwo) {
                currentLeadPlayer++;
            } else {
                matchPhase.nextPhase();
            }
            getCurrentPlayer().deactivateCard();
            return false;
        }
    }

    /**
     * Picks the students from the bag and places them on the clouds depending on the number of players
     */
    private void PP_FirstPlayerPickFromCloudCards() throws InvalidPlayerCountException, CollectionUnderflowError {
        int numberOfStudent = pawnCounts.getStudentsDrawnForCloud();
        StudentCollection tmp;
        for (int cloudIdx = managedTable.getNumberOfClouds(); cloudIdx > 0; cloudIdx--) {
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
        if (isAssistantCardPlayable(cardIndex)) {
            try {
                playersSortedByCurrentTurnOrder.get(currentLeadPlayer).playAssistantCardAtIndex(cardIndex);
            } catch (CollectionUnderflowError | IndexOutOfBoundsException e) {
                throw new AssistantCardNotPlayableException();
            }
        } else {
            throw new AssistantCardNotPlayableException();
        }
    }

    /**
     * Moves the student s to the islandIdx island
     */
    private void AP_MoveStudentsToIsland(Student s, int islandIdx) {
        managedTable.placeStudentOnIsland(s, islandIdx);
    }

    /**
     * Moves the student s to the dining room
     */
    private void AP_MoveStudentsToDiningRoom(Student s) throws TableFullException {
        getCurrentPlayer().placeStudentAtTableAndGetCoin(s);
        AP_CheckAndAssignProfessorTo(s);
    }

    /**
     * Picks all the students from the cloud and place them in the player's entrance
     * @param cloudIdx  clouds's id
     */
    private void AP_CollectAllStudentsFromCloud(int cloudIdx) {
        StudentCollection sc;
        sc = managedTable.pickStudentsFromCloud(cloudIdx);
        getCurrentPlayer().addAllStudentsToEntrance(sc);
    }

    /**
     * Moves Mother Nature by the step and checks if there are any modifier activated by the active character cards
     */
    private void AP_MoveMotherNatureBySteps(int steps) {
        int newSteps = steps;
        if (getCurrentPlayer().getActiveCharacterCard() != null) {
            if (getCurrentPlayer().getActiveCharacterCard().getCharacter().getChangesMNSteps()) {
                try {
                    steps += getCurrentPlayer().getActiveCharacterCard().useCard(managedTable, null, getCurrentPlayer(), null);
                } catch (CharacterCardIncorrectParametersException | CharacterCardNoMoreUsesAvailableException e) {
                    e.printStackTrace();
                }
            }
        }
        managedTable.moveMotherNature(steps);

    }

    /**
     * Checks the number of students checkedStudent and assign the relative professor to the player
     */
    private void AP_CheckAndAssignProfessorTo(Student checkedStudent) {
        Professor associatedProf = checkedStudent.getAssociatedProfessor();
        int controlledStudent = getCurrentPlayer().getCountAtTable(checkedStudent);
        if (getCurrentPlayer().getActiveCharacterCard() != null && getCurrentPlayer().getActiveCharacterCard().getCharacter().getChangesProfControl()) {
            try {
                controlledStudent += getCurrentPlayer().getActiveCharacterCard().useCard(managedTable, getAllPlayers(), getCurrentPlayer(), null);
            } catch (CharacterCardIncorrectParametersException | CharacterCardNoMoreUsesAvailableException e) {
                e.printStackTrace();
            }
        }
        for (Player otherPlayer : getAllPlayers()) {
            if (!otherPlayer.getNickname().equals(getCurrentPlayer().getNickname())) {
                if (otherPlayer.getCountAtTable(checkedStudent) < controlledStudent) {
                    otherPlayer.removeProfessor(associatedProf);
                    getCurrentPlayer().addProfessor(associatedProf);

                }
            }
        }

    }

    /**
     * Purchase the character card
     */
    public boolean purchaseCharacterCards(int cardIndex) {
        CharacterCard card = managedTable.getCardAtIndex(cardIndex);
        return getCurrentPlayer().playCharacterCard(card);
    }

    /**
     * Does the relative effect of the card that the player played
     */
    private void useCharacterCard(CharacterCardParamSet userInfo) {
        try {
            getCurrentPlayer().getActiveCharacterCard().useCard(managedTable, getAllPlayers(), getCurrentPlayer(), userInfo);
        } catch (CharacterCardIncorrectParametersException | CharacterCardNoMoreUsesAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check the Match End conditions: Player has no more tower, no students left in the bag, no more assistant cards available
     */
    private void roundCheckMatchEnd() {
        for(Player p : getAllPlayers()){
            if(p.getAvailableTowerCount() == 0 || p.getAvailableAssistantCards().size() == 0 || managedTable.getBagStudentCount() == 0) {
                //Notify Match End
                break;
            }
        }

    }

    /**
     * Checks the influence on the current Island and in case assigns the control to a player
     */
    private void AP_CheckAndChangeCurrentIslandControl() {
        int currentPlayerInfluence = 0;
        try {
            currentPlayerInfluence = managedTable.getInfluenceOnCurrentIsland(getCurrentPlayer());
        } catch (IslandSkippedInfluenceForStopCardException e) {
            e.printStackTrace();
        }
        Player currentLeader = getCurrentPlayer();
        for (Player p : getAllPlayers()) {
            if (p != getCurrentPlayer()) {
                try {
                    if (currentPlayerInfluence < managedTable.getInfluenceOnCurrentIsland(p)) {
                        currentLeader = p;
                    }
                } catch (IslandSkippedInfluenceForStopCardException e) {
                    e.printStackTrace();
                }
            }
        }
        if (managedTable.getCurrentIsland().getActiveTowerType() == null) {
            try {
                managedTable.changeControlOfCurrentIsland(null, currentLeader);
            } catch (IslandSkippedControlAssignmentForStopCardException e) {
                e.printStackTrace();
            }
        } else {
            if (managedTable.getCurrentIsland().getActiveTowerType() != currentLeader.getTowerType()) {
                for (Player p : getPlayersWithTowers()) {
                    if (managedTable.getCurrentIsland().getActiveTowerType() == p.getTowerType() && p != currentLeader) {
                        try {
                            managedTable.changeControlOfCurrentIsland(p, currentLeader);
                        } catch (IslandSkippedControlAssignmentForStopCardException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

    /**
     * Manage the course of the game depending on the match phase
     * @param AssistantCardIndex Index of the Assistant card to play
     * @param studentToMove Student type to move
     * @param islandDestination Island idx to move to the students
     * @param moveToIsland  Move to Island or To dining room
     * @param motherNatureSteps Steps to move mother nature
     * @param cloudIdx Cloud's idx from which to pick the students
     * @param characterCardIdx  Character card idx to play or buy
     * @param userInfo  Set of parameters needed to the character card
     */
    public void matchController(int AssistantCardIndex, Student studentToMove, int islandDestination, boolean moveToIsland, int motherNatureSteps, int cloudIdx, int characterCardIdx, CharacterCardParamSet userInfo) throws CollectionUnderflowError, InvalidPlayerCountException, AssistantCardNotPlayableException, TableFullException {
        switch (matchPhase) {
            case PlanPhaseStepOne -> {
                PP_FirstPlayerPickFromCloudCards();
                matchPhase.nextPhase();
            }
            case PlanPhaseStepTwo -> {
                if (purchaseCharacterCards(characterCardIdx)) {
                    useCharacterCard(userInfo);
                }
                PP_PlayAssistantCard(AssistantCardIndex);
                moveToNextPlayer();
            }
            case ActionPhaseStepOne -> {
                if (purchaseCharacterCards(characterCardIdx)) {
                    useCharacterCard(userInfo);
                }
                if (moveToIsland) {
                    AP_MoveStudentsToIsland(studentToMove, islandDestination);
                } else {
                    AP_MoveStudentsToDiningRoom(studentToMove);
                }
                moveToNextPlayer();
            }
            case ActionPhaseStepTwo -> {
                if (purchaseCharacterCards(characterCardIdx)) {
                    useCharacterCard(userInfo);
                }
                AP_MoveMotherNatureBySteps(motherNatureSteps);
                AP_CheckAndChangeCurrentIslandControl();
                moveToNextPlayer();
            }
            case ActionPhaseStepThree -> {
                if (purchaseCharacterCards(characterCardIdx)) {
                    useCharacterCard(userInfo);
                }
                AP_CollectAllStudentsFromCloud(cloudIdx);
                moveToNextPlayer();
            }
        }
    }

    /**
     *
     * @return  The list of players with towers
     */
    protected abstract List<Player> getPlayersWithTowers();
}
