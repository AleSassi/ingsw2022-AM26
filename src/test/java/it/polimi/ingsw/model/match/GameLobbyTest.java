package it.polimi.ingsw.model.match;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.assistants.Wizard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameLobbyTest {

    @Test
    void testEmptyMatch() {
        GameLobby lobby = new GameLobby(3, MatchVariant.BasicRulesSet);
        assertEquals(GameLobbyState.FillableWithPlayers, lobby.getCurrentState());
        assertEquals(0, lobby.getNumberOfPlayers());
    }

    @Test
    void testFullMatch() {
        GameLobby lobby = new GameLobby(2, MatchVariant.BasicRulesSet);
        assertDoesNotThrow(() -> {
            lobby.addPlayer("Ale", Wizard.Wizard1);
            assertEquals(1, lobby.getNumberOfPlayers());
            lobby.addPlayer("Fede", Wizard.Wizard2);
            assertEquals(2, lobby.getNumberOfPlayers());
        });
        assertThrows(LobbyFullException.class, () -> lobby.addPlayer("Leo", Wizard.Wizard3));
        assertEquals(2, lobby.getNumberOfPlayers());
    }

    @Test
    void testUniqueNicknameException() {
        GameLobby lobby = new GameLobby(2, MatchVariant.BasicRulesSet);
        assertThrows(NicknameNotUniqueException.class, () -> {
            lobby.addPlayer("Ale", Wizard.Wizard1);
            lobby.addPlayer("Ale", Wizard.Wizard2);
        });
    }
    /*
    @Test
    void testMatchManagerCreation() {
        GameLobby lobby = new GameLobby(2, MatchVariant.BasicRulesSet);
        assertDoesNotThrow(() -> {
            lobby.addPlayer("Ale", Wizard.Wizard1);
            assertNull(lobby.startGame());
            lobby.addPlayer("Fede", Wizard.Wizard2);
            MatchManager createdManager = lobby.startGame();
            assertNotNull(createdManager);
            assertEquals(2, createdManager.getAllPlayers().size());
            assertTrue(createdManager.getAllPlayers().stream().map(Player::getNickname).toList().contains("Ale"));
            assertTrue(createdManager.getAllPlayers().stream().map(Player::getNickname).toList().contains("Fede"));
        });
    }

    @Test
    void testTeamMatchManagerCreation() {
        GameLobby lobby = new GameLobby(4, MatchVariant.BasicRulesSet);
        assertDoesNotThrow(() -> {
            lobby.addPlayer("Ale", Wizard.Wizard1);
            assertNull(lobby.startGame());
            lobby.addPlayer("Fede", Wizard.Wizard2);
            assertNull(lobby.startGame());
            lobby.addPlayer("Leo", Wizard.Wizard3);
            assertNull(lobby.startGame());
            lobby.addPlayer("Prof", Wizard.Wizard4);
            MatchManager createdManager = lobby.startGame();
            assertNotNull(createdManager);
            assertEquals(4, createdManager.getAllPlayers().size());
            assertTrue(createdManager.getAllPlayers().stream().map(Player::getNickname).toList().contains("Ale"));
            assertTrue(createdManager.getAllPlayers().stream().map(Player::getNickname).toList().contains("Fede"));
            assertTrue(createdManager.getAllPlayers().stream().map(Player::getNickname).toList().contains("Leo"));
            assertTrue(createdManager.getAllPlayers().stream().map(Player::getNickname).toList().contains("Prof"));
        });
    }
    */
}