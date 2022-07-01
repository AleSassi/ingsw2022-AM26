package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.ui.rescale.RescalableAnchorPane;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Class {@code ModalPopup} represent an {@code AnchorPane} that can be presented modally by the controller
 */
public abstract class ModalPopup extends RescalableAnchorPane {
	
	private final Label title;
	private final double titleFont = 30;
	private final double width = 400;
	private final double height = 480;
	private final double layoutX = 400;
	private final double layoutY = 480;
	private final double titleHeight = 35;

	/**
	 * Constructor creates this {@code ModalPopup} with a title label
	 * @param titleText (type String) title for the label
	 */
	public ModalPopup(String titleText) {
		title = new Label(titleText);
		title.setFont(new Font("Avenir", titleFont));
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
		setPrefSize(width * scale, height * scale);
		setLayoutX((GUI.getWindowWidth() - (layoutX * scale * 1.2)) * 0.5);
		setLayoutY((GUI.getWindowHeight() - (layoutX * scale * 1.2)) * 0.5);
		title.setFont(new Font("Avenir", titleFont * scale));
		title.setPrefHeight(titleHeight * scale);
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
