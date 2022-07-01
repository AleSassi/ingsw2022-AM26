package it.polimi.ingsw.server.model.assistants;

import it.polimi.ingsw.server.exceptions.model.CharacterCardIncorrectParametersException;
import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;
/**
 * This Class represent the {@code AssistantCard}
 * @author Leonardo Betti
 */
public enum AssistantCard {
	/**
	 * initialize {@code AssistantCard}
	 */
	TURTLE(1, 1),
	ELEPHANT(2, 1),
	DOG(3, 2),
	OCTOPUS(4, 2),
	SNAKE(5, 3),
	FOX(6, 3),
	EAGLE(7, 4),
	CAT(8, 4),
	PEAFOWL(9, 5),
	LION(10, 5);
	private final int priorityNumber;
	private final int motherNatureSteps;

	/**
	 * constructor, set the parameter of the card according to
	 * @param priorityNumber (type int) number of priority of card
	 * @param motherNatureSteps (type int) number of mother nature step
	 */
	AssistantCard(int priorityNumber, int motherNatureSteps) {
		this.priorityNumber = priorityNumber;
		this.motherNatureSteps = motherNatureSteps;
	}
	/**
	 * getter
	 * @return (type int) priorityNumber, number of priority of card
	 */
	public int getPriorityNumber() {
		return priorityNumber;
	}
	/**
	 * getter
	 * @return motherNatureSteps (type int) number of mother nature step
	 */
	public int getMotherNatureSteps() {
		return motherNatureSteps;
	}
}
