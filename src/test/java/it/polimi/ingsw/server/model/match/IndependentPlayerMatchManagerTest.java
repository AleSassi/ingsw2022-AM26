package it.polimi.ingsw.server.model.match;

import it.polimi.ingsw.server.exceptions.model.*;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.TableManager;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.characters.CharacterCard;
import it.polimi.ingsw.server.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.server.model.match.IndependentPlayerMatchManager;
import it.polimi.ingsw.server.model.match.MatchPhase;
import it.polimi.ingsw.server.model.match.MatchVariant;
import it.polimi.ingsw.server.model.student.Island;
import it.polimi.ingsw.server.model.student.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class IndependentPlayerMatchManagerTest {

    private IndependentPlayerMatchManager matchManager;

    @BeforeEach
    void initIndependentPlayerMatchManager() {
        matchManager = new IndependentPlayerMatchManager();
        MatchVariant matchVariant = MatchVariant.ExpertRuleSet;
        List<String> playerNicknames = new ArrayList<>();
        List<Wizard> wiz = new ArrayList<>();
        playerNicknames.add("Fede");
        playerNicknames.add("Ale");
        wiz.add(Wizard.Wizard1);
        wiz.add(Wizard.Wizard2);
        assertDoesNotThrow(() -> matchManager.startMatch(matchVariant, playerNicknames, wiz));
        //Check the Entrance space & tower count
        for (Player player: matchManager.getAllPlayers()) {
            assertEquals(7, player.getStudentsInEntrance());
            assertEquals(8, player.getAvailableTowerCount());
        }
        //Check the nicknames of the players
        assertEquals("Fede", matchManager.getAllPlayers().get(0).getNickname());
        assertEquals("Ale", matchManager.getAllPlayers().get(1).getNickname());
    }

    /**
     * This method test that the card that the player wants to be played will be the last played card
     */
    @Test
    void planPhaseTwoTest() {
        Player player = matchManager.getCurrentPlayer();
        AssistantCard card = player.getAvailableAssistantCards().get(0);
        assertDoesNotThrow(() -> runPhase(MatchPhase.PlanPhaseStepTwo, 0, 0, false, 0, 0));
        assertEquals(card, player.getLastPlayedAssistantCard());
        assertEquals(MatchPhase.PlanPhaseStepTwo, matchManager.getMatchPhase());
        assertEquals("Ale", matchManager.getCurrentPlayer().getNickname());
    }
    
    @Test
    void testUnplayableAssistant() {
        planPhaseTwoTest();
        assertThrows(AssistantCardNotPlayableException.class, () -> {
            runPhase(MatchPhase.PlanPhaseStepTwo, 0, 0, false, 0, 0);
        });
    }
    
    @Test
    void testForcedUnplayableAssistant() {
        planPhaseTwoTest();
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 9; i++) {
                matchManager.getCurrentPlayer().playAssistantCardAtIndex(1);
            }
        });
        //Only one card playable
        Player player = matchManager.getCurrentPlayer();
        AssistantCard card = player.getAvailableAssistantCards().get(0);
        assertDoesNotThrow(() -> runPhase(MatchPhase.PlanPhaseStepTwo, 0, 0, false, 0, 0));
        assertEquals(card, player.getLastPlayedAssistantCard());
        assertEquals(1, player.getAssistantCardOrderModifier());
        assertEquals(MatchPhase.ActionPhaseStepOne, matchManager.getMatchPhase());
        assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
    }
    
    @Test
    void testExceptionWithEmptyAssistants() {
        planPhaseTwoTest();
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                matchManager.getCurrentPlayer().playAssistantCardAtIndex(0);
            }
        });
        assertThrows(AssistantCardNotPlayableException.class, () -> {
            runPhase(MatchPhase.PlanPhaseStepTwo, 0, 0, false, 0, 0);
        });
    }

    /**
     * This method test that
     */
    @RepeatedTest(10)
    void actionPhaseOneRoomTest() {
        assertDoesNotThrow(() -> {
            runPhase(MatchPhase.PlanPhaseStepTwo, 0, 0, false, 0, 0);
            runPhase(MatchPhase.PlanPhaseStepTwo, 1, 0, false, 0, 0);
        });
        Student removedStudent = Student.RedDragon;
        for (Student s: Student.values()) {
            try {
                matchManager.getCurrentPlayer().removeStudentFromEntrance(s);
                removedStudent = s;
                matchManager.getCurrentPlayer().addStudentToEntrance(s);
                break;
            } catch (CollectionUnderflowError ignored) {}
        }
        Student finalRemovedStudent = removedStudent;
        assertDoesNotThrow(() -> matchManager.runAction(0, finalRemovedStudent, 0, false, 0 , 0 ));
        assertEquals(6, matchManager.getCurrentPlayer().getStudentsInEntrance());
        assertEquals(1, matchManager.getCurrentPlayer().getCountAtTable(finalRemovedStudent));
        assertEquals(MatchPhase.ActionPhaseStepOne, matchManager.getMatchPhase());
        assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
    }
    
    @RepeatedTest(10)
    void actionPhaseOneFillRoomTest() {
        assertDoesNotThrow(() -> {
            runPhase(MatchPhase.PlanPhaseStepTwo, 0, 0, false, 0, 0);
            runPhase(MatchPhase.PlanPhaseStepTwo, 1, 0, false, 0, 0);
            runPhase(MatchPhase.ActionPhaseStepOne, 0, 0, false, 0, 0);
        });
    }

    /**
     * This method tests that ActionPhaseOne with movement to island works correctly
     */
    @RepeatedTest(10)
    void actionPhaseOneIslandTest() {
        assertDoesNotThrow(() -> {
            runPhase(MatchPhase.PlanPhaseStepTwo, 0, 0, false, 0, 0);
            runPhase(MatchPhase.PlanPhaseStepTwo, 1, 0, false, 0, 0);
        });
        Student removedStudent = Student.RedDragon;
        for (Student s: Student.values()) {
            try {
                matchManager.getCurrentPlayer().removeStudentFromEntrance(s);
                removedStudent = s;
                matchManager.getCurrentPlayer().addStudentToEntrance(s);
                break;
            } catch (CollectionUnderflowError ignored) {}
        }
        Student finalRemovedStudent = removedStudent;
        int prevNumberOfStudents = matchManager.generateTableStateMessage().getIslands().get(1).getNumberOfSameStudents(removedStudent);
        assertDoesNotThrow(() -> matchManager.runAction(0, finalRemovedStudent, 1, true, 0, 0));
        assertEquals(6, matchManager.getCurrentPlayer().getStudentsInEntrance());
        assertEquals(prevNumberOfStudents + 1, matchManager.generateTableStateMessage().getIslands().get(1).getNumberOfSameStudents(removedStudent));
    }

    /**
     * This method test that the check and change of the island's control works correctly
     */
    @RepeatedTest(10)
    void actionPhaseTwoControlTest() {
        //Move to Action Phase
        assertDoesNotThrow(() -> {
            runPhase(MatchPhase.PlanPhaseStepTwo, 0, 0, false, 0, 0);
            runPhase(MatchPhase.PlanPhaseStepTwo, 1, 0, false, 0, 0);
        });
        //Move a Student (of which the Player has 2 in the Entrance) to the Table - the Player will control this student
        Student studentToMove = null;
        for (Student student: Student.values()) {
            if (matchManager.getCurrentPlayer().getBoard().getEntrance().getCount(student) >= 2) {
                studentToMove = student;
                break;
            }
        }
        if (studentToMove != null) {
            Student finalStudentToMove = studentToMove;
            assertDoesNotThrow(() -> {
                matchManager.runAction(0, finalStudentToMove, 0, false, 0, 0);
                matchManager.runAction(0, finalStudentToMove, 0, true, 0, 0);
                //Move one last student to finish the phase
                Student lastStudentToMove = Student.BlueUnicorn;
                for (Student student: Student.values()) {
                    if (matchManager.getCurrentPlayer().getBoard().getEntrance().getCount(student) >= 1) {
                        lastStudentToMove = student;
                        break;
                    }
                }
                matchManager.runAction(0, lastStudentToMove, 0, false, 0, 0);
            });
            //Now we are in Phase 2 - Move MN to Island 0
            assertDoesNotThrow(() -> {
                int currentIslandIndex = 0;
                for (Island island: matchManager.generateTableStateMessage().getIslands()) {
                    if (island.isMotherNaturePresent()) {
                        break;
                    }
                    currentIslandIndex += 1;
                }
                runPhase(MatchPhase.ActionPhaseStepTwo, 0, 0, false, 12 - currentIslandIndex, 0);
            });
            //Check that the current Island has a Tower
            assertEquals(1, matchManager.generateTableStateMessage().getIslands().get(0).getTowerCount());
            assertEquals(matchManager.getCurrentPlayer().getTowerType(), matchManager.generateTableStateMessage().getIslands().get(0).getActiveTowerType());
            for (int i = 1; i < 12; i++) {
                assertEquals(0, matchManager.generateTableStateMessage().getIslands().get(i).getTowerCount());
            }
        }
    }

    /**
     * This method test that the actionPhaseThree works correctly
     */
    @Test
    void actionPhaseThreeTest() {
        assertDoesNotThrow(() -> {
            runPhase(MatchPhase.PlanPhaseStepTwo, 0, 0, false, 0, 0);
            runPhase(MatchPhase.PlanPhaseStepTwo, 1, 0, false, 0, 0);
            runPhase(MatchPhase.ActionPhaseStepOne, 0, 0, false, 0, 0);
            runPhase(MatchPhase.ActionPhaseStepTwo, 0, 0, false, 1, 0);
        });
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 0, 0));
        assertEquals(7, matchManager.getCurrentPlayer().getStudentsInEntrance());
        int totCount = 0;
        for (Student s: Student.values()) {
            totCount += matchManager.generateTableStateMessage().getManagedClouds().get(0).getCount(s);
        }
        assertEquals(0, totCount);
    }

    @Test
    void getPlayersSortedByRoundTurnOrderTest() {
        List<Player> players = new ArrayList<>();

        assertDoesNotThrow(() -> {
            players.add(matchManager.getCurrentPlayer());
            runPhase(MatchPhase.PlanPhaseStepTwo, 0, 0, false, 0, 0);
            players.add(matchManager.getCurrentPlayer());
            runPhase(MatchPhase.PlanPhaseStepTwo, 1, 0, false, 0, 0);
        });
        assertEquals("Ale", matchManager.getPlayersSortedByRoundTurnOrder().get(1).getNickname());
        assertEquals("Fede", matchManager.getPlayersSortedByRoundTurnOrder().get(0).getNickname());
    }

    @Test
    void testMoveToNextPlayer() {
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
    }

    /**
     * This method test that the list of players having towers is returned correctly
     */
    @Test
    void getPlayersWithTowersTest() {
        assertEquals("Fede", matchManager.getPlayersWithTowers().get(0).getNickname());
        assertEquals("Ale", matchManager.getPlayersWithTowers().get(1).getNickname());
    }
    
    private void runPhase(MatchPhase phase, int AssistantCardIndex, int islandDestination, boolean moveToIsland, int motherNatureSteps, int cloudIdx) throws StudentMovementInvalidException, AssistantCardNotPlayableException, CloudPickInvalidException {
        switch (phase) {
            case PlanPhaseStepTwo -> {
                boolean isLastPlan = matchManager.getCurrentPlayer().getNickname().equals("Ale");
                matchManager.runAction(AssistantCardIndex, null, 0, false, 0, 0);
                assertEquals(isLastPlan ? MatchPhase.ActionPhaseStepOne : MatchPhase.PlanPhaseStepTwo, matchManager.getMatchPhase());
            }
            case ActionPhaseStepOne -> {
                //Move 3 Students to the Table
                int[] removedStudents = new int[Student.values().length];
                String nickname = matchManager.getCurrentPlayer().getNickname();
                for (int i = 0; i < 3; i++) {
                    Student removedStudent = Student.RedDragon;
                    for (Student s: Student.values()) {
                        try {
                            matchManager.getCurrentPlayer().removeStudentFromEntrance(s);
                            removedStudent = s;
                            matchManager.getCurrentPlayer().addStudentToEntrance(s);
                            break;
                        } catch (CollectionUnderflowError ignored) {}
                    }
                    removedStudents[Student.getRawValueOf(removedStudent)] += 1;
                    Student finalRemovedStudent = removedStudent;
                    matchManager.runAction(0, finalRemovedStudent, islandDestination, moveToIsland, 0, 0);
                    assertEquals(6 - i, matchManager.getCurrentPlayer().getStudentsInEntrance());
                    assertEquals(removedStudents[Student.getRawValueOf(removedStudent)], matchManager.getCurrentPlayer().getCountAtTable(finalRemovedStudent));
                    assertEquals(i == 2 ? MatchPhase.ActionPhaseStepTwo : MatchPhase.ActionPhaseStepOne, matchManager.getMatchPhase());
                    assertEquals(nickname, matchManager.getCurrentPlayer().getNickname());
                }
            }
            case ActionPhaseStepTwo -> {
                //Move MN
                String nickname = matchManager.getCurrentPlayer().getNickname();
                matchManager.runAction(0, null, 0, false, motherNatureSteps, 0);
                assertEquals(MatchPhase.ActionPhaseStepThree, matchManager.getMatchPhase());
                assertEquals(nickname, matchManager.getCurrentPlayer().getNickname());
            }
            case ActionPhaseStepThree -> {
                //Pick the Cloud
                String nickname = matchManager.getCurrentPlayer().getNickname();
                matchManager.runAction(0, null, 0, false, 1, cloudIdx);
                assertEquals(MatchPhase.ActionPhaseStepOne, matchManager.getMatchPhase());
                assertEquals(nickname.equals("Ale") ? "Fede" : "Ale", matchManager.getCurrentPlayer().getNickname());
            }
        }
    }
    
    @Test
    void testSimpleTurn() {
        planPhaseTwoTest();
        assertDoesNotThrow(() -> matchManager.runAction(1, null, 0, false, 0, 0));
        assertEquals(MatchPhase.ActionPhaseStepOne, matchManager.getMatchPhase());
        assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
        //Move 3 Students to the Table
        int[] removedStudents = new int[Student.values().length];
        for (int i = 0; i < 3; i++) {
            Student removedStudent = Student.RedDragon;
            for (Student s: Student.values()) {
                try {
                    matchManager.getCurrentPlayer().removeStudentFromEntrance(s);
                    removedStudent = s;
                    matchManager.getCurrentPlayer().addStudentToEntrance(s);
                    break;
                } catch (CollectionUnderflowError ignored) {}
            }
            removedStudents[Student.getRawValueOf(removedStudent)] += 1;
            Student finalRemovedStudent = removedStudent;
            assertDoesNotThrow(() -> matchManager.runAction(0, finalRemovedStudent, 0, false, 0 , 0 ));
            assertEquals(6 - i, matchManager.getCurrentPlayer().getStudentsInEntrance());
            assertEquals(removedStudents[Student.getRawValueOf(removedStudent)], matchManager.getCurrentPlayer().getCountAtTable(finalRemovedStudent));
            assertEquals(i == 2 ? MatchPhase.ActionPhaseStepTwo : MatchPhase.ActionPhaseStepOne, matchManager.getMatchPhase());
            assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
        }
        //Move MN
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 1, 0));
        assertEquals(MatchPhase.ActionPhaseStepThree, matchManager.getMatchPhase());
        assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
        //Pick the Cloud
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 0, 0));
        assertEquals(MatchPhase.ActionPhaseStepOne, matchManager.getMatchPhase());
        assertEquals("Ale", matchManager.getCurrentPlayer().getNickname());
    }
    
    @Test
    void testFullRound() {
        testSimpleTurn();
        //Move 3 Students to the Table
        int[] removedStudents = new int[Student.values().length];
        for (int i = 0; i < 3; i++) {
            Student removedStudent = Student.RedDragon;
            for (Student s: Student.values()) {
                try {
                    matchManager.getCurrentPlayer().removeStudentFromEntrance(s);
                    removedStudent = s;
                    matchManager.getCurrentPlayer().addStudentToEntrance(s);
                    break;
                } catch (CollectionUnderflowError ignored) {}
            }
            removedStudents[Student.getRawValueOf(removedStudent)] += 1;
            Student finalRemovedStudent = removedStudent;
            assertDoesNotThrow(() -> matchManager.runAction(0, finalRemovedStudent, 0, false, 0 , 0 ));
            assertEquals(6 - i, matchManager.getCurrentPlayer().getStudentsInEntrance());
            assertEquals(removedStudents[Student.getRawValueOf(removedStudent)], matchManager.getCurrentPlayer().getCountAtTable(finalRemovedStudent));
            assertEquals(i == 2 ? MatchPhase.ActionPhaseStepTwo : MatchPhase.ActionPhaseStepOne, matchManager.getMatchPhase());
            assertEquals("Ale", matchManager.getCurrentPlayer().getNickname());
        }
        //Move MN
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 1, 0));
        assertEquals(MatchPhase.ActionPhaseStepThree, matchManager.getMatchPhase());
        assertEquals("Ale", matchManager.getCurrentPlayer().getNickname());
        //Pick the Cloud
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 0, 1));
        assertEquals(MatchPhase.PlanPhaseStepTwo, matchManager.getMatchPhase());
        assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
    }
}