package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.GameServer;
import it.polimi.ingsw.server.controller.network.messages.*;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.Wizard;
import it.polimi.ingsw.server.model.match.MatchPhase;
import it.polimi.ingsw.server.model.match.MatchVariant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameControllerTest {
	
	private GameController controller;
	private List<NetworkMessage> sentMessages;
	
	@BeforeEach
	void setup() {
		GameServer server = mock(GameServer.class);
		controller = new GameController(server);
		sentMessages = new ArrayList<>();
		doAnswer((Answer<NetworkMessage>) invocationOnMock -> {
			Object[] args = invocationOnMock.getArguments();
			if (args[0] instanceof NetworkMessage message) {
				sentMessages.add(message);
			}
			return null;
		}).when(server).sendMessage(any(NetworkMessage.class), anyString());
	}
	
	@Test
	void testSimpleLogin() {
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new LoginMessage("Ale", 2, MatchVariant.BasicRuleSet, Wizard.Wizard1));
		NotificationCenter.shared().post(NotificationName.ServerDidReceiveLoginMessage, controller, userInfo);
		assertEquals(new LoginResponse("Ale", true, 1, null), sentMessages.get(0));
		assertTrue(controller.containsPlayerWithNickname("Ale"));
		assertTrue(controller.acceptsPlayers());
	}
	
	@Test
	void testSequenceLogin() {
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new LoginMessage("Ale", 2, MatchVariant.BasicRuleSet, Wizard.Wizard1));
		NotificationCenter.shared().post(NotificationName.ServerDidReceiveLoginMessage, controller, userInfo);
		userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new LoginMessage("Fra", 2, MatchVariant.BasicRuleSet, Wizard.Wizard2));
		NotificationCenter.shared().post(NotificationName.ServerDidReceiveLoginMessage, controller, userInfo);
		assertEquals(new LoginResponse("Ale", true, 1, null), sentMessages.get(0));
		assertEquals(new LoginResponse("Fra", true, 0, null), sentMessages.get(1));
		assertTrue(controller.containsPlayerWithNickname("Ale"));
		assertTrue(controller.containsPlayerWithNickname("Fra"));
		assertFalse(controller.acceptsPlayers());
	}
	
	@Test
	void testMultiLogin() {
		sentMessages = new ArrayList<>();
		AtomicInteger count = new AtomicInteger();
		AtomicBoolean isAleFirst = new AtomicBoolean(false);
		new Thread(() -> {
			HashMap<String, Object> userInfo = new HashMap<>();
			userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new LoginMessage("Ale", 3, MatchVariant.BasicRuleSet, Wizard.Wizard1));
			NotificationCenter.shared().post(NotificationName.ServerDidReceiveLoginMessage, controller, userInfo);
			if (count.addAndGet(1) == 1) {
				isAleFirst.set(true);
			}
		}).start();
		new Thread(() -> {
			HashMap<String, Object> userInfo = new HashMap<>();
			userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new LoginMessage("Fra", 3, MatchVariant.BasicRuleSet, Wizard.Wizard2));
			NotificationCenter.shared().post(NotificationName.ServerDidReceiveLoginMessage, controller, userInfo);
			count.addAndGet(1);
		}).start();
		while (count.get() != 2) {}
		assertTrue(sentMessages.contains(new LoginResponse("Ale", true, isAleFirst.get() ? 2 : 1, null)));
		assertTrue(sentMessages.contains(new LoginResponse("Fra", true, isAleFirst.get() ? 1 : 2, null)));
		assertEquals(3, sentMessages.size());
		assertTrue(controller.containsPlayerWithNickname("Ale"));
		assertTrue(controller.containsPlayerWithNickname("Fra"));
		assertTrue(controller.acceptsPlayers());
	}
	
	@Test
	void testLoginAndMatchStart() {
		testSequenceLogin();
		assertTrue(sentMessages.get(3) instanceof TableStateMessage);
		assertTrue(sentMessages.get(4) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(5) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(6) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
		assertTrue(sentMessages.get(7) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.PlanPhaseStepTwo);
		assertTrue(sentMessages.get(8) instanceof TableStateMessage);
		assertTrue(sentMessages.get(9) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(10) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(11) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
		assertTrue(sentMessages.get(12) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.PlanPhaseStepTwo);
		assertEquals(13, sentMessages.size());
	}
	
}