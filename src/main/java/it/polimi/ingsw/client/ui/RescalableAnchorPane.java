package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.notifications.Notification;
import it.polimi.ingsw.notifications.NotificationCenter;
import it.polimi.ingsw.notifications.NotificationName;
import javafx.scene.layout.AnchorPane;

public abstract class RescalableAnchorPane extends AnchorPane implements JavaFXRescalable {
	
	public RescalableAnchorPane() {
		super();
		NotificationCenter.shared().addObserver(this::didReceiveWindowResizeNotification, NotificationName.JavaFXWindowDidResize, null);
	}
	
	private void didReceiveWindowResizeNotification(Notification notification) {
		Double scaleValue = RescaleUtils.rescaleAfterNotification(notification);
		if (scaleValue != null) {
			rescale(scaleValue);
		}
	}
}

class RescaleUtils {
	protected static Double rescaleAfterNotification(Notification notification) {
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
	
	protected static double getScaleValue(double newDimensionSize, boolean isWidth) {
		double newScale = newDimensionSize / (isWidth ? GUI.referenceWidth : GUI.referenceHeight);
		double heightScale = GUI.getWindowHeight() / GUI.referenceHeight;
		double widthScale = GUI.getWindowWidth() / GUI.referenceWidth;
		return Math.min(newScale, isWidth ? heightScale : widthScale);
	}
}
