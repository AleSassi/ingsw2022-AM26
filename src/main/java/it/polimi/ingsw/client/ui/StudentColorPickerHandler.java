package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.server.model.student.Student;

/**
 * Interface {@code StudentColorPickerHandler} handles the {@link it.polimi.ingsw.client.ui.StudentPane StudentPane's} click events
 */
@FunctionalInterface
public interface StudentColorPickerHandler {
	
	void handle(Student pickedStudent);
	
}
