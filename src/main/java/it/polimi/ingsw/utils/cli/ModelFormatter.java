package it.polimi.ingsw.utils.cli;

import it.polimi.ingsw.server.model.Professor;
import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.characters.CharacterCardBean;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;
import it.polimi.ingsw.server.model.student.StudentHost;

/**
 * A utility class for formatting model objects in teh CLI
 */
public class ModelFormatter {
	
	/**
	 * Gets the color associated with a Professor
	 * @param professor The professor to find the color of
	 * @return The ANSI color of the professor
	 */
	public static ANSIColors getProfessorColor(Professor professor) {
		switch (professor) {
			case YellowElf -> {
				return ANSIColors.Yellow;
			}
			case BlueUnicorn -> {
				return ANSIColors.Blue;
			}
			case GreenFrog -> {
				return ANSIColors.Green;
			}
			case RedDragon -> {
				return ANSIColors.Red;
			}
			case PinkFair -> {
				return ANSIColors.Pink;
			}
		}
		return ANSIColors.Red;
	}
	
	/**
	 * Builds a formatted string for an assistant card
	 * @param card The assistant card to format
	 * @return The formatted string for an assistant card
	 */
	public static String formatStringForAssistantCard(AssistantCard card) {
		return StringFormatter.formatWithColor(card.toString(), ANSIColors.Unknown) +
				" (priority: " + card.getPriorityNumber() +
				", motherNatureSteps: " + card.getMotherNatureSteps() +
				')';
	}
	
	/**
	 * Builds a formatted string for the character card
	 * @param bean The character card
	 * @return the formatted string builder for the character card
	 */
	public static StringBuilder formatStringForCharacterCardBean(CharacterCardBean bean) {
		StringBuilder formattedString = new StringBuilder();
		formattedString.append("\t\tCharacter: ").append(bean.getCharacter().toString());
		formattedString.append("\n\t\tPrice: ").append(bean.getTotalPrice());
		if (bean.getExcludedStudent() != null) {
			formattedString.append("\n\t\tExcluded Student: ").append(bean.getExcludedStudent());
		}
		if (bean.getMemorizedModifier() != -1) {
			formattedString.append("\n\t\tModifier: ").append(bean.getMemorizedModifier());
		}
		if (bean.getAvailableStopCards() != -1) {
			formattedString.append("\n\t\tAvailable Stop Cards: ").append(bean.getAvailableStopCards());
		}
		if (bean.getHostedStudents() != null) {
			formattedString.append("\n\t\tHosted Students: ").append(formatStringForStudentCollection(bean.getHostedStudents()));
		}
		return formattedString;
	}
	
	/**
	 * Builds a formatted string for the student host
	 * @param collection The student host
	 * @return the formatted string builder for the student host
	 */
	public static StringBuilder formatStringForStudentHost(StudentHost collection) {
		//We need to duplicate in order to avoid mixing model code with view code
		StringBuilder formattedString = new StringBuilder("[");
		boolean isFirst = true;
		for (Student student: Student.values()) {
			if (collection.getCount(student) > 0) {
				if (!isFirst) {
					formattedString.append(", ");
				}
				formattedString.append(StringFormatter.formatWithColor(student.toString(), ModelFormatter.getProfessorColor(student.getAssociatedProfessor()))).append(": ").append(collection.getCount(student));
				isFirst = false;
			}
		}
		formattedString.append("]");
		return formattedString;
	}
	
	/**
	 * Builds a formatted string for the student collection
	 * @param collection The student collection
	 * @return the formatted string builder for the student collection
	 */
	public static StringBuilder formatStringForStudentCollection(StudentCollection collection) {
		StringBuilder formattedString = new StringBuilder("[");
		boolean isFirst = true;
		for (Student student: Student.values()) {
			if (collection.getCount(student) > 0) {
				if (!isFirst) {
					formattedString.append(", ");
				}
				formattedString.append(StringFormatter.formatWithColor(student.toString(), ModelFormatter.getProfessorColor(student.getAssociatedProfessor()))).append(": ").append(collection.getCount(student));
				isFirst = false;
			}
		}
		formattedString.append("]");
		return formattedString;
	}
	
}
