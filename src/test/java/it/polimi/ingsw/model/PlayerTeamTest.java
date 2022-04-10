package it.polimi.ingsw.model;

import it.polimi.ingsw.model.assistants.Wizard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTeamTest {
    
    private PlayerTeam testTeam;
    private Player player1, player2;
    
    @BeforeEach
    void initTeam() {
        testTeam = new PlayerTeam();
        assertDoesNotThrow(() -> {
            player1 = new Player("pippo", Wizard.Wizard1, Tower.Black, 8);
            player2 = new Player("giovanni", Wizard.Wizard1, Tower.Black, 0);
            testTeam.addPlayer(player1);
            testTeam.addPlayer(player2);
        });
    }

    @Test
    void getAllPlayers() {
        ArrayList<Player> playerList = testTeam.getAllPlayers();
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