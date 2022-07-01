package it.polimi.ingsw.client.ui.characters;

import it.polimi.ingsw.client.ui.StudentColorPicker;
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
import javafx.scene.layout.GridPane;

import java.util.HashMap;

/**
 * Class {@code CharacterCardStudentPickerPane} represent the {@link it.polimi.ingsw.server.model.characters.CharacterCard CharacterCard} that allows the user to pick a student
 */
public class CharacterCardStudentPickerPane extends CharacterCardPane {
	
	private AnchorPane pickedStudentPane;
	private final double layoutX = 40;
	private final double layoutY = 81;
	private final double width = 50;
	private final double height = 50;

	/**
	 * Constructor creates a new {@code CharacterCardStudentPickerPane}
	 * @param cardIndex (type int) {@link it.polimi.ingsw.server.model.characters.CharacterCard CharacterCard's} index in table list
	 * @param cardBean (type CharacterCardBean) The card data
	 */
	public CharacterCardStudentPickerPane(int cardIndex, CharacterCardBean cardBean) {
		super(cardIndex, cardBean);
	}
	
	@Override
	protected void performAdditionalInitializationForCard(int cardIndex, CharacterCardBean cardBean) {
		setupPickedStudentPane(cardBean.getExcludedStudent());
	}

	/**
	 * Sets up the picked {@link it.polimi.ingsw.client.ui.StudentPane StudentPane} for displaying the picked student
	 * @param pickedStudent (type Student) The picked Student
	 */
	private void setupPickedStudentPane(Student pickedStudent) {
		if (pickedStudent != null) {
			if (pickedStudentPane != null) {
				Platform.runLater(() -> {
					getChildren().remove(pickedStudentPane);
					pickedStudentPane = null;
				});
			}
			pickedStudentPane = GUIUtils.createStudentButton(pickedStudent, null);
			Platform.runLater(() -> getChildren().add(1, pickedStudentPane));
			rescale(getCurrentScaleValue());
		}
	}
	
	@Override
	protected void executeActionOnClick() {
		String title;
		if (getCharacter() == Character.Mushroom) {
			title = "Choose the excluded Student";
		} else {
			title = "Choose the stolen Student";
		}
		StudentColorPicker picker = new StudentColorPicker(title);
		picker.setCompletionHandler(pickedStudent -> {
			//Close the Character Card control loop by sending the data
			CharacterCardNetworkParamSet paramSet = new CharacterCardNetworkParamSet(pickedStudent, null, false, -1, -1, -1, null);
			closeCardControlLoop(paramSet);
			getParentController().dismissModalPopup(picker);
		});
		getParentController().showModalPopup(picker);
	}
	
	@Override
	protected void adaptStudentsForControlLoopClosed(Student[] clickedStudents) {
	}
	
	@Override
	protected void didReceiveTableStateNotification(Notification notification) {
		super.didReceiveTableStateNotification(notification);
		if (notification.getUserInfo() != null && notification.getUserInfo().get(NotificationKeys.IncomingNetworkMessage.getRawValue()) instanceof TableStateMessage tableStateMessage) {
			//Update with new state
			CharacterCardBean bean = tableStateMessage.getPlayableCharacterCards().get(getCharacterCardIndexInTableList());
			setupPickedStudentPane(bean.getExcludedStudent());
		}
	}
	
	@Override
	public void rescale(double scale) {
		super.rescale(scale);
		if (pickedStudentPane != null) {
			pickedStudentPane.setLayoutX(layoutX * 0.5 * scale);
			pickedStudentPane.setLayoutY(layoutY * scale);
			pickedStudentPane.setPrefSize(width * scale, height * scale);
		}
	}
}
