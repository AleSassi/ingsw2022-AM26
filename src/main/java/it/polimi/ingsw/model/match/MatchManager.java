package it.polimi.ingsw.model.match;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Professor;
import it.polimi.ingsw.model.TableManager;
import it.polimi.ingsw.model.assistants.Wizard;
import it.polimi.ingsw.model.student.EmptyCollectionException;
import it.polimi.ingsw.model.student.Student;
import it.polimi.ingsw.model.student.StudentCollection;
import org.w3c.dom.ls.LSInput;

import java.security.PublicKey;
import java.util.*;

public abstract class MatchManager {
    private List<Player> playersSortedByCurrentTurnOrder = new ArrayList<>();
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
        playersSortedByCurrentTurnOrder = getPlayersSortedByRoundTurnOrder();

    }

    protected abstract void addPlayer(String nickname, Wizard wiz) throws InvalidPlayerCountException;
    protected abstract List<Player> getAllPlayers ();

    private void initEntrance(Player p) throws EmptyCollectionException {
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

    public boolean moveToNextPlayer() {
        if (currentLeadPlayer == playersSortedByCurrentTurnOrder.size()-1) {
            currentLeadPlayer = 0;
            if(matchPhase == MatchPhase.PlanPhaseStepTwo) {
                matchPhase = matchPhase.nextPhase();
                playersSortedByCurrentTurnOrder = getPlayersSortedByRoundTurnOrder();
            }
            else {
                matchPhase = matchPhase.nextPhase();
            }
            return true;
        } else {
            currentLeadPlayer++;
            if(matchPhase == MatchPhase.ActionPhaseStepThree) matchPhase = MatchPhase.ActionPhaseStepOne;
            else if(matchPhase == MatchPhase.PlanPhaseStepTwo) matchPhase = MatchPhase.PlanPhaseStepOne;
            return false;
        }
    }


}
