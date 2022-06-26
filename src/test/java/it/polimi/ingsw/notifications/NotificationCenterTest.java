package it.polimi.ingsw.notifications;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationCenterTest {
	
	private List<Boolean> callbacksCalled;
	
	@BeforeEach
	void init() {
		callbacksCalled = new ArrayList<>();
		String observedObject = "Ale";
		NotificationCenter.shared().addObserver(this, (notification) -> callbacksCalled.add(true), NotificationName.TestNotification, null);
		NotificationCenter.shared().addObserver(this, (notification) -> callbacksCalled.add(true), NotificationName.TestNotification, null);
		NotificationCenter.shared().addObserver(this, (notification) -> callbacksCalled.add(true), NotificationName.TestNotification, observedObject);
		NotificationCenter.shared().addObserver(this, (notification) -> callbacksCalled.add(true), NotificationName.TestNotification, observedObject);
	}
	
	@Test
	void testAllCallbacksCalled() {
		NotificationCenter.shared().post(NotificationName.TestNotification, null, null);
		assertEquals(4, callbacksCalled.size());
		for (boolean bool: callbacksCalled) {
			assertTrue(bool);
		}
	}
	
	@Test
	void testObservedObjectCallbacksCalled() {
		NotificationCenter.shared().post(NotificationName.TestNotification, "Ale", null);
		assertEquals(2, callbacksCalled.size());
		for (boolean bool: callbacksCalled) {
			assertTrue(bool);
		}
	}
	
	@Test
	void testNoCallbacksCalled() {
		NotificationCenter.shared().post(NotificationName.TestNotification, "Fede", null);
		assertTrue(callbacksCalled.isEmpty());
	}
	
	@Test
	void testRemoveObservers() {
		NotificationCenter.shared().removeObserver(this);
		NotificationCenter.shared().post(NotificationName.TestNotification, null, null);
		assertTrue(callbacksCalled.isEmpty());
	}

}