package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.utils.ui.GUIUtils;
import it.polimi.ingsw.utils.ui.StudentDropTarget;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;

public class StudentPane extends AnchorPane {
	
	private final Student student;
	private EventHandler<MouseEvent> clickEventHandler;
	
	public StudentPane(Student student) {
		super();
		this.student = student;
		GUIUtils.setStyleWithBackgroundImage(this, "images/students/" + student.getColor() + ".png");
		setStyle(getStyle() + ";\n-fx-background-color: white;\n-fx-border-radius: 100px;\n-fx-background-radius: 100px");
	}
	
	public void configureClickForDropTargets(StudentDropTarget[] validDropTargets) {
		if (clickEventHandler != null) {
			removeEventHandler(MouseEvent.MOUSE_CLICKED, clickEventHandler);
		}
		if (validDropTargets != null && validDropTargets.length > 0) {
			clickEventHandler = event -> {
				HashMap<String, Object> userInfo = new HashMap<>();
				userInfo.put(NotificationKeys.ClickedStudentColor.getRawValue(), student);
				userInfo.put(NotificationKeys.StudentDropTargets.getRawValue(), validDropTargets);
				// We forward a notification so that the controller class can get the Student from the PlayerMessage
				NotificationCenter.shared().post(NotificationName.JavaFXDidStartMovingStudent, null, userInfo);
				event.consume();
			};
			addEventHandler(MouseEvent.MOUSE_CLICKED, clickEventHandler);
		}
	}
}
