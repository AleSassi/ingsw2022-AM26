package it.polimi.ingsw.client.ui.characters;

import it.polimi.ingsw.server.controller.network.messages.CharacterCardNetworkParamSet;
import it.polimi.ingsw.server.model.characters.CharacterCardBean;
import it.polimi.ingsw.server.model.student.Student;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;

public class CharacterCardMNStepsPane extends CharacterCardPane {
	
	public CharacterCardMNStepsPane(int cardIndex, CharacterCardBean cardBean) {
		super(cardIndex, cardBean);
	}
	
	@Override
	protected void performAdditionalInitializationForCard(int cardIndex, CharacterCardBean cardBean) {
	
	}
	
	@Override
	protected void executeActionOnClick() {
		Platform.runLater(() -> {
			TextInputDialog td = new TextInputDialog("Mother Nature Steps");
			td.setHeaderText("Enter the additional number of steps Mother Nature must move by (between 0 and 2)");
			td.showAndWait().ifPresent((text) -> {
				try {
					int number = Integer.parseInt(text);
					if (number < 0) {
						number = 0;
					} else if (number > 2) {
						number = 2;
					}
					//Send the player action
					CharacterCardNetworkParamSet parameters = new CharacterCardNetworkParamSet(null, null, false, number, -1, number, null);
					closeCardControlLoop(parameters);
				} catch (NumberFormatException e) {
					this.executeActionOnClick();
				}
			});
		});
	}
	
	@Override
	protected void adaptStudentsForControlLoopClosed(Student[] clickedStudents) {
	}
}
