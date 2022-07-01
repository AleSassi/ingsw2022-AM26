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
import it.polimi.ingsw.server.model.student.Student;
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

/**
 * Class {@code GameControllerTest} tests {@link it.polimi.ingsw.server.controller.GameController}
 * @see GameControllerTest
 */
class GameControllerTest {
	
	private GameController controller;
	private List<NetworkMessage> sentMessages;
	
	/**
	 * Test setup and initialization for mocking the server and writing messages to a list
	 */
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

	/**
	 * Tests a simple login
	 */
	@Test
	void testSimpleLogin() {
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new LoginMessage("Ale", 2, MatchVariant.ExpertRuleSet, Wizard.Wizard1));
		NotificationCenter.shared().post(NotificationName.ServerDidReceiveLoginMessage, controller, userInfo);
		assertEquals(new LoginResponse("Ale", true, 1, null), sentMessages.get(0));
		assertTrue(controller.containsPlayerWithNickname("Ale"));
		assertTrue(controller.acceptsPlayers());
		assertEquals(2, controller.getMaxPlayerCount());
		assertEquals(MatchVariant.ExpertRuleSet, controller.getMatchVariant());
	}

	/**
	 * Tests two logins in sequence
	 */
	@Test
	void testSequenceLogin() {
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new LoginMessage("Ale", 2, MatchVariant.ExpertRuleSet, Wizard.Wizard1));
		NotificationCenter.shared().post(NotificationName.ServerDidReceiveLoginMessage, controller, userInfo);
		userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new LoginMessage("Fra", 2, MatchVariant.ExpertRuleSet, Wizard.Wizard2));
		NotificationCenter.shared().post(NotificationName.ServerDidReceiveLoginMessage, controller, userInfo);
		assertEquals(new LoginResponse("Ale", true, 1, null), sentMessages.get(0));
		assertEquals(new LoginResponse("Fra", true, 0, null), sentMessages.get(1));
		assertTrue(controller.containsPlayerWithNickname("Ale"));
		assertTrue(controller.containsPlayerWithNickname("Fra"));
		assertFalse(controller.acceptsPlayers());
		assertEquals(2, controller.getMaxPlayerCount());
		assertEquals(MatchVariant.ExpertRuleSet, controller.getMatchVariant());
	}

	/**
	 * Tests multiple logins on different threads
	 */
	@Test
	void testMultiLogin() {
		sentMessages = new ArrayList<>();
		AtomicInteger count = new AtomicInteger();
		AtomicBoolean isAleFirst = new AtomicBoolean(false);
		new Thread(() -> {
			HashMap<String, Object> userInfo = new HashMap<>();
			userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new LoginMessage("Ale", 3, MatchVariant.ExpertRuleSet, Wizard.Wizard1));
			NotificationCenter.shared().post(NotificationName.ServerDidReceiveLoginMessage, controller, userInfo);
			if (count.addAndGet(1) == 1) {
				isAleFirst.set(true);
			}
		}).start();
		new Thread(() -> {
			HashMap<String, Object> userInfo = new HashMap<>();
			userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new LoginMessage("Fra", 3, MatchVariant.ExpertRuleSet, Wizard.Wizard2));
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
		assertEquals(3, controller.getMaxPlayerCount());
		assertEquals(MatchVariant.ExpertRuleSet, controller.getMatchVariant());
	}

	/**
	 * Tests the login phase and start the match
	 */
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

	/**
	 * Tests that a {@link it.polimi.ingsw.server.model.Player Player} can't log in a full lobby
	 */
	@Test
	void testPlayerOverflow() {
		testLoginAndMatchStart();
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new LoginMessage("Test", 2, MatchVariant.ExpertRuleSet, Wizard.Wizard3));
		NotificationCenter.shared().post(NotificationName.ServerDidReceiveLoginMessage, controller, userInfo);
		assertEquals(new LoginResponse("Test", false, Integer.MAX_VALUE, "The lobby you entered in is already full"), sentMessages.get(13));
		assertFalse(controller.containsPlayerWithNickname("Test"));
	}

	/**
	 * Tests that a {@link it.polimi.ingsw.server.model.Player Player} can't log in while another {@code Player} has the same nickname
	 */
	@Test
	void testBlockLoginWithSameName() {
		testMultiLogin();
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new LoginMessage("Ale", 3, MatchVariant.ExpertRuleSet, Wizard.Wizard3));
		NotificationCenter.shared().post(NotificationName.ServerDidReceiveLoginMessage, controller, userInfo);
		assertEquals(new LoginResponse("Ale", false, Integer.MAX_VALUE, "The nickname you entered is not unique. Please choose another nickname"), sentMessages.get(3));
	}

	/**
	 * Tests that a {@link it.polimi.ingsw.server.model.Player Player} can't log in while another {@code Player} has the same wizard
	 */
	@Test
	void testBlockLoginWithSameWizard() {
		testMultiLogin();
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new LoginMessage("Test", 3, MatchVariant.ExpertRuleSet, Wizard.Wizard2));
		NotificationCenter.shared().post(NotificationName.ServerDidReceiveLoginMessage, controller, userInfo);
		assertEquals(new LoginResponse("Test", false, Integer.MAX_VALUE, "The Wizard you have chosen has already been taken by another player in the same lobby"), sentMessages.get(3));
	}

	/**
	 * Tests the termination of the game
	 */
	@Test
	void testTerminate() {
		NotificationCenter.shared().post(NotificationName.ServerDidTerminateMatch, controller, null);
		assertTrue(controller.isTerminated());
	}

	/**
	 * Tests the victory notification
	 */
	@RepeatedTest(100)
	void testVictoryNotificationForwarding() {
		testLoginAndMatchStart();
		List<String> winnerNicknames = Arrays.stream((new String[]{"Ale", "Fra"})).toList();
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.WinnerNickname.getRawValue(), winnerNicknames);
		NotificationCenter.shared().post(NotificationName.PlayerVictory, null /* so that it reaches the controller without adding test-specific methods */, userInfo);
		NetworkMessage victoryMessage = new VictoryMessage(winnerNicknames.toArray(new String[0]));
		assertEquals(victoryMessage, sentMessages.get(13));
		assertEquals(victoryMessage, sentMessages.get(14));
	}

	/**
	 * Tests that all the {@link it.polimi.ingsw.server.model.Player Player's} action are blocked if not logged in
	 */
	@Test
	void testBlockActionWhenNotLoggedIn() {
		testSimpleLogin();
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidPlayAssistantCard, 0, null, false, 0, 0, 0, 0 , null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPlayAssistantCard, false, "You are trying to run an Action without logging in first, or without waiting for the Match to start"), sentMessages.get(1));
	}

	/**
	 * Tests the PlayAssistant move
	 */
	@Test
	void testReceivePlayAssistantMessage() {
		testLoginAndMatchStart();
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidPlayAssistantCard, 0, null, false, 0, 0, 0, 0 , null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPlayAssistantCard, true, ""), sentMessages.get(13));
		assertTrue(sentMessages.get(14) instanceof TableStateMessage);
		assertTrue(sentMessages.get(15) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(16) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(17) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
		assertTrue(sentMessages.get(18) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.PlanPhaseStepTwo);
		assertTrue(sentMessages.get(19) instanceof TableStateMessage);
		assertTrue(sentMessages.get(20) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(21) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(22) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
		assertTrue(sentMessages.get(23) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.PlanPhaseStepTwo);
	}

	/**
	 * Tests that if the PlayAssistant move has the wrong parameters doesn't go through
	 */
	@Test
	void testReceivePlayAssistantMessageWithParamError() {
		testLoginAndMatchStart();
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidPlayAssistantCard, 29, null, false, 0, 0, 0, 0 , null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPlayAssistantCard, false, "Invalid move: the Assistant Card you chose cannot be played, because other opponents have already played it before you or the index is not valid"), sentMessages.get(13));
	}

	/**
	 * Tests the case of a message received by an invalid {@link it.polimi.ingsw.server.model.Player Player}
	 */
	@Test
	void testReceiveMessageFromInvalidPlayer() {
		testLoginAndMatchStart();
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Fra", PlayerActionMessage.ActionType.DidPlayAssistantCard, 29, null, false, 0, 0, 0, 0 , null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(new PlayerActionResponse("Fra", PlayerActionMessage.ActionType.DidPlayAssistantCard, false, "Invalid move: you are not the current Player"), sentMessages.get(13));
	}

	/**
	 * Tests an invalid move
	 */
	@Test
	void testInvalidMove() {
		testLoginAndMatchStart();
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidMoveStudent, 0, Student.BlueUnicorn, false, 0, 0, 0, 0 , null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidMoveStudent, false, "Invalid move: the action is not valid for the current Match Phase"), sentMessages.get(13));
	}

	/**
	 * Tests a complete round
	 */
	@Test
	void testRound() {
		testReceivePlayAssistantMessage();
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Fra", PlayerActionMessage.ActionType.DidPlayAssistantCard, 1, null, false, 0, 0, 0, 0 , null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(new PlayerActionResponse("Fra", PlayerActionMessage.ActionType.DidPlayAssistantCard, true, ""), sentMessages.get(24));
		assertTrue(sentMessages.get(25) instanceof TableStateMessage);
		assertTrue(sentMessages.get(26) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(27) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(28) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
		assertTrue(sentMessages.get(29) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
		assertTrue(sentMessages.get(30) instanceof TableStateMessage);
		assertTrue(sentMessages.get(31) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(32) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(33) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
		assertTrue(sentMessages.get(34) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
	}

	/**
	 * Tests the {@link it.polimi.ingsw.server.model.student.Student Student} move to the dining room message
	 */
	@RepeatedTest(5)
	void testReceiveStudToTableMessage() {
		testRound();
		HashMap<String, Object> userInfo = new HashMap<>();
		PlayerStateMessage fraState = (PlayerStateMessage) sentMessages.get(27);
		Student studentToMove = null;
		for (Student student: Student.values()) {
			if (fraState.getBoard().getEntrance().getCount(student) > 0) {
				studentToMove = student;
				break;
			}
		}
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidMoveStudent, 0, studentToMove, false, 0, 0, 0, 0 , null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidMoveStudent, true, ""), sentMessages.get(35));
		assertTrue(sentMessages.get(36) instanceof TableStateMessage);
		assertTrue(sentMessages.get(37) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(38) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(39) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
		assertTrue(sentMessages.get(40) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
		assertTrue(sentMessages.get(41) instanceof TableStateMessage);
		assertTrue(sentMessages.get(42) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(43) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(44) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
		assertTrue(sentMessages.get(45) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
	}

	/**
	 * Tests the {@link it.polimi.ingsw.server.model.student.Student Student} move to the island message
	 */
	@Test
	void testReceiveStudToIslandMessage() {
		testRound();
		HashMap<String, Object> userInfo = new HashMap<>();
		PlayerStateMessage fraState = (PlayerStateMessage) sentMessages.get(27);
		Student studentToMove = null;
		for (Student student: Student.values()) {
			if (fraState.getBoard().getEntrance().getCount(student) > 0) {
				studentToMove = student;
				break;
			}
		}
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidMoveStudent, 0, studentToMove, true, 0, 0, 0, 0 , null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidMoveStudent, true, ""), sentMessages.get(35));
		assertTrue(sentMessages.get(36) instanceof TableStateMessage);
		assertTrue(sentMessages.get(37) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(38) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(39) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
		assertTrue(sentMessages.get(40) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
		assertTrue(sentMessages.get(41) instanceof TableStateMessage);
		assertTrue(sentMessages.get(42) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(43) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(44) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
		assertTrue(sentMessages.get(45) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
	}

	/**
	 * Tests the {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard's} purchase message but without coins
	 */
	@RepeatedTest(10)
	void testReceivePurchaseCharacterCardWithoutCoins() {
		testRound();
		HashMap<String, Object> userInfo = new HashMap<>();
		TableStateMessage tableStateMessage = (TableStateMessage) sentMessages.get(25);
		int cardIndex = -1;
		for (int i = 0; i < 3; i++) {
			if (tableStateMessage.getPlayableCharacterCards().get(i).getTotalPrice() > 1) {
				cardIndex = i;
				break;
			}
		}
		if (cardIndex >= 0) {
			userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidPurchaseCharacterCard, 0, null, true, 0, 0, 0, cardIndex, null));
			NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
			assertEquals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPurchaseCharacterCard, false, "Not enough Coins to purchase the Card"), sentMessages.get(35));
		}
	}

	/**
	 * Tests sending a message for purchasing a card with out of bounds index
	 */
	@Test
	void testReceivePurchaseCharacterCardOutOfBounds() {
		testRound();
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidPurchaseCharacterCard, 0, null, true, 0, 0, 0, 77, null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPurchaseCharacterCard, false, "Invalid action: the Character Card index you sent was incorrect"), sentMessages.get(35));
	}
	
	/**
	 * Tests the case of an already purchased {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard}
	 */
	@RepeatedTest(10)
	void testReceivePurchaseCharacterCardAlreadyInUse() {
		testRound();
		//Simulate Ale's turn
		TableStateMessage tableStateMessage = (TableStateMessage) sentMessages.get(25);
		int cardIndex = -1;
		for (int i = 0; i < 3; i++) {
			if (tableStateMessage.getPlayableCharacterCards().get(i).getTotalPrice() == 1) {
				cardIndex = i;
				break;
			}
		}
		if (cardIndex >= 0) {
			HashMap<String, Object> userInfo = new HashMap<>();
			int i = 0;
			userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidPurchaseCharacterCard, 0, null, false, 0, 0, 0, cardIndex, null));
			NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
			assertTrue(sentMessages.get(35 + 11 * i).equals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPurchaseCharacterCard, true, "-1")) || sentMessages.get(35 + 11 * i).equals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPurchaseCharacterCard, true, "")));
			assertTrue(sentMessages.get(36 + 11 * i) instanceof TableStateMessage);
			assertTrue(sentMessages.get(37 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(38 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(39 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
			assertTrue(sentMessages.get(40 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == (i < 2 ? MatchPhase.ActionPhaseStepOne : MatchPhase.ActionPhaseStepTwo));
			assertTrue(sentMessages.get(41 + 11 * i) instanceof TableStateMessage);
			assertTrue(sentMessages.get(42 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(43 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(44 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
			assertTrue(sentMessages.get(45 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == (i < 2 ? MatchPhase.ActionPhaseStepOne : MatchPhase.ActionPhaseStepTwo));
			i += 1;
			
			for (; i < 4; i++) {
				PlayerStateMessage fraState = (PlayerStateMessage) sentMessages.get(27 + 11 * i);
				Student studentToMove = null;
				for (Student student: Student.values()) {
					if (fraState.getBoard().getEntrance().getCount(student) > 0) {
						studentToMove = student;
						break;
					}
				}
				userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidMoveStudent, 0, studentToMove, false, 0, 0, 0, 0, null));
				NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
				assertEquals(sentMessages.get(35 + 11 * i), new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidMoveStudent, true, ""));
				assertTrue(sentMessages.get(36 + 11 * i) instanceof TableStateMessage);
				assertTrue(sentMessages.get(37 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
				assertTrue(sentMessages.get(38 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
				assertTrue(sentMessages.get(39 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
				assertTrue(sentMessages.get(40 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == (i < 3 ? MatchPhase.ActionPhaseStepOne : MatchPhase.ActionPhaseStepTwo));
				assertTrue(sentMessages.get(41 + 11 * i) instanceof TableStateMessage);
				assertTrue(sentMessages.get(42 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
				assertTrue(sentMessages.get(43 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
				assertTrue(sentMessages.get(44 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
				assertTrue(sentMessages.get(45 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == (i < 3 ? MatchPhase.ActionPhaseStepOne : MatchPhase.ActionPhaseStepTwo));
			}
			
			userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidMoveMNBySteps, 0, null, false, 0, 1, 0, 0, null));
			NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
			assertEquals(sentMessages.get(35 + 11 * i), new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidMoveMNBySteps, true, ""));
			assertTrue(sentMessages.get(36 + 11 * i) instanceof TableStateMessage);
			assertTrue(sentMessages.get(37 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(38 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
			assertTrue(sentMessages.get(39 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
			assertTrue(sentMessages.get(40 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepThree);
			assertTrue(sentMessages.get(41 + 11 * i) instanceof TableStateMessage);
			assertTrue(sentMessages.get(42 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
			assertTrue(sentMessages.get(43 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(44 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
			assertTrue(sentMessages.get(45 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepThree);
			i += 1;
			
			userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidChooseCloudIsland, 0, null, false, 0, 1, 0, 0, null));
			NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
			assertEquals(sentMessages.get(35 + 11 * i), new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidChooseCloudIsland, true, ""));
			assertTrue(sentMessages.get(36 + 11 * i) instanceof TableStateMessage);
			assertTrue(sentMessages.get(37 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(38 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
			assertTrue(sentMessages.get(39 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
			assertTrue(sentMessages.get(40 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
			assertTrue(sentMessages.get(41 + 11 * i) instanceof TableStateMessage);
			assertTrue(sentMessages.get(42 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
			assertTrue(sentMessages.get(43 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(44 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
			assertTrue(sentMessages.get(45 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
			i += 1;
			
			// Test for Fra
			tableStateMessage = (TableStateMessage) sentMessages.get(36 + 11 * (i - 1));
			cardIndex = -1;
			for (int j = 0; j < 3; j++) {
				if (tableStateMessage.getPlayableCharacterCards().get(j).getTotalPrice() == 1) {
					cardIndex = j;
					break;
				}
			}
			if (cardIndex >= 0) {
				userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Fra", PlayerActionMessage.ActionType.DidPurchaseCharacterCard, 0, null, true, 0, 0, 0, cardIndex, null));
				NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
				assertTrue(sentMessages.get(35 + 11 * i).equals(new PlayerActionResponse("Fra", PlayerActionMessage.ActionType.DidPurchaseCharacterCard, true, "-1")) || sentMessages.get(35 + 11 * i).equals(new PlayerActionResponse("Fra", PlayerActionMessage.ActionType.DidPurchaseCharacterCard, true, "")));
				assertTrue(sentMessages.get(36 + 11 * i) instanceof TableStateMessage);
				assertTrue(sentMessages.get(37 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
				assertTrue(sentMessages.get(38 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
				assertTrue(sentMessages.get(39 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
				assertTrue(sentMessages.get(40 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
				assertTrue(sentMessages.get(41 + 11 * i) instanceof TableStateMessage);
				assertTrue(sentMessages.get(42 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
				assertTrue(sentMessages.get(43 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
				assertTrue(sentMessages.get(44 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
				assertTrue(sentMessages.get(45 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
			}
		}
	}

	/**
	 * Tests the case of a {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard} played without purchasing it
	 */
	@Test
	void testReceivePlayCharacterCardWithoutPurchase() {
		testRound();
		HashMap<String, Object> userInfo = new HashMap<>();
		TableStateMessage tableStateMessage = (TableStateMessage) sentMessages.get(25);
		int cardIndex = -1;
		for (int i = 0; i < 3; i++) {
			if (tableStateMessage.getPlayableCharacterCards().get(i).getTotalPrice() > 1) {
				cardIndex = i;
				break;
			}
		}
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidPlayCharacterCard, 0, null, true, 0, 0, 0, cardIndex, null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPlayCharacterCard, false, "Invalid move: you have not purchased the Character card"), sentMessages.get(35));
	}

	/**
	 * Tests a {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard} use with the wrong parameters
	 */
	@RepeatedTest(10)
	void testReceivePlayCharacterWithWrongParams() {
		testRound();
		HashMap<String, Object> userInfo = new HashMap<>();
		TableStateMessage tableStateMessage = (TableStateMessage) sentMessages.get(25);
		int cardIndex = -1;
		for (int i = 0; i < 3; i++) {
			if (tableStateMessage.getPlayableCharacterCards().get(i).getTotalPrice() == 1) {
				cardIndex = i;
				break;
			}
		}
		if (cardIndex >= 0 && (tableStateMessage.getPlayableCharacterCards().get(cardIndex).getCharacter().getChangesMNSteps() || tableStateMessage.getPlayableCharacterCards().get(cardIndex).getCharacter().getHostedStudentsCount() > 0)) {
			userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidPurchaseCharacterCard, 0, null, true, 0, 0, 0, cardIndex, null));
			NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
			assertTrue(sentMessages.get(35).equals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPurchaseCharacterCard, true, "-1")) || sentMessages.get(35).equals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPurchaseCharacterCard, true, "")));
			assertTrue(sentMessages.get(36) instanceof TableStateMessage);
			assertTrue(sentMessages.get(37) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(38) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(39) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
			assertTrue(sentMessages.get(40) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
			assertTrue(sentMessages.get(41) instanceof TableStateMessage);
			assertTrue(sentMessages.get(42) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(43) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(44) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
			assertTrue(sentMessages.get(45) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
			
			userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidPlayCharacterCard, 0, null, true, 0, 0, 0, cardIndex, new CharacterCardNetworkParamSet(null, null, false, -1, -1, -1, null)));
			NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
			assertEquals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPlayCharacterCard, false, "Invalid move: the Character Card parameters are not valid"), sentMessages.get(46));
		}
	}

	/**
	 * Test the case of multiple {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard} uses which exceed the card limit
	 */
	@RepeatedTest(20)
	void testTooManyCardUses() {
		testRound();
		HashMap<String, Object> userInfo = new HashMap<>();
		TableStateMessage tableStateMessage = (TableStateMessage) sentMessages.get(25);
		int cardIndex = -1;
		for (int i = 0; i < 3; i++) {
			if (tableStateMessage.getPlayableCharacterCards().get(i).getTotalPrice() == 1) {
				cardIndex = i;
				break;
			}
		}
		if (cardIndex >= 0 && (tableStateMessage.getPlayableCharacterCards().get(cardIndex).getCharacter().getHostedStudentsCount() > 0)) {
			userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidPurchaseCharacterCard, 0, null, true, 0, 0, 0, cardIndex, null));
			NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
			assertTrue(sentMessages.get(35).equals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPurchaseCharacterCard, true, "-1")) || sentMessages.get(35).equals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPurchaseCharacterCard, true, "")));
			assertTrue(sentMessages.get(36) instanceof TableStateMessage);
			assertTrue(sentMessages.get(37) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(38) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(39) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
			assertTrue(sentMessages.get(40) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
			assertTrue(sentMessages.get(41) instanceof TableStateMessage);
			assertTrue(sentMessages.get(42) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(43) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(44) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
			assertTrue(sentMessages.get(45) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
			
			int numberOfTimesPlayed = 0;
			while (numberOfTimesPlayed < tableStateMessage.getPlayableCharacterCards().get(cardIndex).getCharacter().getMaxNumberOfUsesInTurn()) {
				Student studentToMove = null;
				tableStateMessage = (TableStateMessage) sentMessages.get(36 + 11 * numberOfTimesPlayed);
				for (Student student: Student.values()) {
					if (tableStateMessage.getPlayableCharacterCards().get(cardIndex).getHostedStudents().getCount(student) > 0) {
						studentToMove = student;
						break;
					}
				}
				PlayerStateMessage fraState = (PlayerStateMessage) sentMessages.get(38 + 11 * numberOfTimesPlayed);
				Student studentInEntrance = null;
				for (Student student: Student.values()) {
					if (fraState.getBoard().getEntrance().getCount(student) > 0) {
						studentInEntrance = student;
						break;
					}
				}
				userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidPlayCharacterCard, 0, null, true, 0, 0, 0, cardIndex, new CharacterCardNetworkParamSet(studentToMove, studentInEntrance, false, 1, 0, 0, null)));
				NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
				assertTrue(sentMessages.get(46 + 11 * numberOfTimesPlayed).equals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPlayCharacterCard, true, "1")) || sentMessages.get(46 + 11 * numberOfTimesPlayed).equals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPlayCharacterCard, true, "0")) || sentMessages.get(46 + 11 * numberOfTimesPlayed).equals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPlayCharacterCard, true, "")));
				assertTrue(sentMessages.get(47 + 11 * numberOfTimesPlayed) instanceof TableStateMessage);
				assertTrue(sentMessages.get(48 + 11 * numberOfTimesPlayed) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
				assertTrue(sentMessages.get(49 + 11 * numberOfTimesPlayed) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
				assertTrue(sentMessages.get(50 + 11 * numberOfTimesPlayed) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
				assertTrue(sentMessages.get(51 + 11 * numberOfTimesPlayed) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
				assertTrue(sentMessages.get(52 + 11 * numberOfTimesPlayed) instanceof TableStateMessage);
				assertTrue(sentMessages.get(53 + 11 * numberOfTimesPlayed) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
				assertTrue(sentMessages.get(54 + 11 * numberOfTimesPlayed) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
				assertTrue(sentMessages.get(55 + 11 * numberOfTimesPlayed) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
				assertTrue(sentMessages.get(56 + 11 * numberOfTimesPlayed) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
				numberOfTimesPlayed += 1;
			}
			userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidPlayCharacterCard, 0, null, true, 0, 0, 0, cardIndex, new CharacterCardNetworkParamSet(null, null, false, 1, -1, -1, null)));
			NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
			assertEquals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidPlayCharacterCard, false, "Invalid move: the Character Card cannot be used anymore (reached max use limit)"), sentMessages.get(57 + 11 * (numberOfTimesPlayed - 1)));
		}
	}

	/**
	 * Tests a move without a {@link it.polimi.ingsw.server.model.student.Student Student}
	 */
	@RepeatedTest(10)
	void testStudentMovementWithoutStudent() {
		testRound();
		PlayerStateMessage fraState = (PlayerStateMessage) sentMessages.get(27);
		Student studentToMove = null;
		for (Student student: Student.values()) {
			if (fraState.getBoard().getEntrance().getCount(student) == 0) {
				studentToMove = student;
				break;
			}
		}
		if (studentToMove != null) {
			HashMap<String, Object> userInfo = new HashMap<>();
			userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidMoveStudent, 0, studentToMove, false, 0, 0, 0, 0, null));
			NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
			assertEquals(new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidMoveStudent, false, "Invalid move: the movement of the selected Student is not valid"), sentMessages.get(35));
		}
	}

	/**
	 * Test the case of an empty {@link it.polimi.ingsw.server.model.student.Cloud Cloud} chosen
	 */
	@Test
	void testEmptyCloudPick() {
		testRound();
		//Simulate Ale's turn
		HashMap<String, Object> userInfo = new HashMap<>();
		int i;
		for (i = 0; i < 3; i++) {
			PlayerStateMessage fraState = (PlayerStateMessage) sentMessages.get(27 + 11 * i);
			Student studentToMove = null;
			for (Student student: Student.values()) {
				if (fraState.getBoard().getEntrance().getCount(student) > 0) {
					studentToMove = student;
					break;
				}
			}
			userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidMoveStudent, 0, studentToMove, false, 0, 0, 0, 0, null));
			NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
			assertEquals(sentMessages.get(35 + 11 * i), new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidMoveStudent, true, ""));
			assertTrue(sentMessages.get(36 + 11 * i) instanceof TableStateMessage);
			assertTrue(sentMessages.get(37 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(38 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(39 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
			assertTrue(sentMessages.get(40 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == (i < 2 ? MatchPhase.ActionPhaseStepOne : MatchPhase.ActionPhaseStepTwo));
			assertTrue(sentMessages.get(41 + 11 * i) instanceof TableStateMessage);
			assertTrue(sentMessages.get(42 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(43 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(44 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
			assertTrue(sentMessages.get(45 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == (i < 2 ? MatchPhase.ActionPhaseStepOne : MatchPhase.ActionPhaseStepTwo));
		}
		
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidMoveMNBySteps, 0, null, false, 0, 1, 0, 0, null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(sentMessages.get(35 + 11 * i), new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidMoveMNBySteps, true, ""));
		assertTrue(sentMessages.get(36 + 11 * i) instanceof TableStateMessage);
		assertTrue(sentMessages.get(37 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(38 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
		assertTrue(sentMessages.get(39 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
		assertTrue(sentMessages.get(40 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepThree);
		assertTrue(sentMessages.get(41 + 11 * i) instanceof TableStateMessage);
		assertTrue(sentMessages.get(42 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
		assertTrue(sentMessages.get(43 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(44 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
		assertTrue(sentMessages.get(45 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepThree);
		i += 1;
		
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidChooseCloudIsland, 0, null, false, 0, 1, 0, 0, null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(sentMessages.get(35 + 11 * i), new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidChooseCloudIsland, true, ""));
		assertTrue(sentMessages.get(36 + 11 * i) instanceof TableStateMessage);
		assertTrue(sentMessages.get(37 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(38 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
		assertTrue(sentMessages.get(39 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
		assertTrue(sentMessages.get(40 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
		assertTrue(sentMessages.get(41 + 11 * i) instanceof TableStateMessage);
		assertTrue(sentMessages.get(42 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
		assertTrue(sentMessages.get(43 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(44 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
		assertTrue(sentMessages.get(45 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
		i += 1;
		
		//Repeat for Fra
		for (int j = 0; j < 3; j++, i++) {
			PlayerStateMessage fraState = (PlayerStateMessage) sentMessages.get(26 + 11 * i);
			Student studentToMove = null;
			for (Student student: Student.values()) {
				if (fraState.getBoard().getEntrance().getCount(student) > 0) {
					studentToMove = student;
					break;
				}
			}
			userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Fra", PlayerActionMessage.ActionType.DidMoveStudent, 0, studentToMove, false, 0, 0, 0, 0, null));
			NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
			assertEquals(sentMessages.get(35 + 11 * i), new PlayerActionResponse("Fra", PlayerActionMessage.ActionType.DidMoveStudent, true, ""));
			assertTrue(sentMessages.get(36 + 11 * i) instanceof TableStateMessage);
			assertTrue(sentMessages.get(37 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(38 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
			assertTrue(sentMessages.get(39 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
			assertTrue(sentMessages.get(40 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == (j < 2 ? MatchPhase.ActionPhaseStepOne : MatchPhase.ActionPhaseStepTwo));
			assertTrue(sentMessages.get(41 + 11 * i) instanceof TableStateMessage);
			assertTrue(sentMessages.get(42 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
			assertTrue(sentMessages.get(43 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(44 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
			assertTrue(sentMessages.get(45 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == (j < 2 ? MatchPhase.ActionPhaseStepOne : MatchPhase.ActionPhaseStepTwo));
		}
		
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Fra", PlayerActionMessage.ActionType.DidMoveMNBySteps, 0, null, false, 0, 1, 0, 0, null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(sentMessages.get(35 + 11 * i), new PlayerActionResponse("Fra", PlayerActionMessage.ActionType.DidMoveMNBySteps, true, ""));
		assertTrue(sentMessages.get(36 + 11 * i) instanceof TableStateMessage);
		assertTrue(sentMessages.get(37 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2);
		assertTrue(sentMessages.get(38 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
		assertTrue(sentMessages.get(39 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
		assertTrue(sentMessages.get(40 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepThree);
		assertTrue(sentMessages.get(41 + 11 * i) instanceof TableStateMessage);
		assertTrue(sentMessages.get(42 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
		assertTrue(sentMessages.get(43 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2);
		assertTrue(sentMessages.get(44 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
		assertTrue(sentMessages.get(45 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepThree);
		i += 1;
		
		//Cause the error
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Fra", PlayerActionMessage.ActionType.DidChooseCloudIsland, 0, null, false, 0, 1, 0, 0, null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(new PlayerActionResponse("Fra", PlayerActionMessage.ActionType.DidChooseCloudIsland, false, "Invalid move: the Cloud you chose is empty. This is not allowed, unless the Bag is also empty"), sentMessages.get(35 + 11 * i));
	}

	/**
	 * Test the second plan phase
	 */
	@RepeatedTest(10)
	void testSecondPlanPhase() {
		testRound();
		//Simulate Ale's turn
		HashMap<String, Object> userInfo = new HashMap<>();
		int i;
		for (i = 0; i < 3; i++) {
			PlayerStateMessage fraState = (PlayerStateMessage) sentMessages.get(27 + 11 * i);
			Student studentToMove = null;
			for (Student student: Student.values()) {
				if (fraState.getBoard().getEntrance().getCount(student) > 0) {
					studentToMove = student;
					break;
				}
			}
			userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidMoveStudent, 0, studentToMove, false, 0, 0, 0, 0, null));
			NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
			assertEquals(sentMessages.get(35 + 11 * i), new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidMoveStudent, true, ""));
			assertTrue(sentMessages.get(36 + 11 * i) instanceof TableStateMessage);
			assertTrue(sentMessages.get(37 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(38 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(39 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
			assertTrue(sentMessages.get(40 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == (i < 2 ? MatchPhase.ActionPhaseStepOne : MatchPhase.ActionPhaseStepTwo));
			assertTrue(sentMessages.get(41 + 11 * i) instanceof TableStateMessage);
			assertTrue(sentMessages.get(42 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(43 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(44 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
			assertTrue(sentMessages.get(45 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == (i < 2 ? MatchPhase.ActionPhaseStepOne : MatchPhase.ActionPhaseStepTwo));
		}
		
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidMoveMNBySteps, 0, null, false, 0, 1, 0, 0, null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(sentMessages.get(35 + 11 * i), new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidMoveMNBySteps, true, ""));
		assertTrue(sentMessages.get(36 + 11 * i) instanceof TableStateMessage);
		assertTrue(sentMessages.get(37 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(38 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
		assertTrue(sentMessages.get(39 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
		assertTrue(sentMessages.get(40 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepThree);
		assertTrue(sentMessages.get(41 + 11 * i) instanceof TableStateMessage);
		assertTrue(sentMessages.get(42 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
		assertTrue(sentMessages.get(43 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(44 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
		assertTrue(sentMessages.get(45 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepThree);
		i += 1;
		
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Ale", PlayerActionMessage.ActionType.DidChooseCloudIsland, 0, null, false, 0, 1, 0, 0, null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(sentMessages.get(35 + 11 * i), new PlayerActionResponse("Ale", PlayerActionMessage.ActionType.DidChooseCloudIsland, true, ""));
		assertTrue(sentMessages.get(36 + 11 * i) instanceof TableStateMessage);
		assertTrue(sentMessages.get(37 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(38 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
		assertTrue(sentMessages.get(39 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
		assertTrue(sentMessages.get(40 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
		assertTrue(sentMessages.get(41 + 11 * i) instanceof TableStateMessage);
		assertTrue(sentMessages.get(42 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
		assertTrue(sentMessages.get(43 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
		assertTrue(sentMessages.get(44 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
		assertTrue(sentMessages.get(45 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepOne);
		i += 1;
		
		//Repeat for Fra
		for (int j = 0; j < 3; j++, i++) {
			PlayerStateMessage fraState = (PlayerStateMessage) sentMessages.get(26 + 11 * i);
			Student studentToMove = null;
			for (Student student: Student.values()) {
				if (fraState.getBoard().getEntrance().getCount(student) > 0) {
					studentToMove = student;
					break;
				}
			}
			userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Fra", PlayerActionMessage.ActionType.DidMoveStudent, 0, studentToMove, false, 0, 0, 0, 0, null));
			NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
			assertEquals(sentMessages.get(35 + 11 * i), new PlayerActionResponse("Fra", PlayerActionMessage.ActionType.DidMoveStudent, true, ""));
			assertTrue(sentMessages.get(36 + 11 * i) instanceof TableStateMessage);
			assertTrue(sentMessages.get(37 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(38 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
			assertTrue(sentMessages.get(39 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
			assertTrue(sentMessages.get(40 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == (j < 2 ? MatchPhase.ActionPhaseStepOne : MatchPhase.ActionPhaseStepTwo));
			assertTrue(sentMessages.get(41 + 11 * i) instanceof TableStateMessage);
			assertTrue(sentMessages.get(42 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
			assertTrue(sentMessages.get(43 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2 && message.getBoard().getAvailableTowerCount() == 8);
			assertTrue(sentMessages.get(44 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
			assertTrue(sentMessages.get(45 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == (j < 2 ? MatchPhase.ActionPhaseStepOne : MatchPhase.ActionPhaseStepTwo));
		}
		
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Fra", PlayerActionMessage.ActionType.DidMoveMNBySteps, 0, null, false, 0, 1, 0, 0, null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(sentMessages.get(35 + 11 * i), new PlayerActionResponse("Fra", PlayerActionMessage.ActionType.DidMoveMNBySteps, true, ""));
		assertTrue(sentMessages.get(36 + 11 * i) instanceof TableStateMessage);
		assertTrue(sentMessages.get(37 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2);
		assertTrue(sentMessages.get(38 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
		assertTrue(sentMessages.get(39 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
		assertTrue(sentMessages.get(40 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepThree);
		assertTrue(sentMessages.get(41 + 11 * i) instanceof TableStateMessage);
		assertTrue(sentMessages.get(42 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
		assertTrue(sentMessages.get(43 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2);
		assertTrue(sentMessages.get(44 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Fra"));
		assertTrue(sentMessages.get(45 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.ActionPhaseStepThree);
		i += 1;
		
		/*
		Ale -> Picked assistant with priority 1
		Fra -> Picked assistant with priority 2
		The default order is: Ale, Fra
		Ale should pick the new assistant first, Fra second
		 */
		userInfo.put(NotificationKeys.IncomingNetworkMessage.getRawValue(), new PlayerActionMessage("Fra", PlayerActionMessage.ActionType.DidChooseCloudIsland, 0, null, false, 0, 1, 1, 0, null));
		NotificationCenter.shared().post(NotificationName.ServerDidReceivePlayerActionMessage, controller, userInfo);
		assertEquals(sentMessages.get(35 + 11 * i), new PlayerActionResponse("Fra", PlayerActionMessage.ActionType.DidChooseCloudIsland, true, ""));
		assertTrue(sentMessages.get(36 + 11 * i) instanceof TableStateMessage);
		assertTrue(sentMessages.get(37 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2);
		assertTrue(sentMessages.get(38 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
		assertTrue(sentMessages.get(39 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
		assertTrue(sentMessages.get(40 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.PlanPhaseStepTwo);
		assertTrue(sentMessages.get(41 + 11 * i) instanceof TableStateMessage);
		assertTrue(sentMessages.get(42 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Ale") && message.getWizard() == Wizard.Wizard1);
		assertTrue(sentMessages.get(43 + 11 * i) instanceof PlayerStateMessage message && message.getNickname().equals("Fra") && message.getWizard() == Wizard.Wizard2);
		assertTrue(sentMessages.get(44 + 11 * i) instanceof ActivePlayerMessage message && message.getActiveNickname().equals("Ale"));
		assertTrue(sentMessages.get(45 + 11 * i) instanceof MatchStateMessage message && message.getCurrentMatchPhase() == MatchPhase.PlanPhaseStepTwo);
	}
}