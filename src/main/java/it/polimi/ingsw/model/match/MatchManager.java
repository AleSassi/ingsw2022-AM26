package it.polimi.ingsw.model.match;

import it.polimi.ingsw.exceptions.AssistantCardNotPlayableException;
import it.polimi.ingsw.exceptions.InvalidPlayerCountException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TableManager;
import it.polimi.ingsw.model.assistants.AssistantCard;
import it.polimi.ingsw.model.assistants.Wizard;
import it.polimi.ingsw.exceptions.CollectionUnderflowError;
import it.polimi.ingsw.model.student.Student;
import it.polimi.ingsw.model.student.StudentCollection;

import java.util.*;

public abstract class MatchManager {
    private List<Player> playersSortedByCurrentTurnOrder;
    private MatchVariant matchVariant;
    private int currentLeadPlayer = 0;
    private MatchPhase matchPhase;
    private TableManager managedTable;
    private int playerCount;
    private PawnCounts pawnCounts = new PawnCounts(playerCount);

    protected PawnCounts getPawnCounts() {
        return pawnCounts;
    }

    public void setUpMatch(MatchVariant variant, List<String> playersNicknames, List<Wizard> wiz) {
        playerCount = playersNicknames.size();
        matchVariant = variant;
        managedTable = new TableManager(pawnCounts.getCloudTileCount(), variant == MatchVariant.ExpertRulesSet);
        playersSortedByCurrentTurnOrder = getAllPlayers();

    }

    protected abstract void addPlayer(String nickname, Wizard wiz) throws InvalidPlayerCountException;

    protected abstract List<Player> getAllPlayers ();

    private void initEntrance(Player p) throws CollectionUnderflowError {
        StudentCollection sc = new StudentCollection();
        sc = managedTable.pickStudentsFromBag(pawnCounts.getStudentsPickedFromBag());
        p.addAllStudentsToEntrance(sc);
    }

    public List<Player> getPlayersSortedByRoundTurnOrder() {
        List<Player> tmp = getAllPlayers();
        Collections.sort(tmp,(player1, player2) -> { return player1.getLastPlayedAssistantCard().getPriorityNumber() - player2.getLastPlayedAssistantCard().getPriorityNumber(); });
        return tmp;
    }

    public int getCurrentPlayer() {
        return currentLeadPlayer;
    }

    private boolean isAssistantCardPlayable(int cardIdxForCurrentPlayer) {
        AssistantCard lastPlayedCard = playersSortedByCurrentTurnOrder.get(currentLeadPlayer).getLastPlayedAssistantCard();
        return false;
    }

    public boolean moveToNextPlayer() throws CollectionUnderflowError, InvalidPlayerCountException {
        if (currentLeadPlayer == playersSortedByCurrentTurnOrder.size()-1) {
            currentLeadPlayer = 0;
            if(matchPhase == MatchPhase.PlanPhaseStepTwo) {
                matchPhase = matchPhase.nextPhase();
                playersSortedByCurrentTurnOrder = getPlayersSortedByRoundTurnOrder();
            } else {
                PP_FirstPlayerPickFromCloudCards();
                matchPhase = matchPhase.nextPhase();
            }
            return true;
        } else {
            currentLeadPlayer++;
            if(matchPhase == MatchPhase.ActionPhaseStepThree) matchPhase = MatchPhase.ActionPhaseStepOne;
            return false;
        }
    }

    /**
     * This method fills all the cloud with the correct number of students
     * @throws InvalidPlayerCountException
     * @throws CollectionUnderflowError
     */
    public void PP_FirstPlayerPickFromCloudCards() throws InvalidPlayerCountException, CollectionUnderflowError {
        int numberOfStudent = pawnCounts.getStudentsDrawnForCloud();
        StudentCollection tmp;
        for(int cloudIdx = managedTable.getNumberOfClouds(); cloudIdx > 0; cloudIdx--){
            tmp = managedTable.pickStudentsFromBag(numberOfStudent);
            for(Student s : Student.values()) {
                managedTable.placeStudentOnCloud(s, cloudIdx, tmp.getCount(s));
            }
        }
    }

    public void PP_PlayAssistantCard(int cardIndex) throws AssistantCardNotPlayableException {
        if(isAssistantCardPlayable(cardIndex)) {
            playersSortedByCurrentTurnOrder.get(currentLeadPlayer).playAssistantCardAtIndex(cardIndex);
        } else throw new AssistantCardNotPlayableException();
    }

    public void AP_MoveStudentsToIsland(Student s, int islandIdx) {
        managedTable.placeStudentOnIsland(s, islandIdx);
    }

    public void AP_AP_MoveStudentsToDiningRoom(Student s, Player p) {
        p.placeStudentAtTableAndGetCoin(s);
    }
}
