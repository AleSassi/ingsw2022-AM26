package it.polimi.ingsw.controller.notifications;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationCenterTest {
	
	private List<Boolean> callbacksCalled;
	
	@BeforeEach
	void init() {
		callbacksCalled = new ArrayList<>();
		String observedObject = "Ale";
		NotificationCenter.shared().addObserver((notification) -> callbacksCalled.add(true), NotificationName.PlayerVictory, null);
		NotificationCenter.shared().addObserver((notification) -> callbacksCalled.add(true), NotificationName.PlayerVictory, null);
		NotificationCenter.shared().addObserver((notification) -> callbacksCalled.add(true), NotificationName.PlayerVictory, observedObject);
		NotificationCenter.shared().addObserver((notification) -> callbacksCalled.add(true), NotificationName.PlayerVictory, observedObject);
	}
	
	@Test
	void testAllCallbacksCalled() {
		NotificationCenter.shared().post(NotificationName.PlayerVictory, null, null);
		assertEquals(4, callbacksCalled.size());
		for (boolean bool: callbacksCalled) {
			assertTrue(bool);
		}
	}
	
	@Test
	void testObservedObjectCallbacksCalled() {
		NotificationCenter.shared().post(NotificationName.PlayerVictory, "Ale", null);
		assertEquals(2, callbacksCalled.size());
		for (boolean bool: callbacksCalled) {
			assertTrue(bool);
		}
	}
	
	@Test
	void testNoCallbacksCalled() {
		NotificationCenter.shared().post(NotificationName.PlayerVictory, "Fede", null);
		assertTrue(callbacksCalled.isEmpty());
	}

}