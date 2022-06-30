package it.polimi.ingsw.client.ui.rescale;

import it.polimi.ingsw.client.ui.GUI;
import it.polimi.ingsw.notifications.Notification;
import javafx.scene.control.Control;

/**
 * Class RescaleUtils is a support class for the resize of the {@code Nodes}
 */
public class RescaleUtils {
	/**
	 * Rescales the {@code Nodes} after the {@link it.polimi.ingsw.notifications.Notification Notification}
	 * @param notification (type Notification)
	 * @return (type double) returns the scale factor
	 */
	public static Double rescaleAfterNotification(Notification notification) {
		if (notification.getUserInfo() != null) {
			if (notification.getUserInfo().containsKey("newWidth")) {
				// When we resize the width dimension, the container should rescale to fit into the container
				double newWidth = ((Number) notification.getUserInfo().get("newWidth")).doubleValue();
				return getScaleValue(newWidth, true);
			} else if (notification.getUserInfo().containsKey("newHeight")) {
				// When we resize the height dimension, the container should rescale to fit into the container
				double newHeight = ((Number) notification.getUserInfo().get("newHeight")).doubleValue();
				return getScaleValue(newHeight, false);
			}
		}
		return null;
	}


	/**
	 * Gets the scale value
	 * @param newDimensionSize (type double)
	 * @param isWidth (type boolean) is true when the value is referred to the width
	 * @return (type double) returns the scale factor
	 */
	public static double getScaleValue(double newDimensionSize, boolean isWidth) {
		double newScale = newDimensionSize / (isWidth ? GUI.referenceWidth : GUI.referenceHeight);
		double heightScale = GUI.getWindowHeight() / GUI.referenceHeight;
		double widthScale = GUI.getWindowWidth() / GUI.referenceWidth;
		return Math.min(newScale, isWidth ? heightScale : widthScale);
	}
	
	public static void rescaleToCenter(Control node, double width, double height, double y, double scale) {
		node.setLayoutX(getCenterX(width, scale));
		node.setLayoutY(y * scale);
		node.setPrefWidth(width * scale);
		node.setPrefHeight(height * scale);
		node.setStyle("-fx-font-size: " + (15 * scale));
	}
	
	private static double getCenterX(double width, double scale) {
		return (GUI.getWindowWidth() - (width * scale)) * 0.5;
	}
}
