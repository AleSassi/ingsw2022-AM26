package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.server.model.student.Student;

@FunctionalInterface
public interface StudentColorPickerHandler {
	
	void handle(Student pickedStudent);
	
}
