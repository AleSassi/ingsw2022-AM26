package it.polimi.ingsw.notifications;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The tests for the NotificationCenter class
 * @see NotificationCenter
 */
class NotificationCenterTest {
	
	private List<Boolean> callbacksCalled;
	
	/**
	 * Common test initialization, so that when the notification is called it fires up to 4 callbacks depending on the Post params
	 */
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
	 * Tests that all the callbacks are called correctly
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
	 * Tests that the callback is not called when it isn't supposed to be called (no observed object found)
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