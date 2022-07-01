package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.server.model.student.Student;

/**
 * Interface {@code StudentColorPickerHandler} to handle callbacks for picking Students form the modal picker
 */
@FunctionalInterface
public interface StudentColorPickerHandler {
	
	/**
	 * Callback function invoked when a Student is picked
	 * @param pickedStudent The Student picked in the modal popup
	 */
	void handle(Student pickedStudent);
	
}
