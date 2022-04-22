package it.polimi.ingsw.model.match;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.assistants.AssistantCard;
import it.polimi.ingsw.model.assistants.AvailableCardsDeck;
import it.polimi.ingsw.model.assistants.Wizard;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.CharacterCardParamSet;
import it.polimi.ingsw.model.characters.GenericModifierCard;
import it.polimi.ingsw.model.student.Student;
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
        assertEquals(9, matchManager.getAllPlayers().get(0).getStudentInEntrance());
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
    @Test
    void actionPhaseOneRoomTest() {
        /*matchManager.setMatchPhase(MatchPhase.ActionPhaseStepOne);
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
            matchManager.runAction(0, finalRemovedStudent, 1, false, 0 , 0 );
        });
        assertEquals(8, matchManager.getCurrentPlayer().getStudentInEntrance());
        assertEquals(1, matchManager.getCurrentPlayer().getCountAtTable(finalRemovedStudent));
        matchManager.getCurrentPlayer().addStudentToEntrance(finalRemovedStudent);*/
    }

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
        assertEquals(8, matchManager.getCurrentPlayer().getStudentInEntrance());
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

    @RepeatedTest(100)
    void actionPhaseTwoIslandTest() {
        /*matchManager.setMatchPhase(MatchPhase.ActionPhaseStepTwo);
        int additionalSteps = 0;
        CharacterCardParamSet userInfo = new CharacterCardParamSet(null, null, null, null, false, 1, 0, 0, null);

        int motherNaturePos = matchManager.getManagedTable().circularWrap(matchManager.getManagedTable().getCurrentIslandIndex(), 11);
        System.out.println("mn index : "+ matchManager.getManagedTable().getCurrentIslandIndex());
        System.out.println("wrap : "+ matchManager.getManagedTable().circularWrap(matchManager.getManagedTable().getCurrentIslandIndex(), 11));
        try {
            if(matchManager.getManagedTable().getPlayableCharacterCards().contains(Character.Magician)) {
                matchManager.purchaseCharacterCards(matchManager.getManagedTable().getPlayableCharacterCards().indexOf(Character.Magician));
                matchManager.useCharacterCard(userInfo);
                additionalSteps++;
            }
        } catch (CharacterCardIncorrectParametersException | CharacterCardNoMoreUsesAvailableException ignored) {

        }
        assertDoesNotThrow(() -> {
            matchManager.runAction(0, null, 0, false, 3, 0);
        });
        assertEquals(motherNaturePos + 3 + additionalSteps, matchManager.getManagedTable().getCurrentIslandIndex());*/


    }

    @Test
    void moveToNextPlayerTest() {
        matchManager.setMatchPhase(MatchPhase.PlanPhaseStepTwo);
    }

    @Test
    void getPlayersSortedByRoundTurnOrderTest() {
        matchManager.setMatchPhase(MatchPhase.PlanPhaseStepTwo);
        List<Player> players = new ArrayList<>();

        assertDoesNotThrow(() -> {
            matchManager.getCurrentPlayer().playAssistantCardAtIndex(0);
            players.add(matchManager.getCurrentPlayer());
            matchManager.moveToNextPlayer();
            matchManager.getCurrentPlayer().playAssistantCardAtIndex(1);
            players.add(matchManager.getCurrentPlayer());
            matchManager.moveToNextPlayer();
            matchManager.getCurrentPlayer().playAssistantCardAtIndex(2);
            players.add(matchManager.getCurrentPlayer());
            matchManager.moveToNextPlayer();
        });




    }

    @Test
    void getCurrentPlayerTest() {
        assertEquals("Fede", matchManager.getCurrentPlayer().getNickname());
        matchManager.moveToNextPlayer();
        assertEquals("Ale", matchManager.getCurrentPlayer().getNickname());
    }

    @Test
    void isAssistantCardPlayable() {
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