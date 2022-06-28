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

public class GUIUtils {
	
	public static AnchorPane createStudentButton(Student student, StudentDropTarget[] validDropTargets) {
		StudentPane studentButton = new StudentPane(student);
		studentButton.configureClickForDropTargets(validDropTargets);
		return studentButton;
	}
	
	public static AnchorPane createProfessorButton(Student student) {
		AnchorPane professorPane = createImageViewWithImageNamed("images/professors/" + student.getColor() + ".png");
		professorPane.setStyle(professorPane.getStyle() + ";\n-fx-background-color: white;\n-fx-border-radius: 100px;\n-fx-background-radius: 100px");
		return professorPane;
	}
	
	public static AnchorPane createTowerButton(Tower tower) {
		AnchorPane towerPane = createImageViewWithImageNamed("images/towers/" + tower + ".png");
		towerPane.setStyle(towerPane.getStyle() + ";\n-fx-background-color: #b7b7b7;\n-fx-border-radius: 100px;\n-fx-background-radius: 100px");
		return towerPane;
	}
	
	public static AnchorPane createImageViewWithImageNamed(String imageName) {
		AnchorPane img = new AnchorPane();
		setStyleWithBackgroundImage(img, imageName);
		return img;
	}
	
	public static void setStyleWithBackgroundImage(AnchorPane destPane, String imageName) {
		destPane.setStyle("-fx-background-image: url(" + getURI(imageName) + ");\n-fx-background-size: 100% 100%");
	}
	
	public static String getURI(String resource) {
		try {
			return Objects.requireNonNull(GUI.class.getResource(resource)).toURI().toString();
		} catch (URISyntaxException e) {
			System.out.println("ERROR WHEN BUILDING URI");
			return resource;
		}
	}
	
}
