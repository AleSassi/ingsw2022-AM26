package it.polimi.ingsw.utils.cli;

import it.polimi.ingsw.server.model.assistants.AssistantCard;
import it.polimi.ingsw.server.model.characters.CharacterCardBean;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;
import it.polimi.ingsw.server.model.student.StudentHost;

public class ModelFormatter {
	
	public static String formatStringForAssistantCard(AssistantCard card) {
		return StringFormatter.formatWithColor(card.toString(), ANSIColors.Unknown) +
				" (priority: " + card.getPriorityNumber() +
				", motherNatureSteps: " + card.getMotherNatureSteps() +
				')';
	}
	
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
	
	public static StringBuilder formatStringForStudentHost(StudentHost collection) {
		//We need to duplicate in order to avoid mixing model code with view code
		StringBuilder formattedString = new StringBuilder("[");
		boolean isFirst = true;
		for (Student student: Student.values()) {
			if (collection.getCount(student) > 0) {
				if (!isFirst) {
					formattedString.append(", ");
				}
				formattedString.append(StringFormatter.formatWithColor(student.toString(), student.getAssociatedProfessor().getProfessorColor())).append(": ").append(collection.getCount(student));
				isFirst = false;
			}
		}
		formattedString.append("]");
		return formattedString;
	}
	
	public static StringBuilder formatStringForStudentCollection(StudentCollection collection) {
		StringBuilder formattedString = new StringBuilder("[");
		boolean isFirst = true;
		for (Student student: Student.values()) {
			if (collection.getCount(student) > 0) {
				if (!isFirst) {
					formattedString.append(", ");
				}
				formattedString.append(StringFormatter.formatWithColor(student.toString(), student.getAssociatedProfessor().getProfessorColor())).append(": ").append(collection.getCount(student));
				isFirst = false;
			}
		}
		formattedString.append("]");
		return formattedString;
	}
	
}
