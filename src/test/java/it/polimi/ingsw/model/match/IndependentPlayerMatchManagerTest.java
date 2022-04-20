package it.polimi.ingsw.model.match;

import it.polimi.ingsw.exceptions.IncorrectConstructorParametersException;
import it.polimi.ingsw.exceptions.InvalidPlayerCountException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.assistants.Wizard;
import org.junit.jupiter.api.BeforeEach;
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
        playerNicknames.add("leo");
        wiz.add(Wizard.Wizard1);
        wiz.add(Wizard.Wizard2);
        wiz.add(Wizard.Wizard3);
        assertDoesNotThrow(() -> {
            matchManager.startMatch(matchVariant, playerNicknames, wiz);
        });
    }



    @Test
    void runAction() {
        //Test PPTwo
    }

    @Test
    void moveToNextPlayer() {
    }

    @Test
    void getPlayersSortedByRoundTurnOrder() {

    }

    @Test
    void getCurrentPlayer() {
    }

    @Test
    void isAssistantCardPlayable() {
    }

    @Test
    void purchaseCharacterCards() {
    }

    @Test
    void useCharacterCard() {
    }

    /**
     * This method test that a player is added correctly
     *
     *
     */
    @Test
    void addPlayer() {
        assertDoesNotThrow(() -> {
            matchManager.addPlayer("fede", Wizard.Wizard1, 6);
        });
        assertEquals("fede", matchManager.getAllPlayers().get(0).getNickname());
        assertEquals(6, matchManager.getAllPlayers().get(0).getAvailableTowerCount());


    }

    /**
     * This method test that the list of all players is returned correctly
     *
     */
    @Test
    void getAllPlayers() throws IncorrectConstructorParametersException, InvalidPlayerCountException {
        matchManager.addPlayer("fede", Wizard.Wizard1, 6);
        matchManager.addPlayer("leo", Wizard.Wizard2, 6);
        matchManager.addPlayer("ale", Wizard.Wizard3, 6);
        List<Player> players = new ArrayList<>();
        assertEquals("fede", matchManager.getAllPlayers().get(0).getNickname());
        assertEquals("leo", matchManager.getAllPlayers().get(1).getNickname());
        assertEquals("ale", matchManager.getAllPlayers().get(2).getNickname());
    }

    /**
     * This method test that the list of players having towers is returned correctly
     */
    @Test
    void getPlayersWithTowers() throws IncorrectConstructorParametersException, InvalidPlayerCountException {
        matchManager.addPlayer("fede", Wizard.Wizard1, 6);
        matchManager.addPlayer("leo", Wizard.Wizard2, 6);
        assertEquals("fede", matchManager.getPlayersWithTowers().get(0).getNickname());
        assertEquals("leo", matchManager.getPlayersWithTowers().get(1).getNickname());
    }
}