package it.polimi.ingsw.client.ui.characters;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationKeys;
import it.polimi.ingsw.notifications.NotificationName;
import it.polimi.ingsw.server.controller.network.messages.CharacterCardNetworkParamSet;
import it.polimi.ingsw.server.controller.network.messages.TableStateMessage;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.characters.CharacterCardBean;
import it.polimi.ingsw.server.model.student.Student;
import it.polimi.ingsw.server.model.student.StudentCollection;
import it.polimi.ingsw.utils.ui.GUIUtils;
import it.polimi.ingsw.utils.ui.StudentDropTarget;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.awt.desktop.QuitEvent;
import java.util.HashMap;

/**
 * Class {@code CharacterCardStudentHostPane} represent the {@link it.polimi.ingsw.server.model.characters.CharacterCard CharacterCard}s hosting Students
 */
public class CharacterCardStudentHostPane extends CharacterCardGenericPane {
	
	private Student pickedStudentFromCard = null;
	private GridPane studentGrid;
	private final double rowConstraint = 42;
	private final double columnConstraint = 42;
	/**
	 * Constructor creates a new {@code CharacterCardStudentHostPane}
	 * @param cardIndex (type int) {@link it.polimi.ingsw.server.model.characters.CharacterCard CharacterCard's} index in table list
	 * @param cardBean (type CharacterCardBean) The card data
	 */
	public CharacterCardStudentHostPane(int cardIndex, CharacterCardBean cardBean) {
		super(cardIndex, cardBean);
	}
	
	@Override
	protected void performAdditionalInitializationForCard(int cardIndex, CharacterCardBean cardBean) {
		super.performAdditionalInitializationForCard(cardIndex, cardBean);
		buildStudentGrid(cardBean.getCharacter(), cardBean.getHostedStudents());
	}

	/**
	 * Builds the {@code Student grid} depending on the {@link it.polimi.ingsw.server.model.characters.Character Character}
	 * @param character (type Character) The card character
	 * @param hostedStudents (type StudentCollection) The hosted students
	 */
	private void buildStudentGrid(Character character, StudentCollection hostedStudents) {
		if (studentGrid == null) {
			studentGrid = new GridPane(); //Grid pane MUST be a 2x3 grid, to hold a max of 6 students
			AnchorPane.setRightAnchor(studentGrid, 0.0);
			AnchorPane.setTopAnchor(studentGrid, 0.0);
			AnchorPane.setBottomAnchor(studentGrid, 0.0);
			AnchorPane.setLeftAnchor(studentGrid, 0.0);
			Platform.runLater(() -> {
				getChildren().add(studentGrid);
			});
		}
		StudentDropTarget[] admissibleTargets = new StudentDropTarget[0];
		/*switch (character) {
			case Abbot -> admissibleTargets = new StudentDropTarget[] {StudentDropTarget.ToIsland};
			case Circus -> admissibleTargets = new StudentDropTarget[] {StudentDropTarget.ToDiningRoom}; //TODO: Must handle externally since I expect the user to click on a STUDENT IN THE DINING ROOM to perform the swap
			case Queen -> admissibleTargets = new StudentDropTarget[] {StudentDropTarget.ToDiningRoom};
		}*/
		int row = 0, col = 0;
		for (Student student: Student.values()) {
			int studentCount = hostedStudents.getCount(student);
			for (int i = 0; i < studentCount; i++) {
				//Create a new StudentButton and add to the grid
				AnchorPane studentButton = GUIUtils.createStudentButton(student, admissibleTargets);
				studentButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
					event.consume();
					if (!isPurchased()) return;
					
					if (character == Character.Queen) {
						//Immediately send the event
						CharacterCardNetworkParamSet paramSet = new CharacterCardNetworkParamSet(student, null, false, -1, -1, -1, null);
						closeCardControlLoop(paramSet);
					} else {
						HashMap<String, Object> userInfo = new HashMap<>();
						userInfo.put(NotificationKeys.JavaFXPlayedCharacter.getRawValue(), character);
						pickedStudentFromCard = student;
						NotificationCenter.shared().post(NotificationName.JavaFXDidPlayCharacterCard, null, userInfo);
					}
				});
				int finalRow = row;
				int finalCol = col;
				Platform.runLater(() -> {
					GridPane.setRowIndex(studentButton, finalRow);
					GridPane.setColumnIndex(studentButton, finalCol);
					studentGrid.getChildren().add(studentButton);
				});
				if (col == 1) {
					row += 1;
					col = 0;
				} else {
					col += 1;
				}
			}
		}
	}
	
	@Override
	protected int getFaderZPosition() {
		return 1;
	}
	
	@Override
	protected void adaptStudentsForControlLoopClosed(Student[] clickedStudents) {
		if (clickedStudents[0] == null) {
			clickedStudents[0] = pickedStudentFromCard;
		}
		if (clickedStudents[1] == null) {
			clickedStudents[1] = pickedStudentFromCard;
		}
	}
	
	@Override
	protected void didReceiveTableStateNotification(Notification notification) {
		super.didReceiveTableStateNotification(notification);
		if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof TableStateMessage tableStateMessage) {
			//Update with new state
			Platform.runLater(() -> {
				studentGrid.getChildren().removeAll(studentGrid.getChildren());
			});
			CharacterCardBean bean = tableStateMessage.getPlayableCharacterCards().get(getCharacterCardIndexInTableList());
			buildStudentGrid(getCharacter(), bean.getHostedStudents());
		}
	}
	
	@Override
	public void rescale(double scale) {
		super.rescale(scale);
		studentGrid.getRowConstraints().removeAll(studentGrid.getRowConstraints());
		studentGrid.getColumnConstraints().removeAll(studentGrid.getColumnConstraints());
		for (int i = 0; i < 3; i++) {
			RowConstraints row = new RowConstraints(rowConstraint * scale);
			studentGrid.getRowConstraints().add(row);
		}
		for (int i = 0; i < 2; i++) {
			ColumnConstraints col = new ColumnConstraints(columnConstraint * scale);
			studentGrid.getColumnConstraints().add(col);
		}
	}
}
