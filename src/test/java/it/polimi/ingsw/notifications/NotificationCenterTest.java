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

	/**
	 * Tests that all the callback are called correctly
	 */
	@Test
	void testAllCallbacksCalled() {
		NotificationCenter.shared().post(NotificationName.TestNotification, null, null);
		assertEquals(4, callbacksCalled.size());
		for (boolean bool: callbacksCalled) {
			assertTrue(bool);
		}
	}

	/**
	 * Tests that the observed object's callback is called correctly
	 */
	@Test
	void testObservedObjectCallbacksCalled() {
		NotificationCenter.shared().post(NotificationName.TestNotification, "Ale", null);
		assertEquals(2, callbacksCalled.size());
		for (boolean bool: callbacksCalled) {
			assertTrue(bool);
		}
	}

	/**
	 * Tests that the callback is not called when it doesn't supposed to be called
	 */
	@Test
	void testNoCallbacksCalled() {
		NotificationCenter.shared().post(NotificationName.TestNotification, "Fede", null);
		assertTrue(callbacksCalled.isEmpty());
	}

	/**
	 * Test that {@code RemoveObservers} removes the observers
	 */
	@Test
	void testRemoveObservers() {
		NotificationCenter.shared().removeObserver(this);
		NotificationCenter.shared().post(NotificationName.TestNotification, null, null);
		assertTrue(callbacksCalled.isEmpty());
	}

}