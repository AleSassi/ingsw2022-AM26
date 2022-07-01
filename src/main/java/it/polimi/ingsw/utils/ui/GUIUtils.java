package it.polimi.ingsw.utils.ui;

import it.polimi.ingsw.client.ui.GUI;
import it.polimi.ingsw.client.ui.StudentPane;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.model.Tower;
import it.polimi.ingsw.server.model.student.Student;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Objects;

/**
 * A set of utility methods for the GUI
 */
public class GUIUtils {
	
	/**
	 * Creates a StudentPane pre-configured for click actions
	 * @param student The student to create the StudentPane with
	 * @param validDropTargets The valid drop targets for the move action
	 * @return The StudentPane pre-configured for click actions
	 */
	public static AnchorPane createStudentButton(Student student, StudentDropTarget[] validDropTargets) {
		StudentPane studentButton = new StudentPane(student);
		studentButton.configureClickForDropTargets(validDropTargets);
		return studentButton;
	}
	
	/**
	 * Creates a Professor pane
	 * @param student The student associated with the professor
	 * @return The professor pane
	 */
	public static AnchorPane createProfessorButton(Student student) {
		AnchorPane professorPane = createImageViewWithImageNamed("images/professors/" + student.getColor() + ".png");
		professorPane.setStyle(professorPane.getStyle() + ";\n-fx-background-color: white;\n-fx-border-radius: 100px;\n-fx-background-radius: 100px");
		return professorPane;
	}
	
	/**
	 * Creates a Tower pane
	 * @param tower The tower to show
	 * @return The tower pane
	 */
	public static AnchorPane createTowerButton(Tower tower) {
		AnchorPane towerPane = createImageViewWithImageNamed("images/towers/" + tower + ".png");
		towerPane.setStyle(towerPane.getStyle() + ";\n-fx-background-color: #b7b7b7;\n-fx-border-radius: 100px;\n-fx-background-radius: 100px");
		return towerPane;
	}
	
	/**
	 * Creates a pane with a background image
	 * @param imageName The bakground image name
	 * @return The pane with a background image
	 */
	public static AnchorPane createImageViewWithImageNamed(String imageName) {
		AnchorPane img = new AnchorPane();
		setStyleWithBackgroundImage(img, imageName);
		return img;
	}
	
	/**
	 * Sets a background image to a pane
	 * @param destPane The pane to set background image to
	 * @param imageName The name of the image
	 */
	public static void setStyleWithBackgroundImage(AnchorPane destPane, String imageName) {
		destPane.setStyle("-fx-background-image: url(" + getURI(imageName) + ");\n-fx-background-size: 100% 100%");
	}
	
	/**
	 * Finds the URI of an image inn the project
	 * @param resource The resource name
	 * @return The full URI
	 */
	public static String getURI(String resource) {
		try {
			return Objects.requireNonNull(GUI.class.getResource(resource)).toURI().toString();
		} catch (URISyntaxException e) {
			System.out.println("ERROR WHEN BUILDING URI");
			return resource;
		}
	}
	
}
