package it.polimi.ingsw.notifications;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {
	
	private Notification notification;
	
	@BeforeEach
	void initNotification() {
		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put("winningPlayerNickname", "Ale");
		userInfo.put("aRandomKey", 990786);
		notification = new Notification(NotificationName.PlayerVictory, userInfo);
	}
	
	@Test
	void testGetName() {
		assertEquals(NotificationName.PlayerVictory, notification.getName());
	}
	
	@Test
	void testGetUserInfo() {
		HashMap<String, Object> userInfo = notification.getUserInfo();
		assertEquals("Ale", userInfo.get("winningPlayerNickname"));
		assertEquals(990786, userInfo.get("aRandomKey"));
	}
}