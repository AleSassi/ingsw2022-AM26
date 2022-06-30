package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.PlayerTeam;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.Wizard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTeamTest {
    
    private PlayerTeam testTeam;
    private Player player1, player2;
    
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

    @Test
    void getAllPlayers() {
        List<Player> playerList = testTeam.getAllPlayers();
        assertTrue(playerList.contains(player1));
        assertTrue(playerList.contains(player2));
    }

    @Test
    void containsPlayer() {
        assertTrue(testTeam.containsPlayer(player1));
        assertTrue(testTeam.containsPlayer(player2));
    }

    @Test
    void getLeadPlayer() {
        assertEquals(player1, testTeam.getLeadPlayer());
        assertNotEquals(player2, testTeam.getLeadPlayer());
    }
}