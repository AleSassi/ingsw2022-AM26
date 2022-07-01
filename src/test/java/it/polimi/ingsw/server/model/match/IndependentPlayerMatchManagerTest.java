package it.polimi.ingsw.server.model.match;

import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.CharacterCardNetworkParamSet;
import it.polimi.ingsw.server.exceptions.model.*;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.TableManager;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.characters.CharacterCardBean;
import it.polimi.ingsw.server.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.server.model.student.Island;
import it.polimi.ingsw.server.model.student.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@code IndependentPlayerMatchManagerTest} class tests {@link it.polimi.ingsw.server.model.match.IndependentPlayerMatchManager IndependentPlayerMatchManager}
 * @see IndependentPlayerMatchManager
 */
class IndependentPlayerMatchManagerTest {

    private IndependentPlayerMatchManager matchManager;
    
    /**
     * Common test initialization
     */
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
     * Test {@code addPLayer} with 3 new {@link it.polimi.ingsw.server.model.Player Players}
     */
    @Test
    void testAddThreePlayers() {
        MatchManager matchManager = new IndependentPlayerMatchManager();
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
     * Tests the {@code planPhaseTwo}
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

    /**
     * Tests that whenever the {@link it.polimi.ingsw.server.model.assistants.AssistantCard AssistantCard's} index is out of bounds {@code runPhase} works correctly
     */
    @Test
    void testUnplayableAssistant() {
        planPhaseTwoTest();
        assertThrows(AssistantCardNotPlayableException.class, () -> {
            runPhase(MatchPhase.PlanPhaseStepTwo, 0, 0, false, 0, 0);
        });
        assertThrows(AssistantCardNotPlayableException.class, () -> {
            runPhase(MatchPhase.PlanPhaseStepTwo, 999, 0, false, 0, 0);
        });
    }
    
    /**
     * Tests the case with an unplayable assistant card
     */
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
    
    /**
     * Tests that if we don't have available assistants an exception is raised
     */
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
     * Tests {@code actionPhaseOneRoom} manages the ActionPhaseOne with {@link it.polimi.ingsw.server.model.student.Student Student} movement to diningRoom
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
    
    /**
     * Tests an invalid student movement
     */
    @RepeatedTest(20)
    void testUnmovableStudent() {
        assertDoesNotThrow(() -> {
            runPhase(MatchPhase.PlanPhaseStepTwo, 0, 0, false, 0, 0);
            runPhase(MatchPhase.PlanPhaseStepTwo, 1, 0, false, 0, 0);
        });
        Student removedStudent = null;
        for (Student s: Student.values()) {
            if (matchManager.getCurrentPlayer().getBoard().getEntrance().getCount(s) == 0) {
                removedStudent = s;
                break;
            }
        }
        if (removedStudent != null) {
            Student finalRemovedStudent = removedStudent;
            assertThrows(StudentMovementInvalidException.class, () -> matchManager.runAction(0, finalRemovedStudent, 0, false, 0 , 0 ));
            assertThrows(StudentMovementInvalidException.class, () -> matchManager.runAction(0, finalRemovedStudent, 0, true, 0 , 0 ));
            assertEquals(7, matchManager.getCurrentPlayer().getStudentsInEntrance());
            assertEquals(0, matchManager.getCurrentPlayer().getCountAtTable(finalRemovedStudent));
            assertEquals(MatchPhase.ActionPhaseStepOne, matchManager.getMatchPhase());
            assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
        }
    }
    
    /**
     * Tests that the planning phase and the first action phase step work
     */
    @RepeatedTest(10)
    void actionPhaseOneFillRoomTest() {
        assertDoesNotThrow(() -> {
            runPhase(MatchPhase.PlanPhaseStepTwo, 0, 0, false, 0, 0);
            runPhase(MatchPhase.PlanPhaseStepTwo, 1, 0, false, 0, 0);
            runPhase(MatchPhase.ActionPhaseStepOne, 0, 0, false, 0, 0);
        });
    }

    /**
     * Tests {@code actionPhaseOneIsland} manages the ActionPhaseOne with {@link it.polimi.ingsw.server.model.student.Student Student} movement to {@link it.polimi.ingsw.server.model.student.Island Island}
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
     * This method test that the {@code actionPhaseThree} works correctly
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

    /**
     * Test that {@code getPlayersSortedByRoundTurnOrder} sorts the {@link it.polimi.ingsw.server.model.Player Players} correctly
     */
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

    /**
     * Test that {@code testMoveToNextPlayer} moves to next {@link it.polimi.ingsw.server.model.Player Player} correctly
     */
    @Test
    void testMoveToNextPlayer() {
        assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
        matchManager.moveToNextPlayer();
        assertEquals("Ale", matchManager.getCurrentPlayer().getNickname());
    }

    /**
     * This method test that the list of all {@link it.polimi.ingsw.server.model.Player Players} is returned correctly
     *
     */
    @Test
    void getAllPlayersTest() {
        assertEquals("Fede", matchManager.getAllPlayers().get(0).getNickname());
        assertEquals("Ale", matchManager.getAllPlayers().get(1).getNickname());
    }

    /**
     * This method test that the list of {@link it.polimi.ingsw.server.model.Player Players} having {@link it.polimi.ingsw.server.model.Tower Towers} is returned correctly
     */
    @Test
    void getPlayersWithTowersTest() {
        assertEquals("Fede", matchManager.getPlayersWithTowers().get(0).getNickname());
        assertEquals("Ale", matchManager.getPlayersWithTowers().get(1).getNickname());
    }
    
    /**
     * Method that executes a match phase and performs the related checks
     * @param phase The match phase to run
     * @param AssistantCardIndex The assistant card index to choose
     * @param islandDestination The destination island for students
     * @param moveToIsland Whether it moves to an island or to the dining room
     * @param motherNatureSteps The number of mother Nature steps
     * @param cloudIdx The picked cloud index
     * @throws StudentMovementInvalidException If the student movement is invalid
     * @throws AssistantCardNotPlayableException If the assistant card cannot be played
     * @throws CloudPickInvalidException If the cloud cannot be picked
     */
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

    /**
     * Tests the flow of a simple turn
     */
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

    /**
     * Tests a full round
     */
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

    /**
     * Tests CollectionUnderflowError when picking from an empty cloud
     */
    @Test
    void testPickFromEmptyCloudTest() {
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
        assertThrows(CloudPickInvalidException.class, () -> matchManager.runAction(0, null, 0, false, 0, 0));
        assertEquals(MatchPhase.ActionPhaseStepThree, matchManager.getMatchPhase());
        assertEquals("Ale", matchManager.getCurrentPlayer().getNickname());
    }

    /**
     * Tests the CharacterCardPurchase
     */
    @RepeatedTest(10)
    void testCharacterCardPurchase() {
        planPhaseTwoTest();
        assertDoesNotThrow(() -> matchManager.runAction(1, null, 0, false, 0, 0));
        
        int cardIndex = -1;
        int index = 0;
        for (CharacterCardBean card: matchManager.generateTableStateMessage().getPlayableCharacterCards()) {
            if (card.getTotalPrice() == 1) {
                cardIndex = index;
                break;
            }
            index += 1;
        }
        if (cardIndex >= 0) {
            int finalCardIndex = cardIndex;
            assertDoesNotThrow(() -> matchManager.purchaseCharacterCards(finalCardIndex));
        }
    }

    /**
     * Tests {@code CharacterCardUse} works correctly
     */
    @RepeatedTest(20)
    void testCharacterCardUse() {
        testCharacterCardPurchase();
        
        Student removedStudent0 = Student.RedDragon;
        for (Student s: Student.values()) {
            try {
                matchManager.getCurrentPlayer().removeStudentFromEntrance(s);
                removedStudent0 = s;
                matchManager.getCurrentPlayer().addStudentToEntrance(s);
                break;
            } catch (CollectionUnderflowError ignored) {}
        }
        Student finalRemovedStudent = removedStudent0;
        assertDoesNotThrow(() -> matchManager.runAction(0, finalRemovedStudent, 0, false, 0 , 0 ));
        
        if (matchManager.getCurrentPlayer().getActiveCharacterCard() != null) {
            if (matchManager.getCurrentPlayer().getActiveCharacterCard().getCharacter().getMaxNumberOfUsesInTurn() > 0) {
                Student removedStudent = null;
                Student targetStudent = null;
                for (Student s: Student.values()) {
                    if (matchManager.getCurrentPlayer().getActiveCharacterCard().getCharacter().getHostedStudentsCount() > 0) {
                        if (matchManager.getCurrentPlayer().getActiveCharacterCard().getHostedStudents().getCount(s) > 0) {
                            removedStudent = s;
                            break;
                        }
                    } else if (matchManager.getCurrentPlayer().getBoard().getEntrance().getCount(s) > 0) {
                        removedStudent = s;
                        break;
                    }
                }
                for (Student s: Student.values()) {
                    if (matchManager.getCurrentPlayer().getBoard().getEntrance().getCount(s) > 0) {
                        targetStudent = s;
                        break;
                    }
                }
                if (matchManager.getCurrentPlayer().getActiveCharacterCard().getCharacter() == Character.Musician) {
                    for (Student s: Student.values()) {
                        if (matchManager.getCurrentPlayer().getBoard().getDiningRoom().getCount(s) > 0) {
                            targetStudent = s;
                            break;
                        }
                    }
                    for (Student s: Student.values()) {
                        if (matchManager.getCurrentPlayer().getBoard().getEntrance().getCount(s) > 0) {
                            removedStudent = s;
                            break;
                        }
                    }
                }
                if (removedStudent != null && targetStudent != null) {
                    CharacterCardNetworkParamSet paramSet = new CharacterCardNetworkParamSet(removedStudent, targetStudent, true, 2, 0, 1, CharacterCardParamSet.StopCardMovementMode.ToIsland);
                    assertDoesNotThrow(() -> matchManager.useCharacterCard(paramSet));
                }
            }
        }
    }
    
    /**
     * Tests the purchase of a card with wrong parameters
     */
    @Test
    void testWrongCardPurchase() {
        planPhaseTwoTest();
        assertDoesNotThrow(() -> matchManager.runAction(1, null, 0, false, 0, 0));
        
        assertThrows(CharacterCardIncorrectParametersException.class, () -> matchManager.purchaseCharacterCards(33));
    }
    
    /**
     * tests the character card use without purchase
     */
    @RepeatedTest(10)
    void testWithoutCardPurchase() {
        planPhaseTwoTest();
        assertDoesNotThrow(() -> matchManager.runAction(1, null, 0, false, 0, 0));
    
        assertThrows(CharacterCardNotPurchasedException.class, () -> matchManager.useCharacterCard(new CharacterCardNetworkParamSet(Student.BlueUnicorn, Student.RedDragon, true, 0, 0, 0, CharacterCardParamSet.StopCardMovementMode.ToIsland)));
    }
    
    /**
     * Tests a roud with a character effect
     */
    @RepeatedTest(20)
    void testRoundWithCharacter() {
        testCharacterCardUse();
        //Finish the round
        //Move 3 Students to the Table
        int[] removedStudents = new int[Student.values().length];
        for (int i = 0; i < 2; i++) {
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
            assertEquals(i == 1 ? MatchPhase.ActionPhaseStepTwo : MatchPhase.ActionPhaseStepOne, matchManager.getMatchPhase());
            assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
        }
        int islandIndex = 0;
        for (Island island: matchManager.generateTableStateMessage().getIslands()) {
            if (island.isMotherNaturePresent()) {
                break;
            }
            islandIndex += 1;
        }
        //Move MN
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 1, 0));
        int newIslandIndex = 0;
        for (Island island: matchManager.generateTableStateMessage().getIslands()) {
            if (island.isMotherNaturePresent()) {
                break;
            }
            newIslandIndex += 1;
        }
        assertEquals(TableManager.circularWrap(islandIndex + 1 + (matchManager.getCurrentPlayer().getActiveCharacterCard() != null && matchManager.getCurrentPlayer().getActiveCharacterCard().getCharacter().getChangesMNSteps() ? 2 : 0), 12), newIslandIndex);
        assertEquals(MatchPhase.ActionPhaseStepThree, matchManager.getMatchPhase());
        assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
        //Pick the Cloud
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 0, 0));
        assertEquals(MatchPhase.ActionPhaseStepOne, matchManager.getMatchPhase());
        assertEquals("Ale", matchManager.getCurrentPlayer().getNickname());
    }
    
    /**
     * Checks that the match reacts to the victory notification
     */
    @Test
    void testReactVictoryNotification() {
        testCharacterCardUse();
        //Finish the round
        //Move 3 Students to the Table
        int[] removedStudents = new int[Student.values().length];
        for (int i = 0; i < 2; i++) {
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
            assertEquals(i == 1 ? MatchPhase.ActionPhaseStepTwo : MatchPhase.ActionPhaseStepOne, matchManager.getMatchPhase());
            assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
        }
        int islandIndex = 0;
        for (Island island: matchManager.generateTableStateMessage().getIslands()) {
            if (island.isMotherNaturePresent()) {
                break;
            }
            islandIndex += 1;
        }
        //Move MN
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 1, 0));
        int newIslandIndex = 0;
        for (Island island: matchManager.generateTableStateMessage().getIslands()) {
            if (island.isMotherNaturePresent()) {
                break;
            }
            newIslandIndex += 1;
        }
        assertEquals(TableManager.circularWrap(islandIndex + 1 + (matchManager.getCurrentPlayer().getActiveCharacterCard() != null && matchManager.getCurrentPlayer().getActiveCharacterCard().getCharacter().getChangesMNSteps() ? 2 : 0), 12), newIslandIndex);
        assertEquals(MatchPhase.ActionPhaseStepThree, matchManager.getMatchPhase());
        assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
        //Send a victory notification
        NotificationCenter.shared().addObserver(this, (notification) -> {
            List<String> winners = ((List<String>) notification.getUserInfo().get(NotificationKeys.WinnerNickname.getRawValue()));
            assertEquals(1, winners.size());
            assertEquals("Fede", winners.get(0));
        }, NotificationName.PlayerVictory, matchManager);
        matchManager.getCurrentPlayer().notifyVictory();
    }
    
    /**
     * Tests the victory notification when no assistants are playable
     */
    @RepeatedTest(20)
    void testVictoryNotificationNoMoreAssistants() {
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
        //Remove all assistants to force Ale to win the game
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 9; i++) {
                matchManager.getCurrentPlayer().playAssistantCardAtIndex(0);
            }
        });
        NotificationCenter.shared().addObserver(this, (notification) -> {
            List<String> winners = ((List<String>) notification.getUserInfo().get(NotificationKeys.WinnerNickname.getRawValue()));
            List<Player> players = matchManager.getAllPlayers();
            if (players.get(0).getAvailableTowerCount() < players.get(1).getAvailableTowerCount()) {
                // 0 wins
                assertEquals(1, winners.size());
                assertEquals(players.get(0).getNickname(), winners.get(0));
            } else if (players.get(0).getAvailableTowerCount() > players.get(1).getAvailableTowerCount() || players.get(0).getControlledProfessors().size() < players.get(1).getControlledProfessors().size()) {
                // 1 wins
                assertEquals(1, winners.size());
                assertEquals(players.get(1).getNickname(), winners.get(0));
            } else if (players.get(0).getControlledProfessors().size() > players.get(1).getControlledProfessors().size()) {
                // 0 wins
                assertEquals(1, winners.size());
                assertEquals(players.get(0).getNickname(), winners.get(0));
            } else if (players.get(0).getControlledProfessors().size() < players.get(1).getControlledProfessors().size()) {
                // 1 wins
                assertEquals(1, winners.size());
                assertEquals(players.get(1).getNickname(), winners.get(0));
            } else {
                // Parity
                assertEquals(2, winners.size());
                assertEquals("Fede", winners.get(0));
                assertEquals("Ale", winners.get(1));
            }
        }, NotificationName.PlayerVictory, matchManager);
        //Pick the Cloud
        assertDoesNotThrow(() -> matchManager.runAction(0, null, 0, false, 0, 1));
        assertEquals(MatchPhase.PlanPhaseStepTwo, matchManager.getMatchPhase());
        assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
    }
}