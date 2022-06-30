package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.CharacterCardNetworkParamSet;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.utils.ui.GUIUtils;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class {@code StudentColorPicker} represent the {@link it.polimi.ingsw.server.model.student.Student Student} picker for the {@link it.polimi.ingsw.server.model.characters.CharacterCard  CharacterCard}
 */
public class StudentColorPicker extends ModalPopup {
	
	private StudentColorPickerHandler completionHandler;
	private final GridPane studentGridPane = new GridPane();
	private final List<AnchorPane> studentPanes = new ArrayList<>();
	
	public StudentColorPicker(String titleString) {
		super(titleString);
		placeStudents();
		super.rescale(getCurrentScaleValue());
	}

	/**
	 * Places the {@link it.polimi.ingsw.client.ui.StudentPane StudentPane} on this {@code StudentColorPicker}
	 */
	private void placeStudents() {
		int row = 1, col = 1;
		for (Student student: Student.values()) {
			AnchorPane studentPane = GUIUtils.createStudentButton(student, null);
			studentPane.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
				event.consume();
				if (completionHandler != null) {
					completionHandler.handle(student);
				}
			});
			GridPane.setRowIndex(studentPane, row);
			GridPane.setColumnIndex(studentPane, col);
			studentPanes.add(studentPane);
			if (col == 3) {
				row += 1;
				col = 1;
			} else {
				col += 1;
			}
		}
		getChildren().add(studentGridPane);
		studentGridPane.getChildren().addAll(studentPanes);
	}

	/**
	 * Sets the {@code EventHandler}
	 * @param completionHandler (type StudentColorPickerHandler)
	 */
	public void setCompletionHandler(StudentColorPickerHandler completionHandler) {
		this.completionHandler = completionHandler;
	}
	
	@Override
	public void rescaleChildren(double scale) {
		studentGridPane.setLayoutY(50 * scale);
		studentGridPane.setPrefSize(ModalPopup.getUnscaledWidth() * scale, ModalPopup.getUnscaledHeight() * scale);
		studentGridPane.setHgap(30 * scale);
		studentGridPane.setVgap(15 * scale);
		studentGridPane.getRowConstraints().removeAll(studentGridPane.getRowConstraints());
		studentGridPane.getColumnConstraints().removeAll(studentGridPane.getColumnConstraints());
		studentGridPane.getRowConstraints().add(new RowConstraints((30 * scale)));
		studentGridPane.getColumnConstraints().add(new ColumnConstraints((20 * scale)));
		for (int i = 0; i < 2; i++) {
			RowConstraints row = new RowConstraints(80 * scale);
			studentGridPane.getRowConstraints().add(row);
		}
		for (int i = 0; i < 3; i++) {
			ColumnConstraints col = new ColumnConstraints(80 * scale);
			studentGridPane.getColumnConstraints().add(col);
		}
	}
}
