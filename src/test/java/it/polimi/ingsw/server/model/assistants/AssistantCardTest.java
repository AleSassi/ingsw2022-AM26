package it.polimi.ingsw.server.model.assistants;

import it.polimi.ingsw.server.model.assistants.AssistantCard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests assistant cards
 * @see AssistantCard
 */
class AssistantCardTest {
	
	/**
	 * create variables with all values of {@link it.polimi.ingsw.server.model.assistants.AssistantCard assistantcard}, and for each one verify return the correct Priority number
	 */
	@Test
	void getPriorityNumber() {
		AssistantCard class1 = AssistantCard.TURTLE;
		AssistantCard class2 = AssistantCard.ELEPHANT;
		AssistantCard class3 = AssistantCard.DOG;
		AssistantCard class4 = AssistantCard.OCTOPUS;
		AssistantCard class5 = AssistantCard.SNAKE;
		AssistantCard class6 = AssistantCard.FOX;
		AssistantCard class7 = AssistantCard.EAGLE;
		AssistantCard class8 = AssistantCard.CAT;
		AssistantCard class9 = AssistantCard.PEAFOWL;
		AssistantCard class10 = AssistantCard.LION;
		assertEquals(class1.getPriorityNumber(), 1);
		assertEquals(class2.getPriorityNumber(), 2);
		assertEquals(class3.getPriorityNumber(), 3);
		assertEquals(class4.getPriorityNumber(), 4);
		assertEquals(class5.getPriorityNumber(), 5);
		assertEquals(class6.getPriorityNumber(), 6);
		assertEquals(class7.getPriorityNumber(), 7);
		assertEquals(class8.getPriorityNumber(), 8);
		assertEquals(class9.getPriorityNumber(), 9);
		assertEquals(class10.getPriorityNumber(), 10);
	}
	/**
	 create variables with all values o f{@link it.polimi.ingsw.server.model.assistants.AssistantCard assistantcard}, and for each one verify return the correct Mother Nature Step
	 */
	@Test
	void getMotherNatureSteps() {
		AssistantCard class1 = AssistantCard.TURTLE;
		AssistantCard class2 = AssistantCard.ELEPHANT;
		AssistantCard class3 = AssistantCard.DOG;
		AssistantCard class4 = AssistantCard.OCTOPUS;
		AssistantCard class5 = AssistantCard.SNAKE;
		AssistantCard class6 = AssistantCard.FOX;
		AssistantCard class7 = AssistantCard.EAGLE;
		AssistantCard class8 = AssistantCard.CAT;
		AssistantCard class9 = AssistantCard.PEAFOWL;
		AssistantCard class10 = AssistantCard.LION;
		assertEquals(class1.getMotherNatureSteps(), 1);
		assertEquals(class2.getMotherNatureSteps(), 1);
		assertEquals(class3.getMotherNatureSteps(), 2);
		assertEquals(class4.getMotherNatureSteps(), 2);
		assertEquals(class5.getMotherNatureSteps(), 3);
		assertEquals(class6.getMotherNatureSteps(), 3);
		assertEquals(class7.getMotherNatureSteps(), 4);
		assertEquals(class8.getMotherNatureSteps(), 4);
		assertEquals(class9.getMotherNatureSteps(), 5);
		assertEquals(class10.getMotherNatureSteps(), 5);
	}
	
	
}