package it.polimi.ingsw.notifications;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class {@code NotificationTest} tests {@link it.polimi.ingsw.notifications.Notification Notification}
 * @see Notification
 */
class NotificationTest {
	
	private Notification notification;
	
	/**
	 * Common test initialization for the notification
	 */
	@BeforeEach
	void initNotification() {
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put("winningPlayerNickname", "Ale");
		userInfo.put("aRandomKey", 990786);
		notification = new Notification(NotificationName.PlayerVictory, userInfo);
	}

	/**
	 * Tests that {@code getName} returns the right nickname
	 */
	@Test
	void testGetName() {
		assertEquals(NotificationName.PlayerVictory, notification.getName());
	}

	/**
	 * Test that {@code getUserInfo} returns the right user info
	 */
	@Test
	void testGetUserInfo() {
		HashMap<String, Object> userInfo = notification.getUserInfo();
		assertEquals("Ale", userInfo.get("winningPlayerNickname"));
		assertEquals(990786, userInfo.get("aRandomKey"));
	}
}