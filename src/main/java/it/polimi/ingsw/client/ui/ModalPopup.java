package it.polimi.ingsw.client.ui;

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class ModalPopup extends RescalableAnchorPane {
	
	private final Label title;
	
	public ModalPopup(String titleText) {
		title = new Label(titleText);
		title.setFont(new Font("Avenir", 30));
		title.setTextFill(new Color(0, 0, 0, 1));
		title.setContentDisplay(ContentDisplay.CENTER);
		title.setAlignment(Pos.CENTER);
		title.setWrapText(true);
		AnchorPane.setTopAnchor(title, 0.0);
		AnchorPane.setLeftAnchor(title, 0.0);
		AnchorPane.setRightAnchor(title, 0.0);
		getChildren().add(title);
	}
	
	@Override
	public void rescale(double scale) {
		setPrefSize(400 * scale, 480 * scale);
		setLayoutX((GUI.getWindowWidth() - (400 * scale * 1.2)) * 0.5);
		setLayoutY((GUI.getWindowHeight() - (480 * scale * 1.2)) * 0.5);
		title.setFont(new Font("Avenir", 30 * scale));
		title.setPrefHeight(35 * scale);
		setStyle("-fx-background-color: #fafafa;\n-fx-background-radius: " + 20 * scale);
		rescaleChildren(scale);
	}
	
	protected abstract void rescaleChildren(double scale);
	
	public static double getUnscaledWidth() {
		return 400;
	}
	
	public static double getUnscaledHeight() {
		return 480;
	}
}
