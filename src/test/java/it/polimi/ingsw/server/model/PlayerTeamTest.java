package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.PlayerTeam;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.Wizard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to the Player Team
 * @see  PlayerTeam
 */
class PlayerTeamTest {
    
    private PlayerTeam testTeam;
    private Player player1, player2;
    
    /**
     * try to add Player{@link it.polimi.ingsw.server.model.Player Player}to the team and verify no exception are thrown
     */
    @BeforeEach
    void initTeam() {
        testTeam = new PlayerTeam();
        assertDoesNotThrow(() -> {
            player1 = new Player("pippo", Wizard.Wizard1, Tower.Black, 8, 1);
            player2 = new Player("giovanni", Wizard.Wizard1, Tower.Black, 0, 1);
            testTeam.addPlayer(player1);
            testTeam.addPlayer(player2);
        });
    }
    /**
     * verify the returned list contain all the  Player{@link it.polimi.ingsw.server.model.Player Player} of team
     */
    @Test
    void getAllPlayers() {
        List<Player> playerList = testTeam.getAllPlayers();
        assertTrue(playerList.contains(player1));
        assertTrue(playerList.contains(player2));
    }
    /**
     * verify the team contain Player{@link it.polimi.ingsw.server.model.Player Player}
     */
    @Test
    void containsPlayer() {
        assertTrue(testTeam.containsPlayer(player1));
        assertTrue(testTeam.containsPlayer(player2));
    }
    /**
     * verify the  Player{@link it.polimi.ingsw.server.model.Player Player} returned is the leader
     */
    @Test
    void getLeadPlayer() {
        assertEquals(player1, testTeam.getLeadPlayer());
        assertNotEquals(player2, testTeam.getLeadPlayer());
    }
}