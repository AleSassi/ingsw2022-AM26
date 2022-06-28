package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.server.model.assistants.AssistantCard;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class AssistantCardPickerView extends ModalPopup {
	
	private final AssistantPickerPane assistantPickerPane;
	
	public AssistantCardPickerView(AssistantCard[] availableAssistantCards) {
		super("Choose your Assistant");
		assistantPickerPane = new AssistantPickerPane(availableAssistantCards);
		getChildren().add(assistantPickerPane);
		super.rescale(1);
	}
	
	@Override
	public void rescaleChildren(double scale) {
		assistantPickerPane.setLayoutX(10 * scale);
		assistantPickerPane.setLayoutY(50 * scale);
	}
}
