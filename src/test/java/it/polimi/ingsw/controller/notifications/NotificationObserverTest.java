package it.polimi.ingsw.controller.notifications;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.model.assistants.Wizard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationObserverTest {
	
	private NotificationObserver observer;
	
	@BeforeEach
	void initObserver() {
		assertDoesNotThrow(() -> {
			Player observedPlayer = new Player("Ale", Wizard.Wizard1, Tower.White, 8);
			observer = new NotificationObserver(observedPlayer, (notification) -> System.out.println("Hello"));
		});
	}
	
	@Test
	void getObservedObject() {
		assertDoesNotThrow(() -> {
			Player observedPlayer = new Player("Ale", Wizard.Wizard1, Tower.White, 8);
			assertEquals(observedPlayer, observer.getObservedObject());
		});
	}
	
	@Test
	void getCallback() {
		assertNotNull(observer.getCallback());
	}
}