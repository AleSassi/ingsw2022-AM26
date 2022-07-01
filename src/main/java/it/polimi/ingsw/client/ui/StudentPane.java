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

/**
 * Class {@code StudentPane} represent the {@link it.polimi.ingsw.server.model.student.Student Student} tile, with optional clickable behavior
 */
public class StudentPane extends AnchorPane {
	
	private final Student student;
	private EventHandler<MouseEvent> clickEventHandler;

	/**
	 * Constructor creates the {@code AnchorPane} which displays a Student tile
	 * @param student (type Student) the {@link it.polimi.ingsw.server.model.student.Student Student} type to display
	 */
	public StudentPane(Student student) {
		super();
		this.student = student;
		GUIUtils.setStyleWithBackgroundImage(this, "images/students/" + student.getColor() + ".png");
		setStyle(getStyle() + ";\n-fx-background-color: white;\n-fx-border-radius: 100px;\n-fx-background-radius: 100px");
	}

	/**
	 * Configures the {@link it.polimi.ingsw.utils.ui.StudentDropTarget StudentDropTargets} of this {@code StudentPane} and adds the click event handlers
	 * @param validDropTargets (type StudentDropTarget[]) The list of drop targets where the Student can be moved
	 */
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
