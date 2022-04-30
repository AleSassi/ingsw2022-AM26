package it.polimi.ingsw.server.model.match;

import it.polimi.ingsw.server.exceptions.model.CharacterCardIncorrectParametersException;
import it.polimi.ingsw.server.exceptions.model.CharacterCardNoMoreUsesAvailableException;
import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.server.model.match.IndependentPlayerMatchManager;
import it.polimi.ingsw.server.model.match.MatchPhase;
import it.polimi.ingsw.server.model.match.MatchVariant;
import it.polimi.ingsw.server.model.student.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IndependentPlayerMatchManagerTest {

    IndependentPlayerMatchManager matchManager;

    @BeforeEach
    void initIndependentPlayerMatchManager() {
        matchManager = new IndependentPlayerMatchManager();
        MatchVariant matchVariant = MatchVariant.ExpertRuleSet;
        List<String> playerNicknames = new ArrayList<>();
        List<Wizard> wiz = new ArrayList<>();
        playerNicknames.add("Fede");
        playerNicknames.add("Ale");
        playerNicknames.add("Leo");
        wiz.add(Wizard.Wizard1);
        wiz.add(Wizard.Wizard2);
        wiz.add(Wizard.Wizard3);
        assertDoesNotThrow(() -> matchManager.startMatch(matchVariant, playerNicknames, wiz));

    }

    @Test
    void initEntranceTest() {
        assertEquals(9, matchManager.getAllPlayers().get(0).getStudentsInEntrance());
    }

    @Test
    void addPlayerTest() {
        assertEquals("Fede", matchManager.getAllPlayers().get(0).getNickname());
        assertEquals(6, matchManager.getAllPlayers().get(0).getAvailableTowerCount());
    }

    /**
     * This method test that the card that the player wants to be played will be the last played card
     */
    @Test
    void planPhaseTwoTest() {
        matchManager.setMatchPhase(MatchPhase.PlanPhaseStepTwo);
        AssistantCard card = matchManager.getCurrentPlayer().getAvailableAssistantCards().get(0);
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 1 , false, 0, 0));
        assertEquals(card, matchManager.getCurrentPlayer().getLastPlayedAssistantCard());
    }

    /**
     * This method test that
     */
    @RepeatedTest(100)
    void actionPhaseOneRoomTest() {
        matchManager.setMatchPhase(MatchPhase.ActionPhaseStepOne);
        Student removedStudent = Student.RedDragon;
        for(Student s: Student.values()) {
            try {
                matchManager.getCurrentPlayer().removeStudentFromEntrance(s);
                removedStudent = s;
                matchManager.getCurrentPlayer().addStudentToEntrance(s);
                break;
            } catch (CollectionUnderflowError ignored) {

            }
        }
        Student finalRemovedStudent = removedStudent;
        assertDoesNotThrow(() -> {
            matchManager.runAction(0, finalRemovedStudent, 0, false, 0 , 0 );
        });
        assertEquals(8, matchManager.getCurrentPlayer().getStudentsInEntrance());
        assertEquals(1, matchManager.getCurrentPlayer().getCountAtTable(finalRemovedStudent));
        matchManager.getCurrentPlayer().addStudentToEntrance(finalRemovedStudent);
    }

    /**
     * This method tests that ActionPhaseOne with movement to island works correctly
     */
    @RepeatedTest(100)
    void actionPhaseOneIslandTest() {
        matchManager.setMatchPhase(MatchPhase.ActionPhaseStepOne);
        Student removedStudent = Student.RedDragon;
        for(Student s: Student.values()) {
            try {
                matchManager.getCurrentPlayer().removeStudentFromEntrance(s);
                removedStudent = s;
                matchManager.getCurrentPlayer().addStudentToEntrance(s);
                break;
            } catch (CollectionUnderflowError ignored) {

            }
        }
        Student finalRemovedStudent = removedStudent;
        assertDoesNotThrow(() -> matchManager.runAction(0, finalRemovedStudent, 1, true, 0, 0));
        assertEquals(8, matchManager.getCurrentPlayer().getStudentsInEntrance());
        int numberOfStudent = 0;
        for(Student s: Student.values()){
            numberOfStudent += matchManager.getManagedTable().getIslandAtIndex(1).getCount(s);
        }
        if(matchManager.getManagedTable().getIslandAtIndex(1).isMotherNaturePresent() || matchManager.getManagedTable().circularWrap(matchManager.getManagedTable().getCurrentIslandIndex(), 11) -6 == 1) {
            assertEquals(1, numberOfStudent);
        }
        else {
            assertEquals(2, numberOfStudent);
        }
        matchManager.getCurrentPlayer().addStudentToEntrance(removedStudent);
    }

    /**
     * This method test that the movement of MN in ActionPhaseTwo works correctly
     */
    @RepeatedTest(100)
    void actionPhaseTwoMotherNatureTest() {
        matchManager.setMatchPhase(MatchPhase.ActionPhaseStepTwo);

        int additionalSteps = 0;
        CharacterCardParamSet userInfo = new CharacterCardParamSet(null, null, null, null, false, 1, 0, 0, null);

        try {
            if(matchManager.getManagedTable().getPlayableCharacterCards().contains(Character.Magician)) {
                matchManager.purchaseCharacterCards(matchManager.getManagedTable().getPlayableCharacterCards().indexOf(Character.Magician));
                matchManager.useCharacterCard(userInfo);
                additionalSteps++;
            }
        } catch (CharacterCardIncorrectParametersException | CharacterCardNoMoreUsesAvailableException ignored) {

        }
        int motherNaturePos = matchManager.getManagedTable().circularWrap(matchManager.getManagedTable().getCurrentIslandIndex() + 3 +additionalSteps, 12);
        assertDoesNotThrow(() -> {
            matchManager.runAction(0, null, 0, false, 3, 0);
        });
        assertEquals(motherNaturePos, matchManager.getManagedTable().getCurrentIslandIndex());
    }

    /**
     * This method test that the check and change of the island's control works correctly
     */
    @RepeatedTest(100)
    void actionPhaseTwoControlTest() {
        matchManager.setMatchPhase(MatchPhase.ActionPhaseStepOne);
        matchManager.getManagedTable().getCurrentIsland().placeStudents(Student.YellowElf, 1);
        matchManager.getManagedTable().getCurrentIsland().placeStudents(Student.RedDragon, 2);

        matchManager.setCurrentLeadPlayer(0);
        matchManager.getAllPlayers().get(0).addStudentToEntrance(Student.YellowElf);
        matchManager.getAllPlayers().get(0).addStudentToEntrance(Student.YellowElf);
        matchManager.getAllPlayers().get(1).addStudentToEntrance(Student.YellowElf);

        assertDoesNotThrow(() -> {
            matchManager.runAction(0, Student.YellowElf, 0, false, 0 , 0 );
            matchManager.runAction(0, Student.YellowElf, 0, false, 0 , 0 );
            matchManager.setCurrentLeadPlayer(1);
            matchManager.runAction(0, Student.YellowElf, 0, false, 0 , 0 );
            matchManager.setCurrentLeadPlayer(0);
            assertEquals(Professor.YellowElf, matchManager.getAllPlayers().get(0).getControlledProfessors().get(0));
        });
        assertNull(matchManager.getManagedTable().getCurrentIsland().getActiveTowerType());
        matchManager.setMatchPhase(MatchPhase.ActionPhaseStepTwo);
        assertDoesNotThrow(() -> {
            matchManager.runAction(0, null, 0, false, 0, 0);
        });
        assertNotEquals(null, matchManager.getManagedTable().getCurrentIsland().getActiveTowerType());
        assertEquals(Tower.Black, matchManager.getManagedTable().getCurrentIsland().getActiveTowerType());

        matchManager.getAllPlayers().get(2).addStudentToEntrance(Student.YellowElf);
        matchManager.getAllPlayers().get(2).addStudentToEntrance(Student.YellowElf);
        matchManager.getAllPlayers().get(2).addStudentToEntrance(Student.YellowElf);
        matchManager.getAllPlayers().get(2).addStudentToEntrance(Student.RedDragon);
        matchManager.setMatchPhase(MatchPhase.ActionPhaseStepOne);
        assertDoesNotThrow(() -> {
            matchManager.setCurrentLeadPlayer(2);
            matchManager.runAction(0, Student.YellowElf, 0, false, 0 , 0 );
            matchManager.runAction(0, Student.YellowElf, 0, false, 0 , 0 );
            matchManager.runAction(0, Student.YellowElf, 0, false, 0 , 0 );
            matchManager.runAction(0, Student.RedDragon, 0, false, 0 , 0 );
            matchManager.setCurrentLeadPlayer(0);
            assertEquals(Professor.YellowElf, matchManager.getAllPlayers().get(2).getControlledProfessors().get(0));
        });
        matchManager.setMatchPhase(MatchPhase.ActionPhaseStepTwo);
        assertDoesNotThrow(() -> {
            matchManager.runAction(0, null, 0, false, 0, 0);
        });
        assertEquals(Tower.Gray, matchManager.getManagedTable().getCurrentIsland().getActiveTowerType());



    }

    /**
     * This method test that the actionPhaseThree works correctly
     */
    @Test
    void actionPhaseThreeTest() {
        matchManager.setMatchPhase(MatchPhase.ActionPhaseStepThree);
        assertDoesNotThrow(() -> {
            matchManager.runAction(0, null, 0, false, 0, 0);
        });
        assertEquals(9, matchManager.getCurrentPlayer().getStudentsInEntrance());
        int totCount = 0;
        for(Student s: Student.values()) {
            totCount += matchManager.getManagedTable().getCloud(0).getCount(s);
        }
        assertEquals(0, totCount);
    }


    /**
     * This method tests that moveToNextPlayer works correctly
     */
    @Test
    void moveToNextPlayerTest() {
        matchManager.setMatchPhase(MatchPhase.PlanPhaseStepTwo);
    }

    @Test
    void getPlayersSortedByRoundTurnOrderTest() {
        matchManager.setMatchPhase(MatchPhase.PlanPhaseStepTwo);
        List<Player> players = new ArrayList<>();

        assertDoesNotThrow(() -> {
            matchManager.getCurrentPlayer().playAssistantCardAtIndex(2);
            players.add(matchManager.getCurrentPlayer());
            matchManager.moveToNextPlayer();
            matchManager.getCurrentPlayer().playAssistantCardAtIndex(1);
            players.add(matchManager.getCurrentPlayer());
            matchManager.moveToNextPlayer();
            matchManager.getCurrentPlayer().playAssistantCardAtIndex(0);
            players.add(matchManager.getCurrentPlayer());
            matchManager.moveToNextPlayer();
        });
        assertEquals(players.get(0).getNickname(), matchManager.getPlayersSortedByRoundTurnOrder().get(2).getNickname());
        assertEquals(players.get(1).getNickname(), matchManager.getPlayersSortedByRoundTurnOrder().get(1).getNickname());
        assertEquals(players.get(2).getNickname(), matchManager.getPlayersSortedByRoundTurnOrder().get(0).getNickname());
    }

    @Test
    void getCurrentPlayerTest() {
        assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
        matchManager.moveToNextPlayer();
        assertEquals("Ale", matchManager.getCurrentPlayer().getNickname());
    }

    /**
     * This method test that the list of all players is returned correctly
     *
     */
    @Test
    void getAllPlayersTest() {
        assertEquals("Fede", matchManager.getAllPlayers().get(0).getNickname());
        assertEquals("Ale", matchManager.getAllPlayers().get(1).getNickname());
        assertEquals("Leo", matchManager.getAllPlayers().get(2).getNickname());
    }

    /**
     * This method test that the list of players having towers is returned correctly
     */
    @Test
    void getPlayersWithTowersTest() {
        assertEquals("Fede", matchManager.getPlayersWithTowers().get(0).getNickname());
        assertEquals("Leo", matchManager.getPlayersWithTowers().get(2).getNickname());
    }
}