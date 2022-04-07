package it.polimi.ingsw.model.match;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TableManager;
import it.polimi.ingsw.model.assistants.AssistantCard;
import it.polimi.ingsw.model.assistants.Wizard;
import it.polimi.ingsw.model.characters.CharacterCard;
import it.polimi.ingsw.model.student.Student;
import it.polimi.ingsw.model.student.StudentCollection;
import org.w3c.dom.ls.LSInput;

import java.security.PublicKey;
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

    protected abstract List<Player> getAllPlayers();

    private void initEntrance(Player p) throws CollectionUnderflowError {
        StudentCollection sc;
        sc = managedTable.pickStudentsFromBag(pawnCounts.getStudentsPickedFromBag());
        p.addAllStudentsToEntrance(sc);
    }

    public List<Player> getPlayersSortedByRoundTurnOrder() {
        List<Player> tmp = getAllPlayers();
        Collections.sort(tmp, (player1, player2) -> player1.getLastPlayedAssistantCard().getPriorityNumber() - player2.getLastPlayedAssistantCard().getPriorityNumber());
        return tmp;
    }

    public Player getCurrentPlayer() {
        return playersSortedByCurrentTurnOrder.get(currentLeadPlayer);
    }

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

    public boolean moveToNextPlayer() throws CollectionUnderflowError, InvalidPlayerCountException {
        if (currentLeadPlayer == playersSortedByCurrentTurnOrder.size() - 1) {
            currentLeadPlayer = 0;
            if (matchPhase == MatchPhase.PlanPhaseStepTwo) {
                matchPhase = matchPhase.nextPhase();
                playersSortedByCurrentTurnOrder = getPlayersSortedByRoundTurnOrder();
            } else {
                PP_FirstPlayerPickFromCloudCards();
                matchPhase = matchPhase.nextPhase();
            }
            return true;
        } else {
            currentLeadPlayer++;
            if (matchPhase == MatchPhase.ActionPhaseStepThree) matchPhase = MatchPhase.ActionPhaseStepOne;
            return false;
        }
    }


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

    private void PP_PlayAssistantCard(int cardIndex) throws AssistantCardNotPlayableException {
        if (isAssistantCardPlayable(cardIndex)) {
            playersSortedByCurrentTurnOrder.get(currentLeadPlayer).playAssistantCardAtIndex(cardIndex);
        } else throw new AssistantCardNotPlayableException();
    }

    private void AP_MoveStudentsToIsland(Student s, int islandIdx) {
        managedTable.placeStudentOnIsland(s, islandIdx);
    }

    private void AP_MoveStudentsToDiningRoom(Student s) {
        getCurrentPlayer().placeStudentAtTableAndGetCoin(s);
    }

    private void AP_CollectAllStudentsFromCloud(int cloudIdx) {
        StudentCollection sc;
        sc = managedTable.pickStudentsFromCloud(cloudIdx);
        getCurrentPlayer().addAllStudentsToEntrance(sc);
    }

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

    public boolean purchaseCharacterCards(int cardIndex) {
        CharacterCard card = managedTable.getCardAtIndex(cardIndex);
        return getCurrentPlayer().playCharacterCard(card);
    }


}
