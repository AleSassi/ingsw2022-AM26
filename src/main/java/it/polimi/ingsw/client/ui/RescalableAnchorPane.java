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
		if (notification.getUserInfo() != null) {
			if (notification.getUserInfo().containsKey("newWidth")) {
				// When we resize the width dimension, the container should not change (stays anchored to the left side of the window)
			} else if (notification.getUserInfo().containsKey("newHeight")) {
				// When we resize the height dimension, the container should rescale to fit into the container
				double newHeight = ((Number) notification.getUserInfo().get("newHeight")).doubleValue();
				double heightScale = newHeight / GUI.referenceHeight;
				double widthScale = GUI.getWindowWidth() / GUI.referenceWidth;
				double scale = Math.min(widthScale, heightScale);
				rescale(scale);
			}
		}
	}
}
