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
		AnchorPane.setTopAnchor(title, 10.0);
		AnchorPane.setLeftAnchor(title, 20.0);
		AnchorPane.setRightAnchor(title, 20.0);
		getChildren().add(title);
		getChildren().add(assistantPickerPane);
		rescale(1);
		setStyle("-fx-background-color: #fafafa;\n-fx-background-radius: 20");
	}
	
	@Override
	public void rescale(double scale) {
		//setPrefSize(520 * scale, 750 * scale);
		assistantPickerPane.setLayoutX(10 * scale);
		assistantPickerPane.setLayoutY(50 * scale);
		title.setFont(new Font("Avenir", 30 * scale));
	}
}
