package it.polimi.ingsw.model;

import it.polimi.ingsw.model.assistants.Wizard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTeamTest {

    @Test
    void getAllPlayers() {
        PlayerTeam team1=new PlayerTeam();
        Player player1=new Player("pippo", Wizard.Wizard1,Tower.Black,8  );
        Player player2=new Player("giovanni", Wizard.Wizard1,Tower.Black,8  );
        team1.addPlayer(player1);
        team1.addPlayer(player2);
        ArrayList<Player> lista=team1.getAllPlayers();
        assertTrue(lista.contains(player1));
        assertTrue(lista.contains(player2));
    }

    @Test
    void containsPlayer() {
        PlayerTeam team1=new PlayerTeam();
        Player player1=new Player("pippo", Wizard.Wizard1,Tower.Black,8  );
        Player player2=new Player("giovanni", Wizard.Wizard1,Tower.Black,8  );
        team1.addPlayer(player1);
        assertTrue(team1.containsPlayer(player1));
        assertFalse(team1.containsPlayer(player2));


    }

    @Test
    void getLeadPlayer() {
        PlayerTeam team1=new PlayerTeam();
        Player player1=new Player("pippo", Wizard.Wizard1,Tower.Black,8  );
        Player player2=new Player("giovanni", Wizard.Wizard1,Tower.Black,0 );
        team1.addPlayer(player1);
        team1.addPlayer(player2);
        assertEquals(player1, team1.getLeadPlayer());
        assertNotEquals(player2, team1.getLeadPlayer());
    }
}