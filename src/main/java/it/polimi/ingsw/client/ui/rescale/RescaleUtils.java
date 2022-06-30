package it.polimi.ingsw.client.ui.rescale;

import it.polimi.ingsw.client.ui.GUI;
import it.polimi.ingsw.notifications.Notification;
import javafx.scene.control.Control;

public class RescaleUtils {
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
