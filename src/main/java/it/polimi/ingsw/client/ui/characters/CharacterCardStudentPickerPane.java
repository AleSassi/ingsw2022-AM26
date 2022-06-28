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

public class CharacterCardStudentPickerPane extends CharacterCardPane {
	
	private AnchorPane pickedStudentPane;
	
	public CharacterCardStudentPickerPane(int cardIndex, CharacterCardBean cardBean) {
		super(cardIndex, cardBean);
	}
	
	@Override
	protected void performAdditionalInitializationForCard(int cardIndex, CharacterCardBean cardBean) {
		setupPickedStudentPane(cardBean.getExcludedStudent());
	}
	
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
			rescale(1);
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
			pickedStudentPane.setLayoutX((90 - 50) * 0.5 * scale);
			pickedStudentPane.setLayoutY((136 - 50 - 5) * scale);
			pickedStudentPane.setPrefSize(50 * scale, 50 * scale);
		}
	}
}