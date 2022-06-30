package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.ui.rescale.RescalableAnchorPane;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Class {@code ModalPopup} represent a support {@code AnchorPane}
 */
public abstract class ModalPopup extends RescalableAnchorPane {
	
	private final Label title;

	/**
	 * Constructor creates this {@code ModalPopup}
	 * @param titleText (type String) title for the label
	 */
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

	/**
	 * Rescale his children
	 * @param scale (type double) scale value
	 */
	protected abstract void rescaleChildren(double scale);

	/**
	 * Gets the default Width
	 * @return (type double) returns the default Width
	 */
	@Override
	public double getUnscaledWidth() {
		return 400;
	}

	/**
	 * Gets the default Height
	 * @return (type double) returns the default Height
	 */
	@Override
	public double getUnscaledHeight() {
		return 480;
	}
}
