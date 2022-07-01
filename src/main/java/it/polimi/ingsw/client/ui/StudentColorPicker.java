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
 * Class {@code StudentColorPicker} represent the {@link it.polimi.ingsw.server.model.student.Student Student} picker for the {@link it.polimi.ingsw.server.model.characters.CharacterCard CharacterCard}
 */
public class StudentColorPicker extends ModalPopup {
	
	private StudentColorPickerHandler completionHandler;
	private final GridPane studentGridPane = new GridPane();
	private final List<AnchorPane> studentPanes = new ArrayList<>();

	//region Resize variable
	private final double studentGridPaneX = 50;
	private final double studentGridHgap = 30;
	private final double studentGridVgap = 15;
	private final double studentGridRowConstraintsA = 30;
	private final double studentGridColumnConstraintsA = 20;
	private final double studentGridRowConstraintsB = 80;
	private final double studentGridColumnConstraintsB = 80;
	//endregion

	
	/**
	 * Constructs a Color Picker with a title string
	 * @param titleString The title string
	 */
	public StudentColorPicker(String titleString) {
		super(titleString);
		placeStudents();
		super.rescale(getCurrentScaleValue());
	}

	/**
	 * Places the {@link it.polimi.ingsw.client.ui.StudentPane StudentPane}s on this {@code StudentColorPicker} in a grid, so that the user can pick one of them
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
	 * Sets the {@code StudentColorPickerHandler} to be invoked when the user clicks on a student
	 * @param completionHandler (type StudentColorPickerHandler) The handler t invoke when the user clicks on a student
	 */
	public void setCompletionHandler(StudentColorPickerHandler completionHandler) {
		this.completionHandler = completionHandler;
	}



	@Override
	public void rescaleChildren(double scale) {
		studentGridPane.setLayoutY(studentGridPaneX * scale);
		studentGridPane.setPrefSize(getUnscaledWidth() * scale, getUnscaledHeight() * scale);
		studentGridPane.setHgap(studentGridHgap * scale);
		studentGridPane.setVgap(studentGridVgap * scale);
		studentGridPane.getRowConstraints().removeAll(studentGridPane.getRowConstraints());
		studentGridPane.getColumnConstraints().removeAll(studentGridPane.getColumnConstraints());
		studentGridPane.getRowConstraints().add(new RowConstraints((studentGridRowConstraintsA * scale)));
		studentGridPane.getColumnConstraints().add(new ColumnConstraints((studentGridColumnConstraintsA * scale)));
		for (int i = 0; i < 2; i++) {
			RowConstraints row = new RowConstraints(studentGridRowConstraintsB * scale);
			studentGridPane.getRowConstraints().add(row);
		}
		for (int i = 0; i < 3; i++) {
			ColumnConstraints col = new ColumnConstraints(studentGridColumnConstraintsB * scale);
			studentGridPane.getColumnConstraints().add(col);
		}
	}
}
