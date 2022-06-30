package it.polimi.ingsw.notifications;

import it.polimi.ingsw.notifications.NotificationObserver;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.assistants.Wizard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationObserverTest {
	
	private NotificationObserver observer;
	
	@BeforeEach
	void initObserver() {
		assertDoesNotThrow(() -> {
			Player observedPlayer = new Player("Ale", Wizard.Wizard1, Tower.White, 8, 1);
			observer = new NotificationObserver(this, observedPlayer, (notification) -> System.out.println("Hello"));
		});
	}

	/**
	 * Tests that {@code getObservedObject} returns the observed object
	 */
	@Test
	void getObservedObject() {
		assertDoesNotThrow(() -> {
			Player observedPlayer = new Player("Ale", Wizard.Wizard1, Tower.White, 8, 1);
			assertEquals(observedPlayer, observer.getObservedObject());
		});
	}

	/**
	 * Tests that {@code getCallback} returns the callback
	 */
	@Test
	void getCallback() {
		assertNotNull(observer.getCallback());
	}
}