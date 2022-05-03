package it.polimi.ingsw.server.model.assistants;

import it.polimi.ingsw.utils.cli.ANSIColors;
import it.polimi.ingsw.utils.cli.StringFormatter;

public enum AssistantCard {
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
	
	
	AssistantCard(int priorityNumber, int motherNatureSteps) {
		this.priorityNumber = priorityNumber;
		this.motherNatureSteps = motherNatureSteps;
	}
	
	public int getPriorityNumber() {
		return priorityNumber;
	}
	
	public int getMotherNatureSteps() {
		return motherNatureSteps;
	}
	
	@Override
	public String toString() {
		return StringFormatter.formatWithColor(super.toString(), ANSIColors.Unknown) +
				" (priority: " + priorityNumber +
				", motherNatureSteps: " + motherNatureSteps +
				')';
	}
}
