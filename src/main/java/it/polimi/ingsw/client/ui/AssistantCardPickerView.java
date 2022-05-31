package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.server.model.assistants.AssistantCard;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class AssistantCardPickerView extends RescalableAnchorPane {
	
	private final AssistantPickerPane assistantPickerPane;
	private final Label title;
	
	public AssistantCardPickerView(AssistantCard[] availableAssistantCards) {
		super();
		assistantPickerPane = new AssistantPickerPane(availableAssistantCards);
		title = new Label("Choose your Assistant");
		title.setFont(new Font("Avenir", 30));
		title.setTextFill(new Color(0, 0, 0, 1));
		title.setContentDisplay(ContentDisplay.CENTER);
		title.setAlignment(Pos.CENTER);
		title.setWrapText(true);
		getChildren().add(title);
		getChildren().add(assistantPickerPane);
		rescale(1);
	}
	
	@Override
	public void rescale(double scale) {
		setPrefSize(400 * scale, 480 * scale);
		setLayoutX((GUI.getWindowWidth() - (400 * scale * 1.2)) * 0.5);
		setLayoutY((GUI.getWindowHeight() - (480 * scale * 1.2)) * 0.5);
		assistantPickerPane.setLayoutX(10 * scale);
		assistantPickerPane.setLayoutY(50 * scale);
		title.setFont(new Font("Avenir", 30 * scale));
		title.setLayoutX(70 * scale);
		title.setLayoutY(10 * scale);
		title.setPrefSize(360 * scale, 35 * scale);
		setStyle("-fx-background-color: #fafafa;\n-fx-background-radius: " + 20 * scale);
	}
}
