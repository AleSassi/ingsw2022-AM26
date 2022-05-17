package it.polimi.ingsw.server.model.match;

import it.polimi.ingsw.server.exceptions.model.AssistantCardNotPlayableException;
import it.polimi.ingsw.server.exceptions.model.CharacterCardIncorrectParametersException;
import it.polimi.ingsw.server.exceptions.model.CharacterCardNoMoreUsesAvailableException;
import it.polimi.ingsw.server.exceptions.model.CollectionUnderflowError;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.characters.CharacterCard;
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

    private IndependentPlayerMatchManager matchManager;

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
        //Check the Entrance space & tower count
        for (Player player: matchManager.getAllPlayers()) {
            assertEquals(9, player.getStudentsInEntrance());
            assertEquals(6, player.getAvailableTowerCount());
        }
        //Check the nicknames of the players
        assertEquals("Fede", matchManager.getAllPlayers().get(0).getNickname());
        assertEquals("Ale", matchManager.getAllPlayers().get(1).getNickname());
        assertEquals("Leo", matchManager.getAllPlayers().get(2).getNickname());
    }

    /**
     * This method test that the card that the player wants to be played will be the last played card
     */
    @Test
    void planPhaseTwoTest() {
        matchManager.setMatchPhase(MatchPhase.PlanPhaseStepTwo);
        Player player = matchManager.getCurrentPlayer();
        AssistantCard card = player.getAvailableAssistantCards().get(0);
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 0, 0));
        assertEquals(card, player.getLastPlayedAssistantCard());
        assertEquals(MatchPhase.PlanPhaseStepTwo, matchManager.getMatchPhase());
        assertEquals("Ale", matchManager.getCurrentPlayer().getNickname());
    }
    
    @Test
    void testUnplayableAssistant() {
        planPhaseTwoTest();
        assertThrows(AssistantCardNotPlayableException.class, () -> {
            matchManager.runAction(0, null, 0, false, 0, 0);
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
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 0, 0));
        assertEquals(card, player.getLastPlayedAssistantCard());
        assertEquals(1, player.getAssistantCardOrderModifier());
        assertEquals(MatchPhase.PlanPhaseStepTwo, matchManager.getMatchPhase());
        assertEquals("Leo", matchManager.getCurrentPlayer().getNickname());
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
            matchManager.runAction(0, null, 0, false, 0, 0);
        });
    }

    /**
     * This method test that
     */
    @RepeatedTest(10)
    void actionPhaseOneRoomTest() {
        matchManager.setMatchPhase(MatchPhase.ActionPhaseStepOne);
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
        assertEquals(8, matchManager.getCurrentPlayer().getStudentsInEntrance());
        assertEquals(1, matchManager.getCurrentPlayer().getCountAtTable(finalRemovedStudent));
        assertEquals(MatchPhase.ActionPhaseStepOne, matchManager.getMatchPhase());
        assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
    }
    
    @RepeatedTest(10)
    void actionPhaseOneFillRoomTest() {
        matchManager.setMatchPhase(MatchPhase.ActionPhaseStepOne);
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
            assertEquals(8 - i, matchManager.getCurrentPlayer().getStudentsInEntrance());
            assertEquals(removedStudents[Student.getRawValueOf(removedStudent)], matchManager.getCurrentPlayer().getCountAtTable(finalRemovedStudent));
            assertEquals(i == 2 ? MatchPhase.ActionPhaseStepTwo : MatchPhase.ActionPhaseStepOne, matchManager.getMatchPhase());
            assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
        }
    }

    /**
     * This method tests that ActionPhaseOne with movement to island works correctly
     */
    @RepeatedTest(10)
    void actionPhaseOneIslandTest() {
        matchManager.setMatchPhase(MatchPhase.ActionPhaseStepOne);
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
        assertDoesNotThrow(() -> matchManager.runAction(0, finalRemovedStudent, 1, true, 0, 0));
        assertEquals(8, matchManager.getCurrentPlayer().getStudentsInEntrance());
        int numberOfStudent = 0;
        for (Student s: Student.values()) {
            numberOfStudent += matchManager.getManagedTable().getIslandAtIndex(1).getCount(s);
        }
        if (matchManager.getManagedTable().getIslandAtIndex(1).isMotherNaturePresent() || matchManager.getManagedTable().circularWrap(matchManager.getManagedTable().getCurrentIslandIndex(), 11) -6 == 1) {
            assertEquals(1, numberOfStudent);
        } else {
            assertEquals(2, numberOfStudent);
        }
        matchManager.getCurrentPlayer().addStudentToEntrance(removedStudent);
    }

    /**
     * This method test that the check and change of the island's control works correctly
     */
    @RepeatedTest(10)
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
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 0, 0));
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
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 0, 0));
        assertEquals(Tower.Gray, matchManager.getManagedTable().getCurrentIsland().getActiveTowerType());
    }

    /**
     * This method test that the actionPhaseThree works correctly
     */
    @Test
    void actionPhaseThreeTest() {
        matchManager.setMatchPhase(MatchPhase.ActionPhaseStepThree);
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 0, 0));
        assertEquals(9, matchManager.getCurrentPlayer().getStudentsInEntrance());
        int totCount = 0;
        for(Student s: Student.values()) {
            totCount += matchManager.getManagedTable().getCloud(0).getCount(s);
        }
        assertEquals(0, totCount);
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
        assertEquals(players.get(2).getNickname(), matchManager.getPlayersSortedByRoundTurnOrder().get(0).getNickname());
        assertEquals(players.get(1).getNickname(), matchManager.getPlayersSortedByRoundTurnOrder().get(1).getNickname());
        assertEquals(players.get(0).getNickname(), matchManager.getPlayersSortedByRoundTurnOrder().get(2).getNickname());
    }

    @Test
    void testMoveToNextPlayer() {
        assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
        matchManager.moveToNextPlayer();
        assertEquals("Ale", matchManager.getCurrentPlayer().getNickname());
        matchManager.moveToNextPlayer();
        assertEquals("Leo", matchManager.getCurrentPlayer().getNickname());
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
    
    @Test
    void testSimpleTurn() {
        planPhaseTwoTest();
        assertDoesNotThrow(() -> matchManager.runAction(1, null, 0, false, 0, 0));
        assertEquals(MatchPhase.PlanPhaseStepTwo, matchManager.getMatchPhase());
        assertEquals("Leo", matchManager.getCurrentPlayer().getNickname());
        assertDoesNotThrow(() -> matchManager.runAction(2, null, 0, false, 0, 0));
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
            assertEquals(8 - i, matchManager.getCurrentPlayer().getStudentsInEntrance());
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
            assertEquals(8 - i, matchManager.getCurrentPlayer().getStudentsInEntrance());
            assertEquals(removedStudents[Student.getRawValueOf(removedStudent)], matchManager.getCurrentPlayer().getCountAtTable(finalRemovedStudent));
            assertEquals(i == 2 ? MatchPhase.ActionPhaseStepTwo : MatchPhase.ActionPhaseStepOne, matchManager.getMatchPhase());
            assertEquals("Ale", matchManager.getCurrentPlayer().getNickname());
        }
        //Move MN
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 1, 0));
        assertEquals(MatchPhase.ActionPhaseStepThree, matchManager.getMatchPhase());
        assertEquals("Ale", matchManager.getCurrentPlayer().getNickname());
        //Pick the Cloud
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 0, 2));
        assertEquals(MatchPhase.ActionPhaseStepOne, matchManager.getMatchPhase());
        assertEquals("Leo", matchManager.getCurrentPlayer().getNickname());
        
        //Move 3 Students to the Table
        removedStudents = new int[Student.values().length];
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
            assertEquals(8 - i, matchManager.getCurrentPlayer().getStudentsInEntrance());
            assertEquals(removedStudents[Student.getRawValueOf(removedStudent)], matchManager.getCurrentPlayer().getCountAtTable(finalRemovedStudent));
            assertEquals(i == 2 ? MatchPhase.ActionPhaseStepTwo : MatchPhase.ActionPhaseStepOne, matchManager.getMatchPhase());
            assertEquals("Leo", matchManager.getCurrentPlayer().getNickname());
        }
        //Move MN
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 1, 0));
        assertEquals(MatchPhase.ActionPhaseStepThree, matchManager.getMatchPhase());
        assertEquals("Leo", matchManager.getCurrentPlayer().getNickname());
        //Pick the Cloud
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 0, 1));
        assertEquals(MatchPhase.PlanPhaseStepTwo, matchManager.getMatchPhase());
        assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
    }
}