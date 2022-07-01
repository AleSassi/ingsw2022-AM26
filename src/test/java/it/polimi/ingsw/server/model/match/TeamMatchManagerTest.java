package it.polimi.ingsw.server.model.match;

import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.match.MatchVariant;
import it.polimi.ingsw.server.model.match.TeamMatchManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@code TeamMatchManagerTest} class tests {@link it.polimi.ingsw.server.model.match.TeamMatchManager TeamMatchManager}
 * @see TeamMatchManager
 */
class TeamMatchManagerTest {

    private TeamMatchManager matchManager ;
    
    /**
     * Common test initialization
     */
    @BeforeEach
    void initIndependentPlayerMatchManager() {
        matchManager = new TeamMatchManager();
        MatchVariant matchVariant = MatchVariant.ExpertRuleSet;
        List<String> playerNicknames = new ArrayList<>();
        List<Wizard> wiz = new ArrayList<>();
        playerNicknames.add("Fede");
        playerNicknames.add("Ale");
        playerNicknames.add("Leo");
        playerNicknames.add("Gio");
        wiz.add(Wizard.Wizard1);
        wiz.add(Wizard.Wizard2);
        wiz.add(Wizard.Wizard3);
        wiz.add(Wizard.Wizard4);
        assertDoesNotThrow(() -> matchManager.startMatch(matchVariant, playerNicknames, wiz));
    }

    /**
     * Test that the {@code getPlayersWithTowers} returns the correct {@link it.polimi.ingsw.server.model.Player Players}
     */
    @Test
    void getPlayersWithTowers() {
        assertEquals("Fede", matchManager.getPlayersWithTowers().get(0).getNickname());
        assertEquals("Leo", matchManager.getPlayersWithTowers().get(1).getNickname());
    }
}